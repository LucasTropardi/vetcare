package com.lucast.vetcare.company.dto;

import com.lucast.vetcare.common.enums.Crt;
import com.lucast.vetcare.common.enums.IeIndicator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CompanyFiscalConfigRequest(
        @Size(max = 20) String ie,
        @NotNull IeIndicator ieIndicator,
        @NotNull Crt crt
) {}
