package com.lucast.vetcare.customers.tutor.dto;

public record TutorStatsResponse (
        long total,
        long active,
        long inactive,
        long withCompany,
        long withPet,
        long withoutContact
) {}