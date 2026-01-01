package com.lucast.vetcare.sales.dto;

import com.lucast.vetcare.common.enums.ItemType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record SaleItemResponse(
        Long id,
        Long productId,
        ItemType itemType,
        String description,
        String unit,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal total,
        OffsetDateTime createdAt
) {}
