package com.lucast.vetcare.customers.pet.dto;

import com.lucast.vetcare.common.enums.PetSex;
import com.lucast.vetcare.common.enums.Species;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CreatePetRequest(
        @NotNull Long tutorId,
        @NotBlank @Size(max = 120) String name,
        @NotNull Species species,
        @Size(max = 120) String breed,
        PetSex sex,
        LocalDate birthDate,
        BigDecimal weightKg,
        String notes
) {}
