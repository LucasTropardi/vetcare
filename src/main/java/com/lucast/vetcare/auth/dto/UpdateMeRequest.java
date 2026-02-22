package com.lucast.vetcare.auth.dto;

import jakarta.validation.constraints.Email;

public record UpdateMeRequest(
        String name,
        @Email String email,
        String password,
        String professionalLicense,
        String signatureImageBase64,
        String signatureImageContentType
) {}
