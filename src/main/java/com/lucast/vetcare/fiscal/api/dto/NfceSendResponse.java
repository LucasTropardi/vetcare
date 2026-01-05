package com.lucast.vetcare.fiscal.api.dto;

import java.util.List;

public record NfceSendResponse(Long fiscalDocumentId, List<String> sefazResponse) {}
