package com.lucast.vetcare.customers.tutor.dto;

import java.time.OffsetDateTime;

public record TutorResponse(
        Long id,
        String name,
        String document,
        String phone,
        String email,
        boolean active,
        TutorAddressResponse address,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
