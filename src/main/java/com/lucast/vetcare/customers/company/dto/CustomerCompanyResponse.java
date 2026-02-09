package com.lucast.vetcare.customers.company.dto;

import java.time.OffsetDateTime;

public record CustomerCompanyResponse(
        Long id,
        Long tutorId,
        String legalName,
        String tradeName,
        String cnpj,
        String phone,
        String email,
        boolean active,
        CustomerCompanyAddressResponse address,
        CustomerCompanyFiscalResponse fiscal,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
