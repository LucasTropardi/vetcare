package com.lucast.vetcare.catalog.dto;

import java.math.BigDecimal;

public record ProductFiscalResponse(
        String ncm,
        String cest,
        String origin,
        String gtinEan,
        String gtinEanTrib,
        String unitTrib,
        BigDecimal tribFactor,
        String cbenef,
        String serviceListCode
) {}
