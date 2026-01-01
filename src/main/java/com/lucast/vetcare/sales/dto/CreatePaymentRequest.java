package com.lucast.vetcare.sales.dto;

import com.lucast.vetcare.common.enums.PaymentMethod;
import com.lucast.vetcare.common.enums.PaymentStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreatePaymentRequest(
        @NotNull PaymentMethod method,
        @NotNull @DecimalMin("0.01") BigDecimal amount,
        PaymentStatus status,
        String notes
) {}
