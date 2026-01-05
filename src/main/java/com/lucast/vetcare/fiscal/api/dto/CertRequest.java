package com.lucast.vetcare.fiscal.api.dto;

import jakarta.validation.constraints.NotBlank;

public record CertRequest(
        @NotBlank String certBase64,
        @NotBlank String certPassword
) {}
