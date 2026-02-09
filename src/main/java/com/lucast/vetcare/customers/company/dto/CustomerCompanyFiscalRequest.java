package com.lucast.vetcare.customers.company.dto;

import com.lucast.vetcare.common.enums.IeIndicator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CustomerCompanyFiscalRequest(
        @Size(max = 20) String ie,
        @NotNull IeIndicator ieIndicator
) {}
