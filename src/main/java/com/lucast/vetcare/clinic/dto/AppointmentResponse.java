package com.lucast.vetcare.clinic.dto;

import com.lucast.vetcare.common.enums.AppointmentStatus;

import java.time.OffsetDateTime;

public record AppointmentResponse(
        Long id,
        Long petId,
        Long veterinarianUserId,
        AppointmentStatus status,
        OffsetDateTime openedAt,
        OffsetDateTime finishedAt,
        OffsetDateTime canceledAt,
        String cancelReason,
        Long createdBy,
        Long finishedBy,
        Long canceledBy,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
