package com.lucast.vetcare.sales.dto;

import com.lucast.vetcare.common.enums.ItemType;

import java.math.BigDecimal;

public record SaleReceiptItemResponse(
        Long id,
        int lineNumber,
        Long productId,
        ItemType itemType,
        String description,
        String unit,
        String ncm,
        String cfop,
        BigDecimal quantity,
        BigDecimal unitPrice,
        BigDecimal total,
        BigDecimal estimatedTax
) {}
