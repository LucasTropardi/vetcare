package com.lucast.vetcare.sales;

import com.lucast.vetcare.sales.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/sales")
public class SaleController {

    private final SaleService service;

    public SaleController(SaleService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SaleResponse create(@RequestBody @Valid CreateSaleRequest req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    public SaleResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @GetMapping("/by-appointment")
    public SaleResponse getByAppointment(@RequestParam Long appointmentId) {
        return service.getByAppointment(appointmentId);
    }

    @PostMapping("/{id}/items")
    public SaleResponse addItem(@PathVariable Long id, @RequestBody @Valid AddSaleItemRequest req) {
        return service.addItem(id, req);
    }

    @PatchMapping("/{id}/items/{itemId}")
    public SaleResponse updateItem(
            @PathVariable Long id,
            @PathVariable Long itemId,
            @RequestBody @Valid UpdateSaleItemRequest req
    ) {
        return service.updateItem(id, itemId, req);
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeItem(@PathVariable Long id, @PathVariable Long itemId) {
        service.removeItem(id, itemId);
    }

    @PatchMapping("/{id}/discount")
    public SaleResponse setDiscount(@PathVariable Long id, @RequestParam BigDecimal value) {
        return service.setDiscount(id, value);
    }

    @PostMapping("/{id}/confirm")
    public SaleResponse confirm(@PathVariable Long id) {
        return service.confirm(id);
    }

    @PostMapping("/{id}/cancel")
    public SaleResponse cancel(@PathVariable Long id, @RequestBody @Valid CancelSaleRequest req) {
        return service.cancel(id, req);
    }

    @PostMapping("/{id}/payments")
    @ResponseStatus(HttpStatus.CREATED)
    public SalePaymentResponse addPayment(@PathVariable Long id, @RequestBody @Valid CreatePaymentRequest req) {
        return service.addPayment(id, req);
    }
}
