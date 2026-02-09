package com.lucast.vetcare.sales.dto;

public record UpdateSaleRequest(
        Long tutorId,
        Long customerCompanyId,
        boolean clearRecipient,
        String notes
) {}
