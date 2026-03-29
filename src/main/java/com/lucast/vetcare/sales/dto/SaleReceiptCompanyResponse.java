package com.lucast.vetcare.sales.dto;

public record SaleReceiptCompanyResponse(
        Long id,
        String displayName,
        String legalName,
        String cnpj,
        String phone
) {}
