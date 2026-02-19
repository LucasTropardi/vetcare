package com.lucast.vetcare.auth;

import com.lucast.vetcare.common.enums.Role;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public final class OidcRoleExtractor {

    private static final List<Role> ROLE_PRIORITY = List.of(Role.ADMIN, Role.VET, Role.RECEPTION);

    private OidcRoleExtractor() {
    }

    public static Collection<GrantedAuthority> toAuthorities(Jwt jwt, @Nullable String clientId) {
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : extractRoles(jwt, clientId)) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }
        return authorities;
    }

    public static Set<Role> extractRoles(Jwt jwt, @Nullable String clientId) {
        Set<Role> roles = new LinkedHashSet<>();

        var realmAccess = jwt.getClaim("realm_access");
        if (realmAccess instanceof Map<?, ?> realmMap) {
            addRolesFromUnknownCollection(realmMap.get("roles"), roles);
        }

        var resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess instanceof Map<?, ?> resourceMap) {
            if (clientId != null && !clientId.isBlank()) {
                addRolesFromResourceEntry(resourceMap.get(clientId), roles);
            } else {
                for (Object entry : resourceMap.values()) {
                    addRolesFromResourceEntry(entry, roles);
                }
            }
        }

        addRolesFromUnknownCollection(jwt.getClaim("roles"), roles);

        return roles;
    }

    @Nullable
    public static Role resolvePrimaryRoleOrNull(Authentication authentication) {
        for (Role role : ROLE_PRIORITY) {
            String authority = "ROLE_" + role.name();
            boolean present = authentication.getAuthorities().stream()
                    .anyMatch(granted -> authority.equals(granted.getAuthority()));
            if (present) {
                return role;
            }
        }
        return null;
    }

    public static Role resolvePrimaryRole(Authentication authentication) {
        Role role = resolvePrimaryRoleOrNull(authentication);
        if (role != null) {
            return role;
        }
        throw new ResponseStatusException(UNAUTHORIZED, "Token does not contain a supported role");
    }

    private static void addRolesFromResourceEntry(Object resourceEntry, Set<Role> roles) {
        if (resourceEntry instanceof Map<?, ?> map) {
            addRolesFromUnknownCollection(map.get("roles"), roles);
        }
    }

    private static void addRolesFromUnknownCollection(Object rawRoles, Set<Role> roles) {
        if (!(rawRoles instanceof Collection<?> collection)) {
            return;
        }

        for (Object entry : collection) {
            if (!(entry instanceof String roleValue)) {
                continue;
            }

            String normalized = roleValue.replace("ROLE_", "").toUpperCase();
            try {
                Role role = Role.valueOf(normalized);
                if (ROLE_PRIORITY.contains(role)) {
                    roles.add(role);
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
    }
}
