package com.lucast.vetcare.customers.tutor.dto;

import jakarta.validation.constraints.Size;

public record TutorAddressRequest(
        @Size(max = 8) String zipCode,
        @Size(max = 160) String street,
        @Size(max = 30) String number,
        @Size(max = 120) String complement,
        @Size(max = 120) String neighborhood,
        @Size(max = 120) String cityName,
        @Size(max = 7) String cityIbge,
        @Size(max = 2) String stateUf,
        @Size(max = 60) String country
) {}
