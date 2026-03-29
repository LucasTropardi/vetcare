package com.lucast.vetcare.cashregister.dto;

import com.lucast.vetcare.common.enums.CashRegisterStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CashRegisterResponse(
        Long id,
        Long companyId,
        String registerCode,
        CashRegisterStatus status,
        BigDecimal openingAmount,
        BigDecimal expectedClosingAmount,
        BigDecimal closingAmount,
        Long openedBy,
        OffsetDateTime openedAt,
        Long closedBy,
        OffsetDateTime closedAt,
        String notes,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
