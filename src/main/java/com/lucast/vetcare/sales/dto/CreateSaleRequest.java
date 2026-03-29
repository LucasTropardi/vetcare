package com.lucast.vetcare.sales.dto;

import jakarta.validation.constraints.NotNull;

public record CreateSaleRequest(
        @NotNull Long companyId,
        Long tutorId,
        Long customerCompanyId,
        Long appointmentId,
        Long cashRegisterId,
        String notes
) {}
