package com.lucast.vetcare.customers.tutor.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateTutorRequest(
        @NotBlank @Size(max = 160) String name,
        @Size(max = 14) String document,
        @Size(max = 30) String phone,
        @Size(max = 160) String email,
        @Valid TutorAddressRequest address
) {}
