package com.lucast.vetcare.sales.dto;

import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;

public record UpdateSaleItemRequest(
        @DecimalMin("0.001") BigDecimal quantity,
        @DecimalMin("0.00") BigDecimal unitPrice
) {}
