package com.lucast.vetcare.clinic.dto;

import jakarta.validation.constraints.NotBlank;

public record AddDiagnosisRequest(
        String code,
        @NotBlank String description,
        Boolean primary
) {}
