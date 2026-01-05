package com.lucast.vetcare.fiscal.api.dto;

import jakarta.validation.constraints.NotBlank;

public record NfceSignRequest(
        @NotBlank String xml,
        @NotBlank String certBase64,
        @NotBlank String certPassword,
        @NotBlank String tipoAssinatura
) {}
