package com.lucast.vetcare.clinic.dto;

public record PrescriptionItemResponse(
        Long id,
        String medicationName,
        String dosage,
        String frequency,
        String duration,
        String route,
        String notes
) {}
