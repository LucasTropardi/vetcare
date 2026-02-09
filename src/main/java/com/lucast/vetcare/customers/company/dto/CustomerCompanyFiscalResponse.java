package com.lucast.vetcare.customers.company.dto;

import com.lucast.vetcare.common.enums.IeIndicator;

import java.time.OffsetDateTime;

public record CustomerCompanyFiscalResponse(
        Long customerCompanyId,
        String ie,
        IeIndicator ieIndicator,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
