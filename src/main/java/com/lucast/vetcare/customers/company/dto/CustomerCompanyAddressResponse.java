package com.lucast.vetcare.customers.company.dto;

import java.time.OffsetDateTime;

public record CustomerCompanyAddressResponse(
        Long customerCompanyId,
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
