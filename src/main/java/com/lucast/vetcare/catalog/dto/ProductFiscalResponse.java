package com.lucast.vetcare.catalog.dto;

import com.lucast.vetcare.common.enums.FiscalOrigin;
import java.math.BigDecimal;

public record ProductFiscalResponse(
        String ncm,
        String cest,
        FiscalOrigin origin,
        String gtinEan,
        String gtinEanTrib,
        String unitTrib,
        BigDecimal tribFactor,
        String cbenef,
        String serviceListCode
) {}
