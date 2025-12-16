package com.lucast.vetcare.clinic.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelAppointmentRequest(
        @NotBlank String reason
) {}
