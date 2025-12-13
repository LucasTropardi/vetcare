package com.lucast.vetcare.catalog.dto;

import java.math.BigDecimal;

public record ProductListDTO(
        Long id,
        String sku,
        String name,
        String category,
        String unit,
        Boolean active,
        BigDecimal salePrice,
        BigDecimal costPrice,
        BigDecimal minStock
) {}
