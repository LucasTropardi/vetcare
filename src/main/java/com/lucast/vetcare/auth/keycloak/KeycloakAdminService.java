package com.lucast.vetcare.auth.keycloak;

import com.lucast.vetcare.common.enums.Role;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_GATEWAY;

@Service
public class KeycloakAdminService {

    private static final Set<String> MANAGED_ROLES = Set.of("ADMIN", "VET", "RECEPTION");

    private final KeycloakAdminProperties properties;
    private final RestClient restClient;

    public KeycloakAdminService(KeycloakAdminProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder().baseUrl(properties.getBaseUrl()).build();
    }

    public void ensureUser(String email,
                           String name,
                           boolean enabled,
                           @Nullable String password,
                           Role role) {
        if (!properties.isEnabled()) {
            return;
        }

        try {
            String token = getAdminAccessToken();
            String userId = findUserId(token, email);

            if (userId == null) {
                userId = createUser(token, email, name, enabled);
            } else {
                updateUser(token, userId, email, name, enabled);
            }

            if (password != null && !password.isBlank()) {
                resetPassword(token, userId, password);
            }

            replaceManagedRealmRoles(token, userId, role.name());
        } catch (RestClientResponseException e) {
            String msg = e.getResponseBodyAsString();
            throw new ResponseStatusException(BAD_GATEWAY, "Keycloak user sync failed: " + msg, e);
        } catch (Exception e) {
            throw new ResponseStatusException(BAD_GATEWAY, "Keycloak user sync failed", e);
        }
    }

    public void updatePassword(String email, String password) {
        if (!properties.isEnabled() || password == null || password.isBlank()) {
            return;
        }

        try {
            String token = getAdminAccessToken();
            String userId = findUserId(token, email);
            if (userId == null) {
                throw new ResponseStatusException(BAD_GATEWAY, "Keycloak user not found for password update");
            }
            resetPassword(token, userId, password);
        } catch (RestClientResponseException e) {
            String msg = e.getResponseBodyAsString();
            throw new ResponseStatusException(BAD_GATEWAY, "Keycloak password update failed: " + msg, e);
        }
    }

    private String getAdminAccessToken() {
        var form = new LinkedMultiValueMap<String, String>();
        form.add("grant_type", "password");
        form.add("client_id", properties.getClientId());
        form.add("username", properties.getUsername());
        form.add("password", properties.getPassword());
        if (properties.getClientSecret() != null && !properties.getClientSecret().isBlank()) {
            form.add("client_secret", properties.getClientSecret());
        }

        var token = restClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", properties.getAdminRealm())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(form)
                .retrieve()
                .body(TokenResponse.class);

        if (token == null || token.access_token() == null || token.access_token().isBlank()) {
            throw new ResponseStatusException(BAD_GATEWAY, "Keycloak token endpoint returned no access_token");
        }

        return token.access_token();
    }

    @Nullable
    private String findUserId(String token, String email) {
        String normalizedEmail = email.trim();

        List<Map<String, Object>> byUsername = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/admin/realms/{realm}/users")
                        .queryParam("username", normalizedEmail)
                        .queryParam("exact", true)
                        .build(properties.getRealm()))
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        String id = extractUserId(byUsername);
        if (id != null) {
            return id;
        }

        List<Map<String, Object>> byEmail = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/admin/realms/{realm}/users")
                        .queryParam("email", normalizedEmail)
                        .queryParam("exact", true)
                        .build(properties.getRealm()))
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        return extractUserId(byEmail);
    }

    private String createUser(String token, String email, String name, boolean enabled) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", email);
        payload.put("email", email);
        payload.put("enabled", enabled);
        payload.put("emailVerified", true);
        payload.put("firstName", name);

        restClient.post()
                .uri("/admin/realms/{realm}/users", properties.getRealm())
                .headers(h -> h.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();

        String userId = findUserId(token, email);
        if (userId == null) {
            throw new ResponseStatusException(BAD_GATEWAY, "Keycloak user created but could not be fetched");
        }
        return userId;
    }

    private void updateUser(String token, String userId, String email, String name, boolean enabled) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("username", email);
        payload.put("email", email);
        payload.put("enabled", enabled);
        payload.put("emailVerified", true);
        payload.put("firstName", name);

        restClient.put()
                .uri("/admin/realms/{realm}/users/{userId}", properties.getRealm(), userId)
                .headers(h -> h.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .body(payload)
                .retrieve()
                .toBodilessEntity();
    }

    private void resetPassword(String token, String userId, String password) {
        Map<String, Object> credential = new HashMap<>();
        credential.put("type", "password");
        credential.put("temporary", false);
        credential.put("value", password);

        restClient.put()
                .uri("/admin/realms/{realm}/users/{userId}/reset-password", properties.getRealm(), userId)
                .headers(h -> h.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .body(credential)
                .retrieve()
                .toBodilessEntity();
    }

    private void replaceManagedRealmRoles(String token, String userId, String targetRole) {
        List<RoleRepresentation> currentRoles = restClient.get()
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", properties.getRealm(), userId)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });

        List<RoleRepresentation> toRemove = new ArrayList<>();
        if (currentRoles != null) {
            for (RoleRepresentation role : currentRoles) {
                if (role != null && role.name() != null && MANAGED_ROLES.contains(role.name())) {
                    toRemove.add(role);
                }
            }
        }

        if (!toRemove.isEmpty()) {
            restClient.method(org.springframework.http.HttpMethod.DELETE)
                    .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", properties.getRealm(), userId)
                    .headers(h -> h.setBearerAuth(token))
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(toRemove)
                    .retrieve()
                    .toBodilessEntity();
        }

        RoleRepresentation target = restClient.get()
                .uri("/admin/realms/{realm}/roles/{roleName}", properties.getRealm(), targetRole)
                .headers(h -> h.setBearerAuth(token))
                .retrieve()
                .body(RoleRepresentation.class);

        if (target == null) {
            throw new ResponseStatusException(BAD_GATEWAY, "Target Keycloak role not found: " + targetRole);
        }

        restClient.post()
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", properties.getRealm(), userId)
                .headers(h -> h.setBearerAuth(token))
                .contentType(MediaType.APPLICATION_JSON)
                .body(List.of(target))
                .retrieve()
                .toBodilessEntity();
    }

    @Nullable
    private String extractUserId(@Nullable List<Map<String, Object>> users) {
        if (users == null || users.isEmpty()) {
            return null;
        }

        Object id = users.get(0).get("id");
        if (id instanceof String value && !value.isBlank()) {
            return value;
        }
        return null;
    }

    private record TokenResponse(String access_token) {
    }

    private record RoleRepresentation(String id, String name) {
    }
}
