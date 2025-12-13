package com.lucast.vetcare.catalog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ProductFiscalRequest(
        @Size(max = 8) String ncm,
        @Size(max = 7) String cest,

        @NotBlank @Size(max = 2) String origin,

        @Size(max = 14) String gtinEan,
        @Size(max = 14) String gtinEanTrib,

        @Size(max = 10) String unitTrib,
        BigDecimal tribFactor,

        @Size(max = 20) String cbenef,
        @Size(max = 20) String serviceListCode
) {}
