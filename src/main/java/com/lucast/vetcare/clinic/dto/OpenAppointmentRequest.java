package com.lucast.vetcare.clinic.dto;

import com.lucast.vetcare.common.enums.AppointmentType;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record OpenAppointmentRequest(
        @NotNull Long petId,
        AppointmentType appointmentType,
        Long veterinarianUserId,
        Long serviceProductId,
        OffsetDateTime scheduledStartAt,
        OffsetDateTime scheduledEndAt,
        String notes,
        String chiefComplaint
) {}
