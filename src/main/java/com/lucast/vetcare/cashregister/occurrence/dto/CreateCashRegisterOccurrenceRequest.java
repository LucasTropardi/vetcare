package com.lucast.vetcare.cashregister.occurrence.dto;

import com.lucast.vetcare.common.enums.CashRegisterOccurrenceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateCashRegisterOccurrenceRequest(
        @NotNull CashRegisterOccurrenceType eventType,
        @NotNull @DecimalMin("0.00") BigDecimal amount,
        String description,
        Long approvedBy
) {
}
