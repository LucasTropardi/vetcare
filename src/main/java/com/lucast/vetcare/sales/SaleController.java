package com.lucast.vetcare.sales;

import com.lucast.vetcare.sales.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

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

    @GetMapping("/{id}")
    @Operation(
            summary = "Get sale by ID",
            description = "Returns the sale details for the given ID"
    )
    public SaleResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/by-appointment")
    @Operation(
            summary = "Get sale by appointment",
            description = "Returns the sale associated with a specific appointment"
    )
    public SaleResponse getByAppointment(@RequestParam Long appointmentId) {
        return service.getByAppointment(appointmentId);
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
