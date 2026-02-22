package com.lucast.vetcare.clinic.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.time.LocalDate;
import java.util.List;

public record CreatePrescriptionRequest(
        String title,
        String guidance,
        LocalDate validUntil,
        @NotEmpty List<@Valid CreatePrescriptionItemRequest> items
) {}
