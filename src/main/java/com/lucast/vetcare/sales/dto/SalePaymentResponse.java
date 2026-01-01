package com.lucast.vetcare.sales.dto;

import com.lucast.vetcare.common.enums.PaymentMethod;
import com.lucast.vetcare.common.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SalePaymentResponse(
        Long id,
        PaymentMethod method,
        PaymentStatus status,
        BigDecimal amount,
        OffsetDateTime paidAt,
        Long createdBy,
        OffsetDateTime createdAt,
        String notes
) {}
