package com.lucast.vetcare.company.dto;

import java.time.OffsetDateTime;

public record CompanyProfileResponse(
        Long id,
        String legalName,
        String tradeName,
        String cnpj,
        String phone,
        String email,
        boolean headquarter,
        Long parentCompanyId,
        CompanyAddressResponse address,
        CompanyFiscalConfigResponse fiscalConfig,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
