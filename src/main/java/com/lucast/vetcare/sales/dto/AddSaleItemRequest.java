package com.lucast.vetcare.sales.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddSaleItemRequest(
        @NotNull Long productId,
        @NotNull @DecimalMin("0.001") BigDecimal quantity,
        BigDecimal unitPrice // se null, usa Product.salePrice
) {}
