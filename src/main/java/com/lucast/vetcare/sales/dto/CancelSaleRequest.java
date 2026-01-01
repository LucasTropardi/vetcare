package com.lucast.vetcare.sales.dto;

import jakarta.validation.constraints.NotBlank;

public record CancelSaleRequest(
        @NotBlank String reason
) {}
