package com.lucast.vetcare.customers.pet.dto;

import com.lucast.vetcare.common.enums.Species;

public record PetListItemResponse(
        Long id,
        Long tutorId,
        String name,
        Species species,
        boolean active
) {}
