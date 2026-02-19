package com.lucast.vetcare.auth;

import com.lucast.vetcare.auth.dto.LoginRequest;
import com.lucast.vetcare.auth.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Authentication",
        description = "Authentication and authorization endpoints"
)
public class AuthController {

    @Operation(
            summary = "Legacy user login (deprecated)",
            description = "Deprecated endpoint. Authentication now uses OIDC Authorization Code + PKCE on the client"
    )
    @PostMapping("/login")
    @Deprecated(forRemoval = true)
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        throw new ResponseStatusException(
                HttpStatus.GONE,
                "Deprecated endpoint. Use OIDC Authorization Code + PKCE with an external identity provider"
        );
    }
}
