package com.lucast.vetcare.cashregister.occurrence.dto;

import com.lucast.vetcare.common.enums.CashRegisterOccurrenceType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CashRegisterOccurrenceResponse(
        Long id,
        Long cashRegisterId,
        CashRegisterOccurrenceType eventType,
        BigDecimal amount,
        String description,
        Long performedBy,
        Long approvedBy,
        OffsetDateTime createdAt
) {
}
