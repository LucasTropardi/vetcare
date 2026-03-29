package com.lucast.vetcare.sales.dto;

public record UpdateSaleRequest(
        Long tutorId,
        Long customerCompanyId,
        Boolean clearRecipient,
        String notes
) {}
