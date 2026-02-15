package com.lucast.vetcare.company.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record UpdateCompanyProfileRequest(
        @NotBlank @Size(max = 200) String legalName,
        @Size(max = 200) String tradeName,
        @NotBlank @Size(max = 14) String cnpj,
        @Size(max = 30) String phone,
        @Size(max = 160) String email,
        @NotNull Boolean headquarter,
        @Valid CompanyAddressRequest address,
        @Valid CompanyFiscalConfigRequest fiscalConfig
) {}
