package com.lucast.vetcare.clinic.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.OffsetDateTime;

public record AddProcedureRequest(
        @NotBlank String description,
        String notes,
        OffsetDateTime performedAt
) {}
