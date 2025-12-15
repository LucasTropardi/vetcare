package com.lucast.vetcare.stock.dto;

import java.math.BigDecimal;

public record ProductStockBalanceListDTO(
        Long productId,
        String sku,
        String name,
        BigDecimal onHand,
        BigDecimal avgCost,
        BigDecimal minStock,
        boolean belowMinStock
) {}
