package com.lucast.vetcare.clinic.dto;

import jakarta.validation.constraints.NotNull;

public record OpenAppointmentRequest(
        @NotNull Long petId,
        Long veterinarianUserId,
        String chiefComplaint
) {}
