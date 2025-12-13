package com.lucast.vetcare.catalog.dto;

import com.lucast.vetcare.common.enums.ItemType;
import com.lucast.vetcare.common.enums.ProductCategory;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String sku,
        String name,
        ItemType itemType,
        ProductCategory category,
        String unit,
        boolean active,
        BigDecimal salePrice,
        BigDecimal costPrice,
        BigDecimal minStock,

        ProductFiscalResponse fiscal
) {}
