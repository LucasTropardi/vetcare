package com.lucast.vetcare.auth;

import com.lucast.vetcare.common.enums.Role;
import com.lucast.vetcare.config.SpringContextHolder;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public final class AuthContext {

    private AuthContext() {
    }

    public static UserEntity requireUser() {
        Authentication authentication = requireAuthentication();
        Jwt jwt = requireJwt(authentication);

        String subject = firstNonBlank(
                jwt.getSubject(),
                jwt.getClaimAsString("sub"),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("email")
        );

        String email = firstNonBlank(
                jwt.getClaimAsString("email"),
                jwt.getClaimAsString("preferred_username")
        );

        if (subject == null && email == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Token does not contain identifiable user claims");
        }

        // Some providers/clients do not send email by default.
        // Keep a stable synthetic identifier to avoid login loop on /api/users/me.
        String userKey = email != null ? email : ("oidc-" + subject + "@local");

        Role tokenRole = OidcRoleExtractor.resolvePrimaryRoleOrNull(authentication);
        UserRepository repository = SpringContextHolder.getBean(UserRepository.class);

        var existing = repository.findByEmailIgnoreCase(userKey).orElse(null);
        if (existing != null) {
            boolean changed = false;

            if (tokenRole != null && existing.getRole() != tokenRole) {
                existing.setRole(tokenRole);
                changed = true;
            }

            if (!existing.isActive()) {
                existing.setActive(true);
                changed = true;
            }

            String resolvedName = resolveName(jwt, userKey);
            if (!resolvedName.equals(existing.getName())) {
                existing.setName(resolvedName);
                changed = true;
            }

            if (changed) {
                existing.setUpdatedAt(OffsetDateTime.now());
                existing = repository.save(existing);
            }

            return existing;
        }

        UserEntity created = new UserEntity();
        created.setName(resolveName(jwt, userKey));
        created.setEmail(userKey);
        created.setPasswordHash("OIDC_EXTERNAL_ACCOUNT");
        created.setRole(tokenRole != null ? tokenRole : Role.RECEPTION);
        created.setActive(true);
        created.setCreatedAt(OffsetDateTime.now());
        created.setUpdatedAt(OffsetDateTime.now());
        return repository.save(created);
    }

    public static Long requireUserId() {
        return requireUser().getId();
    }

    public static Role requireRole() {
        Authentication authentication = requireAuthentication();
        Role tokenRole = OidcRoleExtractor.resolvePrimaryRoleOrNull(authentication);
        if (tokenRole != null) {
            return tokenRole;
        }

        UserEntity user = requireUser();
        return user.getRole();
    }

    public static String requireSubject() {
        Jwt jwt = requireJwt(requireAuthentication());
        String subject = firstNonBlank(
                jwt.getSubject(),
                jwt.getClaimAsString("sub"),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("email")
        );
        if (subject == null) {
            throw new ResponseStatusException(UNAUTHORIZED, "Token does not contain subject/sub");
        }
        return subject;
    }

    private static Authentication requireAuthentication() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !auth.isAuthenticated()) {
            throw new ResponseStatusException(UNAUTHORIZED, "Unauthorized");
        }
        return auth;
    }

    private static Jwt requireJwt(Authentication auth) {
        Object principal = auth.getPrincipal();
        if (principal instanceof Jwt jwt) {
            return jwt;
        }
        throw new ResponseStatusException(UNAUTHORIZED, "Unauthorized");
    }

    private static String resolveName(Jwt jwt, String fallback) {
        String name = firstNonBlank(
                jwt.getClaimAsString("name"),
                jwt.getClaimAsString("preferred_username"),
                jwt.getClaimAsString("given_name")
        );
        return name != null ? name : fallback;
    }

    @Nullable
    private static String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }
}
