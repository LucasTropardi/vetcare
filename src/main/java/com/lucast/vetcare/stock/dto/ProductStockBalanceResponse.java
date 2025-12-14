package com.lucast.vetcare.stock.dto;

import java.math.BigDecimal;

public record ProductStockBalanceResponse(
        Long productId,
        BigDecimal onHand,
        BigDecimal avgCost
) {}
