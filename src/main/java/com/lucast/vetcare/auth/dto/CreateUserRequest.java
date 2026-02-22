package com.lucast.vetcare.auth.dto;

import com.lucast.vetcare.common.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateUserRequest(
        @NotBlank String name,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotBlank String confirmPassword,
        @NotNull Role role,
        String professionalLicense,
        String signatureImageBase64,
        String signatureImageContentType
) {}
