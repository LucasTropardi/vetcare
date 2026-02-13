package com.lucast.vetcare.customers.pet.dto;

public record PetStatsResponse(
        long total,
        long active,
        long inactive,
        long dogs,
        long cats,
        long others
) {}
