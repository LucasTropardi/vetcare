package com.lucast.vetcare.fiscal.api.dto;

import jakarta.validation.constraints.NotBlank;

public record NfceValidateRequest(
        @NotBlank String xml,
        @NotBlank String servico
) {}
