package com.lucast.vetcare.fiscal.api.dto;

import com.lucast.vetcare.fiscal.domain.FiscalDocumentStatus;
import com.lucast.vetcare.fiscal.domain.FiscalDocumentType;

import java.time.OffsetDateTime;

public record FiscalDocumentResponse(
        Long id,
        FiscalDocumentType docType,
        FiscalDocumentStatus status,
        Long saleId,
        String uf,
        String environment,
        String accessKey,
        String protocol,
        String lastResponse,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}
