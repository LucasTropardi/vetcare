package com.lucast.vetcare.auth;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

public final class AuthContext {

    private AuthContext() {}

    public static UserEntity requireUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getPrincipal() == null || !(auth.getPrincipal() instanceof UserEntity user)) {
            throw new ResponseStatusException(UNAUTHORIZED, "Unauthorized");
        }
        return user;
    }

    public static Long requireUserId() {
        return requireUser().getId();
    }
}
