package com.lucast.vetcare.company.dto;

import com.lucast.vetcare.common.enums.Crt;
import com.lucast.vetcare.common.enums.IeIndicator;

public record CompanyFiscalConfigResponse(
        Long companyId,
        String ie,
        IeIndicator ieIndicator,
        Crt crt
) {}
