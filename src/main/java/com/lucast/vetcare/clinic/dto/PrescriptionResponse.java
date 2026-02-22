package com.lucast.vetcare.clinic.dto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public record PrescriptionResponse(
        Long id,
        Long appointmentId,
        Long veterinarianUserId,
        String title,
        String guidance,
        LocalDate validUntil,
        OffsetDateTime createdAt,
        List<PrescriptionItemResponse> items
) {}
