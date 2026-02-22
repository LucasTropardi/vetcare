package com.lucast.vetcare.clinic.dto;

import java.time.OffsetDateTime;

public record PetshopRecordResponse(
        Long id,
        Long appointmentId,
        Long attendedByUserId,
        String serviceReport,
        String productsUsed,
        String checkinNotes,
        String checkoutNotes,
        OffsetDateTime startedAt,
        OffsetDateTime finishedAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
