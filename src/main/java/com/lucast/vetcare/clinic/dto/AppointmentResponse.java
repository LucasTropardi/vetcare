package com.lucast.vetcare.clinic.dto;

import com.lucast.vetcare.common.enums.AppointmentStatus;
import com.lucast.vetcare.common.enums.AppointmentType;

import java.time.OffsetDateTime;

public record AppointmentResponse(
        Long id,
        Long petId,
        AppointmentType appointmentType,
        Long veterinarianUserId,
        Long serviceProductId,
        AppointmentStatus status,
        OffsetDateTime scheduledStartAt,
        OffsetDateTime scheduledEndAt,
        String notes,
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
