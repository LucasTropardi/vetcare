package com.lucast.vetcare.company.dto;

public record CompanyAddressResponse(
        Long companyId,
        String zipCode,
        String street,
        String number,
        String complement,
        String neighborhood,
        String cityName,
        String cityIbge,
        String stateUf,
        String country
) {}
