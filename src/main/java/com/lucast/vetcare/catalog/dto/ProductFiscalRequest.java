package com.lucast.vetcare.catalog.dto;

import com.lucast.vetcare.common.enums.FiscalOrigin;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductFiscalRequest(
        @Pattern(regexp="\\d{8}", message = "ncm must have exactly 8 digits")
        String ncm,

        @Pattern(regexp="\\d{7}", message = "cest must have exactly 7 digits")
        String cest,

        FiscalOrigin origin,

        @Pattern(regexp="\\d{8}|\\d{12}|\\d{13}|\\d{14}", message = "gtinEan must have 8, 12, 13 or 14 digits")
        String gtinEan,

        @Pattern(regexp="\\d{8}|\\d{12}|\\d{13}|\\d{14}", message = "gtinEanTrib must have 8, 12, 13 or 14 digits")
        String gtinEanTrib,

        @Size(max = 10, message = "unitTrib max length is 10")
        String unitTrib,

        BigDecimal tribFactor,

        @Size(max = 20, message = "cbenef max length is 20")
        String cbenef,

        @Size(max = 20, message = "serviceListCode max length is 20")
        String serviceListCode
) {}

