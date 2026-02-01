package com.lucast.vetcare.auth;

import com.lucast.vetcare.auth.dto.LoginRequest;
import com.lucast.vetcare.auth.dto.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Authentication",
        description = "Authentication and authorization endpoints"
)
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Operation(
            summary = "User login",
            description = "Authenticate user using email and password and return a JWT access token"
    )
    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        var user = userRepository.findByEmail(request.email())
                .filter(UserEntity::isActive)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
        }

        return new LoginResponse(jwtService.generateAccessToken(user), "Bearer");
    }
}
