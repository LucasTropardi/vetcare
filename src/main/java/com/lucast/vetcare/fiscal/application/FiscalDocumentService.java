package com.lucast.vetcare.fiscal.application;

import com.lucast.vetcare.fiscal.domain.*;
import com.lucast.vetcare.fiscal.repository.FiscalDocumentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FiscalDocumentService {

    private final FiscalDocumentRepository repo;

    public FiscalDocumentService(FiscalDocumentRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public FiscalDocumentEntity create(FiscalDocumentType type, Long saleId, String uf, String environment, String xml) {
        var e = new FiscalDocumentEntity();
        e.setDocType(type);
        e.setSaleId(saleId);
        e.setUf(uf);
        e.setEnvironment(environment);
        e.setXml(xml);
        e.setStatus(FiscalDocumentStatus.DRAFT);
        return repo.save(e);
    }

    @Transactional
    public FiscalDocumentEntity markSigned(Long id, String signedXml) {
        var e = repo.findById(id).orElseThrow();
        e.setXmlSigned(signedXml);
        e.setStatus(FiscalDocumentStatus.SIGNED);
        return repo.save(e);
    }

    @Transactional
    public FiscalDocumentEntity markSent(Long id, String lastResponse) {
        var e = repo.findById(id).orElseThrow();
        e.setLastResponse(lastResponse);
        e.setStatus(FiscalDocumentStatus.SENT);
        return repo.save(e);
    }
}
