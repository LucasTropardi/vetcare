package com.lucast.vetcare.customers.company.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateCustomerCompanyRequest(
        @NotNull Long tutorId,
        @NotBlank @Size(max = 200) String legalName,
        @Size(max = 200) String tradeName,
        @NotBlank @Size(max = 14) String cnpj,
        @Size(max = 30) String phone,
        @Size(max = 160) String email,
        @Valid CustomerCompanyAddressRequest address,
        @Valid CustomerCompanyFiscalRequest fiscal
) {}
