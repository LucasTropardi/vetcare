package com.lucast.vetcare.clinic.dto;

import java.time.OffsetDateTime;

public record ProcedureResponse(
        Long id,
        Long medicalRecordId,
        String description,
        String notes,
        OffsetDateTime performedAt,
        OffsetDateTime createdAt
) {}
