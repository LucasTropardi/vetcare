package com.lucast.vetcare.sales.dto;

import com.lucast.vetcare.common.enums.SaleStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record SaleResponse(
        Long id,
        Long companyId,
        Long tutorId,
        Long customerCompanyId,
        Long appointmentId,
        SaleStatus status,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal total,
        BigDecimal paidTotal,
        BigDecimal remaining,
        String notes,
        Long createdBy,
        Long confirmedBy,
        OffsetDateTime confirmedAt,
        Long canceledBy,
        OffsetDateTime canceledAt,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<SaleItemResponse> items,
        List<SalePaymentResponse> payments
) {}
