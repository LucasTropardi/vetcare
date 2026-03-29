package com.lucast.vetcare.sales;

import com.lucast.vetcare.common.enums.SaleStatus;
import com.lucast.vetcare.sales.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/sales")
@Tag(
        name = "Sales",
        description = "Endpoints for managing sales, items, payments and sale lifecycle"
)
public class SaleController {

    private final SaleService service;

    public SaleController(SaleService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create sale",
            description = "Creates a new sale"
    )
    public SaleResponse create(@RequestBody @Valid CreateSaleRequest req) {
        return service.create(req);
    }

    @GetMapping
    @Operation(
            summary = "List sales",
            description = "Lists sales with pagination and optional filters for period, status and customer search"
    )
    public Page<SaleListItemResponse> list(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) SaleStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @ParameterObject
            @PageableDefault(size = 20)
            Pageable pageable
    ) {
        return service.list(query, status, dateFrom, dateTo, pageable);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get sale by ID",
            description = "Returns the sale details for the given ID"
    )
    public SaleResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/{id}/receipt")
    @Operation(
            summary = "Get receipt preview",
            description = "Returns the homologation/dev visual receipt for a confirmed sale"
    )
    public SaleReceiptResponse getReceipt(@PathVariable Long id) {
        return service.getReceipt(id);
    }

    @GetMapping("/{id}/xml")
    @Operation(
            summary = "Download fiscal XML",
            description = "Returns the latest fiscal XML generated for a confirmed sale"
    )
    public ResponseEntity<byte[]> downloadXml(@PathVariable Long id) {
        String xml = service.getFiscalXml(id);
        String filename = "sale-" + id + ".xml";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_XML);
        headers.setContentDisposition(ContentDisposition.attachment().filename(filename, StandardCharsets.UTF_8).build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(xml.getBytes(StandardCharsets.UTF_8));
    }

    @GetMapping("/by-appointment")
    @Operation(
            summary = "Get sale by appointment",
            description = "Returns the sale associated with a specific appointment"
    )
    public SaleResponse getByAppointment(@RequestParam Long appointmentId) {
        return service.getByAppointment(appointmentId);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update sale",
            description = "Updates sale recipient or notes (draft only)"
    )
    public SaleResponse update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateSaleRequest req
    ) {
        return service.update(id, req);
    }

    @PostMapping("/{id}/items")
    @Operation(
            summary = "Add sale item",
            description = "Adds a new item to an existing sale"
    )
    public SaleResponse addItem(
            @PathVariable Long id,
            @RequestBody @Valid AddSaleItemRequest req
    ) {
        return service.addItem(id, req);
    }

    @PatchMapping("/{id}/items/{itemId}")
    @Operation(
            summary = "Update sale item",
            description = "Updates quantity or price of an existing sale item"
    )
    public SaleResponse updateItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @RequestBody @Valid UpdateSaleItemRequest req
    ) {
        return service.updateItem(id, itemId, req);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Remove sale item",
            description = "Removes an item from the sale"
    )
    public void removeItem(
            @PathVariable Long id,
            @PathVariable Long itemId
    ) {
        service.removeItem(id, itemId);
    }

    @PatchMapping("/{id}/discount")
    @Operation(
            summary = "Apply discount",
            description = "Applies a discount value to the sale"
    )
    public SaleResponse setDiscount(
            @PathVariable Long id,
            @RequestParam BigDecimal value
    ) {
        return service.setDiscount(id, value);
    }

    @PostMapping("/{id}/confirm")
    @Operation(
            summary = "Confirm sale",
            description = "Confirms the sale and finalizes its values"
    )
    public SaleResponse confirm(@PathVariable Long id) {
        return service.confirm(id);
    }

    @PostMapping("/{id}/checkout")
    @Operation(
            summary = "Finalize sale checkout",
            description = "Registers payment lines, confirms the sale and returns a homologation/dev receipt"
    )
    public FinalizeSaleResponse checkout(
            @PathVariable Long id,
            @RequestBody @Valid FinalizeSaleRequest req
    ) {
        return service.checkout(id, req);
    }

    @PostMapping("/{id}/cancel")
    @Operation(
            summary = "Cancel sale",
            description = "Cancels a sale with a reason"
    )
    public SaleResponse cancel(
            @PathVariable Long id,
            @RequestBody @Valid CancelSaleRequest req
    ) {
        return service.cancel(id, req);
    }

    @PostMapping("/{id}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Add payment",
            description = "Registers a payment for the sale"
    )
    public SalePaymentResponse addPayment(
            @PathVariable Long id,
            @RequestBody @Valid CreatePaymentRequest req
    ) {
        return service.addPayment(id, req);
    }
}
