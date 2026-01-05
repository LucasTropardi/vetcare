package com.lucast.vetcare.fiscal.application;

import com.lucast.vetcare.common.enums.PaymentStatus;
import com.lucast.vetcare.common.enums.SaleStatus;
import com.lucast.vetcare.fiscal.FiscalProperties;
import com.lucast.vetcare.fiscal.document.FiscalEventEntity;
import com.lucast.vetcare.fiscal.document.repository.FiscalEventRepository;
import com.lucast.vetcare.fiscal.domain.FiscalDocumentEntity;
import com.lucast.vetcare.fiscal.domain.FiscalDocumentStatus;
import com.lucast.vetcare.fiscal.domain.FiscalDocumentType;
import com.lucast.vetcare.fiscal.enums.AssinaturaEnum;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfce.NfceIssuanceService;
import com.lucast.vetcare.fiscal.nfce.NfceXmlBuilder;
import com.lucast.vetcare.fiscal.repository.FiscalDocumentRepository;
import com.lucast.vetcare.fiscal.util.FiscalUtils;
import com.lucast.vetcare.sales.SaleRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Objects;

@Service
public class FiscalWorkflowService {

    public static final String EVENT_DRAFT_CREATED = "DRAFT_CREATED";
    public static final String EVENT_SIGNED = "SIGNED";
    public static final String EVENT_SENT = "SENT";
    public static final String EVENT_AUTHORIZED = "AUTHORIZED";
    public static final String EVENT_REJECTED = "REJECTED";
    public static final String EVENT_CANCEL_REQUESTED = "CANCEL_REQUESTED";
    public static final String EVENT_CANCELED = "CANCELED";

    private final FiscalDocumentRepository documentRepository;
    private final FiscalEventRepository eventRepository;
    private final SaleRepository saleRepository;
    private final FiscalProperties props;
    private final NfceIssuanceService nfceIssuanceService;
    private final NfceXmlBuilder xmlBuilder;

    public FiscalWorkflowService(
            FiscalDocumentRepository documentRepository,
            FiscalEventRepository eventRepository,
            SaleRepository saleRepository,
            FiscalProperties props,
            NfceXmlBuilder xmlBuilder,
            NfceIssuanceService nfceIssuanceService
    ) {
        this.documentRepository = documentRepository;
        this.eventRepository = eventRepository;
        this.saleRepository = saleRepository;
        this.props = props;
        this.xmlBuilder = xmlBuilder;
        this.nfceIssuanceService = nfceIssuanceService;
    }

    @Transactional
    public FiscalDocumentEntity createDraftForSale(Long saleId, FiscalDocumentType docType, String uf, String environment) {
        if (saleId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "saleId is required");
        }
        var sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sale not found: " + saleId));

        // Regra do POS: só gera documento fiscal quando a venda está confirmada e totalmente paga.
        requireSaleClosed(sale);

        var existing = documentRepository.findTopBySaleIdOrderByIdDesc(saleId).orElse(null);
        if (existing != null && existing.getStatus() == FiscalDocumentStatus.DRAFT && existing.getDocType() == docType) {
            return existing;
        }

        var doc = new FiscalDocumentEntity();
        doc.setDocType(Objects.requireNonNullElse(docType, FiscalDocumentType.NFCE));
        doc.setSaleId(saleId);
        doc.setUf(choose(uf, props.getUf()));
        doc.setEnvironment(choose(environment, props.getAmbiente()));
        doc.setStatus(FiscalDocumentStatus.DRAFT);
        doc.setXml(xmlBuilder.buildFromSale(sale, doc.getUf(), doc.getEnvironment()));
        doc = documentRepository.save(doc);

