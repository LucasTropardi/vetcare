package com.lucast.vetcare.sales.dto;

public record SaleReceiptCustomerResponse(
        boolean identified,
        String name,
        String document
) {}
