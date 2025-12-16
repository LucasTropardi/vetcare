package com.lucast.vetcare.clinic.dto;

import java.time.OffsetDateTime;

public record MedicalRecordResponse(
        Long id,
        Long appointmentId,
        String chiefComplaint,
        String clinicalNotes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
