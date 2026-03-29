package com.lucast.vetcare.catalog.dto;

import com.lucast.vetcare.common.enums.ItemType;

import java.math.BigDecimal;

public record ProductPosLookupDTO(
        Long id,
        String sku,
        String name,
        String unit,
        ItemType itemType,
        boolean active,
        BigDecimal salePrice,
        String gtinEan,
        String gtinEanTrib
) {}
