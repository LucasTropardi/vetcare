package com.lucast.vetcare.fiscal.api.dto;

import java.time.OffsetDateTime;

public record FiscalEventResponse(
        Long id,
        String eventType,
        String status,
        String sefazMessage,
        OffsetDateTime createdAt
) {}
