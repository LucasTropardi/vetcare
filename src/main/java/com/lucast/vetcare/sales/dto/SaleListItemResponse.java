package com.lucast.vetcare.sales.dto;

import com.lucast.vetcare.common.enums.SaleStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SaleListItemResponse(
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
        Integer itemCount,
        String customerName,
        String customerDocument,
        String registerCode,
        Long saleNumber,
        String documentNumber,
        String protocol,
        Boolean canCancel,
        Boolean xmlAvailable,
        Boolean receiptAvailable
) {
}
