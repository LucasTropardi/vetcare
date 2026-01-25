package com.lucast.vetcare.fiscal.api;

import com.lucast.vetcare.fiscal.api.dto.CreateFiscalDocumentRequest;
import com.lucast.vetcare.fiscal.api.dto.FiscalDocumentResponse;
import com.lucast.vetcare.fiscal.api.dto.FiscalEventResponse;
import com.lucast.vetcare.fiscal.application.FiscalWorkflowService;
import com.lucast.vetcare.fiscal.document.repository.FiscalEventRepository;
import com.lucast.vetcare.fiscal.repository.FiscalDocumentRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/fiscal/documents")
@Tag(
        name = "Fiscal Documents",
        description = "Endpoints for managing fiscal documents lifecycle (draft, sign, send and events)"
)
public class FiscalDocumentController {

    private final FiscalWorkflowService workflow;
    private final FiscalDocumentRepository documentRepository;
    private final FiscalEventRepository eventRepository;

    public FiscalDocumentController(
            FiscalWorkflowService workflow,
            FiscalDocumentRepository documentRepository,
            FiscalEventRepository eventRepository
    ) {
        this.workflow = workflow;
        this.documentRepository = documentRepository;
        this.eventRepository = eventRepository;
    }

    @Operation(
            summary = "Create fiscal document draft",
            description = "Creates a draft fiscal document for a sale"
    )
    @PostMapping
    public FiscalDocumentResponse createDraft(@RequestBody CreateFiscalDocumentRequest req) {
        var doc = workflow.createDraftForSale(
                req.saleId(),
                req.docType(),
                req.uf(),
                req.environment()
        );
        return toResponse(doc);
    }

    @Operation(
            summary = "Sign fiscal document",
            description = "Signs a fiscal document, generating its access key"
    )
    @PostMapping("/{id}/sign")
    public FiscalDocumentResponse sign(@PathVariable Long id) {
        var doc = workflow.sign(id);
        return toResponse(doc);
    }

    @Operation(
            summary = "Send fiscal document",
            description = "Sends the fiscal document to SEFAZ"
    )
    @PostMapping("/{id}/send")
    public FiscalDocumentResponse send(@PathVariable Long id) {
        var doc = workflow.send(id);
        return toResponse(doc);
    }

    @Operation(
            summary = "Get fiscal document by ID",
            description = "Returns fiscal document details by its identifier"
    )
    @GetMapping("/{id}")
    public FiscalDocumentResponse get(@PathVariable Long id) {
        var doc = documentRepository.findById(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "FiscalDocument not found: " + id
                        )
                );
        return toResponse(doc);
    }

    @Operation(
            summary = "List fiscal document events",
            description = "Lists SEFAZ events related to a fiscal document, optionally filtered by status"
    )
    @GetMapping("/{id}/events")
    public List<FiscalEventResponse> events(
            @PathVariable Long id,
            @RequestParam(required = false) String status
    ) {
        var list = (status == null || status.isBlank())
                ? eventRepository.findByDocument_IdOrderByCreatedAtDesc(id)
                : eventRepository.findByDocument_IdAndStatusOrderByCreatedAtDesc(id, status);

        return list.stream()
                .map(ev -> new FiscalEventResponse(
                        ev.getId(),
                        ev.getEventType(),
                        ev.getStatus(),
                        ev.getSefazMessage(),
                        ev.getCreatedAt()
                ))
                .toList();
    }

    private FiscalDocumentResponse toResponse(
            com.lucast.vetcare.fiscal.domain.FiscalDocumentEntity d
    ) {
        return new FiscalDocumentResponse(
                d.getId(),
                d.getDocType(),
                d.getStatus(),
                d.getSaleId(),
                d.getUf(),
                d.getEnvironment(),
                d.getAccessKey(),
                d.getProtocol(),
                d.getLastResponse(),
                d.getCreatedAt(),
                d.getUpdatedAt()
        );
    }
}
