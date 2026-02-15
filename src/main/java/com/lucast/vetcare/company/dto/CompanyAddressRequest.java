package com.lucast.vetcare.company.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CompanyAddressRequest(
        @NotBlank @Size(max = 8) String zipCode,
        @NotBlank @Size(max = 160) String street,
        @Size(max = 30) String number,
        @Size(max = 120) String complement,
        @Size(max = 120) String neighborhood,
        @NotBlank @Size(max = 120) String cityName,
        @Size(max = 7) String cityIbge,
        @NotBlank @Size(max = 2) String stateUf,
        @Size(max = 60) String country
) {}
