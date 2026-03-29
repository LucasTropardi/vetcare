package com.lucast.vetcare.sales.dto;

import com.lucast.vetcare.common.enums.PaymentMethod;
import com.lucast.vetcare.common.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SaleReceiptPaymentResponse(
        Long id,
        PaymentMethod method,
        PaymentStatus status,
        BigDecimal amount,
        String notes,
        OffsetDateTime paidAt
) {}
