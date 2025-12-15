package com.lucast.vetcare.customers.tutor.dto;

public record TutorListItemResponse(
        Long id,
        String name,
        String document,
        String phone,
        String email,
        boolean active
) {}
