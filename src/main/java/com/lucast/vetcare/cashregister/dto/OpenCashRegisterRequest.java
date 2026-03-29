package com.lucast.vetcare.cashregister.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record OpenCashRegisterRequest(
        @NotNull Long companyId,
        @NotBlank String registerCode,
        @NotNull @DecimalMin("0.00") BigDecimal openingAmount,
        @DecimalMin("0.00") BigDecimal expectedClosingAmount,
        String notes
) {
}
