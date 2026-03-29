package com.lucast.vetcare.sales.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record FinalizeSaleRequest(
        Long tutorId,
        Long customerCompanyId,
        Boolean clearRecipient,
        String notes,
        @NotEmpty List<@Valid CheckoutPaymentRequest> payments
) {}