        registerEvent(doc, EVENT_DRAFT_CREATED, "OK", doc.getXml(), null, "Draft created");
        return doc;
    }

    @Transactional
    public FiscalDocumentEntity sign(Long documentId) {
        var doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FiscalDocument not found: " + documentId));

        if (doc.getStatus() != FiscalDocumentStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot sign when status is " + doc.getStatus() + ". Expected DRAFT.");
        }

        try {
            var cert = loadCertOrThrow();
            String signed = nfceIssuanceService.sign(doc.getXml(), cert, AssinaturaEnum.NFE);

            doc.setXmlSigned(signed);
            doc.setStatus(FiscalDocumentStatus.SIGNED);
            doc = documentRepository.save(doc);

            registerEvent(doc, EVENT_SIGNED, "OK", doc.getXml(), signed, "Signed");
            return doc;
        } catch (FiscalException e) {
            registerEvent(doc, EVENT_SIGNED, "ERROR", doc.getXml(), null, e.getMessage());
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Sign failed: " + e.getMessage());
        }
    }

    @Transactional
    public FiscalDocumentEntity send(Long documentId) {
        var doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FiscalDocument not found: " + documentId));

        if (doc.getStatus() == FiscalDocumentStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot send when status is DRAFT. Sign first.");
        }
        if (doc.getStatus() == FiscalDocumentStatus.AUTHORIZED || doc.getStatus() == FiscalDocumentStatus.CANCELED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot send when status is " + doc.getStatus());
        }
        if (doc.getStatus() != FiscalDocumentStatus.SIGNED && doc.getStatus() != FiscalDocumentStatus.SENT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot send when status is " + doc.getStatus() + ". Expected SIGNED (or SENT for retry).");
        }

        try {
            var cert = loadCertOrThrow();
            String uf = choose(doc.getUf(), props.getUf());
            String env = choose(doc.getEnvironment(), props.getAmbiente());
            String codigoUf = String.valueOf(FiscalUtils.ufToCodUf(uf));
            TipoAmbienteEnum amb = "PRODUCAO".equalsIgnoreCase(env) ? TipoAmbienteEnum.PRODUCAO : TipoAmbienteEnum.HOMOLOGACAO;

            Long lote = System.currentTimeMillis() % 1_000_000L;
            ArrayList<String> ret = nfceIssuanceService.send(doc.getXmlSigned(), lote, codigoUf, amb, cert);

            // retorno (EnviaNFCe.montaRetorno): [tpAmb, verAplic, dhRecbto, nProt, digVal, cStat, xMotivo, cUF, chNFe, versao, respostaXml]
            String protocol = getSafe(ret, 3);
            String accessKey = getSafe(ret, 8);
            String responseXml = getSafe(ret, 10);

            doc.setProtocol(protocol);
            doc.setAccessKey(accessKey);
            doc.setXmlProc(responseXml);
            doc.setLastResponse(responseXml);
            doc.setStatus(FiscalDocumentStatus.AUTHORIZED);
            doc = documentRepository.save(doc);

            registerEvent(doc, EVENT_SENT, "OK", doc.getXmlSigned(), responseXml, "Sent");
            registerEvent(doc, EVENT_AUTHORIZED, "OK", null, responseXml, "Authorized");
            return doc;
        } catch (FiscalException e) {
            String msg = e.getMessage();
            doc.setLastResponse(msg);
            doc = documentRepository.save(doc);
            registerEvent(doc, EVENT_SENT, "ERROR", doc.getXmlSigned(), null, msg);
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Send failed: " + msg);
        }
    }

    @Transactional
    public FiscalDocumentEntity markAuthorized(Long documentId, String xmlProc, String protocol, String accessKey, String lastResponse) {
        var doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FiscalDocument not found: " + documentId));

        if (doc.getStatus() != FiscalDocumentStatus.SENT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot authorize when status is " + doc.getStatus() + ". Expected SENT.");
        }

        doc.setXmlProc(xmlProc);
        doc.setProtocol(protocol);
        doc.setAccessKey(accessKey);
        doc.setLastResponse(lastResponse);
        doc.setStatus(FiscalDocumentStatus.AUTHORIZED);
        doc = documentRepository.save(doc);

        registerEvent(doc, EVENT_AUTHORIZED, "OK", null, xmlProc, "Authorized");
        return doc;
    }

    @Transactional
    public FiscalDocumentEntity markRejected(Long documentId, String sefazMessage, String lastResponse) {
        var doc = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "FiscalDocument not found: " + documentId));

        if (doc.getStatus() != FiscalDocumentStatus.SENT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cannot reject when status is " + doc.getStatus() + ". Expected SENT.");
        }

        doc.setLastResponse(lastResponse);
        doc.setStatus(FiscalDocumentStatus.REJECTED);
        doc = documentRepository.save(doc);

        registerEvent(doc, EVENT_REJECTED, "ERROR", null, lastResponse, sefazMessage);
        return doc;
    }

    private void registerEvent(FiscalDocumentEntity doc, String eventType, String status, String requestXml, String responseXml, String message) {
        var ev = new FiscalEventEntity();
        ev.setDocument(doc);
        ev.setEventType(eventType);
        ev.setStatus(status);
        ev.setRequestXml(requestXml);
        ev.setResponseXml(responseXml);
        ev.setSefazMessage(message);
        eventRepository.save(ev);
    }

    private com.lucast.vetcare.fiscal.certificado.Certificado loadCertOrThrow() throws FiscalException {
        String path = props.getCertificado().getCaminho();
        String senha = props.getCertificado().getSenha();

        if (path == null || path.isBlank()) {
            throw new FiscalException("Erro", "Configure app.fiscal.certificado.caminho com o arquivo .pfx (PKCS12)");
        }
        if (senha == null || senha.isBlank()) {
            throw new FiscalException("Erro", "Configure app.fiscal.certificado.senha");
        }

        try {
            byte[] pfxBytes = Files.readAllBytes(Path.of(path));
            String b64 = java.util.Base64.getEncoder().encodeToString(pfxBytes);
            return nfceIssuanceService.buildCertFromBase64Pfx(b64, senha);
        } catch (Exception e) {
            throw new FiscalException("Erro", "Falha ao ler certificado em " + path + ": " + e.getMessage());
        }
    }

    private void requireSaleClosed(com.lucast.vetcare.sales.SaleEntity sale) {
        if (sale.getStatus() != SaleStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale must be CONFIRMED to generate fiscal document");
        }

        boolean hasPending = sale.getPayments().stream().anyMatch(p -> p.getStatus() == PaymentStatus.PENDING);
        if (hasPending) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale has pending payments");
        }

        BigDecimal paid = sale.getPayments().stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .map(p -> nvl(p.getAmount()))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = nvl(sale.getTotal()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal remaining = total.subtract(paid);
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale is not fully paid. Remaining: " + remaining);
        }
    }

    private static BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static String choose(String provided, String fallback) {
        return (provided == null || provided.isBlank()) ? fallback : provided;
    }

    private static String getSafe(ArrayList<String> list, int idx) {
        if (list == null) return null;
        if (idx < 0 || idx >= list.size()) return null;
        return list.get(idx);
    }
}
