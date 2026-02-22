package com.lucast.vetcare.clinic.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePrescriptionItemRequest(
        @NotBlank String medicationName,
        String dosage,
        String frequency,
        String duration,
        String route,
        String notes
) {}
