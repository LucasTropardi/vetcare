package com.lucast.vetcare.customers.pet.dto;

import com.lucast.vetcare.common.enums.PetSex;
import com.lucast.vetcare.common.enums.Species;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;

public record PetResponse(
        Long id,
        Long tutorId,
        String name,
        Species species,
        String breed,
        PetSex sex,
        LocalDate birthDate,
        BigDecimal weightKg,
        String notes,
        boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
