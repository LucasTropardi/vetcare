package com.lucast.vetcare.customers.tutor.dto;

import java.time.OffsetDateTime;

public record TutorAddressResponse(
        Long tutorId,
        String zipCode,
        String street,
        String number,
        String complement,
        String neighborhood,
        String cityName,
        String cityIbge,
        String stateUf,
        String country,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
