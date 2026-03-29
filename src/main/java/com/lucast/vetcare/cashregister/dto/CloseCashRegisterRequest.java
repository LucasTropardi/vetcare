package com.lucast.vetcare.cashregister.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CloseCashRegisterRequest(
        @NotNull @DecimalMin("0.00") BigDecimal closingAmount,
        String notes
) {
}
