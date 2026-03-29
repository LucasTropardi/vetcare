package com.lucast.vetcare.sales.dto;

public record FinalizeSaleResponse(
        SaleResponse sale,
        SaleReceiptResponse receipt
) {}
