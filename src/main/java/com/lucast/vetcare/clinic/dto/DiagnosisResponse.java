package com.lucast.vetcare.clinic.dto;

import java.time.OffsetDateTime;

public record DiagnosisResponse(
        Long id,
        Long medicalRecordId,
        String code,
        String description,
        boolean primary,
        OffsetDateTime createdAt
) {}
