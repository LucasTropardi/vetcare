package com.lucast.vetcare.sales.dto;

import java.math.BigDecimal;

public record SaleReceiptTaxBreakdownResponse(
        BigDecimal federal,
        BigDecimal state,
        BigDecimal municipal,
        BigDecimal total
) {}
