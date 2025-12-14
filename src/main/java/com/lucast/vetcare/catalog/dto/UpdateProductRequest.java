package com.lucast.vetcare.catalog.dto;

import com.lucast.vetcare.common.enums.ItemType;
import com.lucast.vetcare.common.enums.ProductCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpdateProductRequest(
        String sku,
        String name,
        ItemType itemType,
        ProductCategory category,
        String unit,
        BigDecimal salePrice,
        BigDecimal costPrice,
        BigDecimal minStock,
        @Valid ProductFiscalRequest fiscal
) {}
