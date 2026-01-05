package com.lucast.vetcare.fiscal.api.dto;

import com.lucast.vetcare.fiscal.domain.FiscalDocumentType;

public record CreateFiscalDocumentRequest(
        Long saleId,
        FiscalDocumentType docType,
        String uf,
        String environment
) {}
