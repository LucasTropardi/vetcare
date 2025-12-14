package com.lucast.vetcare.stock.dto;

import com.lucast.vetcare.common.enums.StockMovementType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record StockMovementResponse(
        Long id,
        Long productId,
        StockMovementType movementType,
        BigDecimal quantity,
        BigDecimal unitCost,
        String notes,
        String referenceType,
        Long referenceId,
        Long createdBy,
        OffsetDateTime createdAt
) {}
