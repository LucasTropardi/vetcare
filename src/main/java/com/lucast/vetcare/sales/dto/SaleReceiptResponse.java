package com.lucast.vetcare.sales.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record SaleReceiptResponse(
        Long saleId,
        Long saleNumber,
        String registerCode,
        String environment,
        String notice,
        String documentLabel,
        String documentNumber,
        String series,
        String accessKey,
        String protocol,
        OffsetDateTime issuedAt,
        SaleReceiptCompanyResponse company,
        SaleReceiptCustomerResponse customer,
        List<SaleReceiptItemResponse> items,
        List<SaleReceiptPaymentResponse> payments,
        BigDecimal subtotal,
        BigDecimal discount,
        BigDecimal total,
        SaleReceiptTaxBreakdownResponse estimatedTaxes,
        BigDecimal receivedTotal,
        BigDecimal change
) {}
