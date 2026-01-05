package com.lucast.vetcare.sales;

import com.lucast.vetcare.auth.AuthContext;
import com.lucast.vetcare.catalog.ProductRepository;
import com.lucast.vetcare.common.enums.*;
import com.lucast.vetcare.sales.dto.*;
import com.lucast.vetcare.stock.StockService;
import com.lucast.vetcare.stock.dto.CreateStockMovementRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final StockService stockService;
    private final SalePaymentRepository paymentRepository;

    public SaleService(
            SaleRepository saleRepository,
            SaleItemRepository saleItemRepository,
            ProductRepository productRepository,
            StockService stockService,
            SalePaymentRepository paymentRepository
    ) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
        this.stockService = stockService;
        this.paymentRepository = paymentRepository;
    }

    @Transactional
    public SaleResponse create(CreateSaleRequest req) {
        Long userId = AuthContext.requireUserId();

        if (req.tutorId() != null && req.customerCompanyId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide tutorId OR customerCompanyId, not both");
        }

        if (req.appointmentId() != null) {
            saleRepository.findByAppointmentId(req.appointmentId())
                    .ifPresent(s -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "Appointment already has a sale"); });
        }

        var s = new SaleEntity();
        s.setCompanyId(req.companyId());
        s.setTutorId(req.tutorId());
        s.setCustomerCompanyId(req.customerCompanyId());
        s.setAppointmentId(req.appointmentId());
        s.setNotes(blankToNull(req.notes()));
        s.setStatus(SaleStatus.DRAFT);
        s.setCreatedBy(userId);

        var saved = saleRepository.save(s);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public SaleResponse get(Long id) {
        return toResponse(getOrThrow(id));
    }

    @Transactional(readOnly = true)
    public SaleResponse getByAppointment(Long appointmentId) {
        var s = saleRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sale not found for appointment"));
        return toResponse(s);
    }

    @Transactional
    public SaleResponse addItem(Long saleId, AddSaleItemRequest req) {
        var sale = getOrThrow(saleId);
        requireDraft(sale);

        var p = productRepository.findById(req.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (!p.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Inactive product cannot be sold");
        }

        BigDecimal qty = req.quantity();
        if (qty == null || qty.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantity must be > 0");
        }

        BigDecimal unitPrice = req.unitPrice() != null ? req.unitPrice() : nvl(p.getSalePrice());
        if (unitPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unitPrice must be >= 0");
        }

        BigDecimal lineTotal = qty.multiply(unitPrice).setScale(2, RoundingMode.HALF_UP);

        var item = new SaleItemEntity();
        item.setSale(sale);
        item.setProductId(p.getId());
        item.setItemType(p.getItemType());
        item.setDescriptionSnapshot(p.getName());
        item.setUnitSnapshot(p.getUnit());
        item.setQuantity(qty);
        item.setUnitPrice(unitPrice);
        item.setTotal(lineTotal);

        sale.getItems().add(item);

        recalcTotals(sale);
        var saved = saleRepository.save(sale);
        return toResponse(saved);
    }

    @Transactional
    public SaleResponse updateItem(Long saleId, Long itemId, UpdateSaleItemRequest req) {
        var sale = getOrThrow(saleId);
        requireDraft(sale);

        var item = saleItemRepository.findByIdAndSale_Id(itemId, saleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sale item not found"));

        if (req.quantity() == null && req.unitPrice() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide quantity and/or unitPrice");
        }

        if (req.quantity() != null) {
            if (req.quantity().compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "quantity must be > 0");
            }
            item.setQuantity(req.quantity());
        }

        if (req.unitPrice() != null) {
            if (req.unitPrice().compareTo(BigDecimal.ZERO) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unitPrice must be >= 0");
            }
            item.setUnitPrice(req.unitPrice().setScale(2, RoundingMode.HALF_UP));
        }

        BigDecimal newLineTotal = item.getQuantity().multiply(item.getUnitPrice()).setScale(2, RoundingMode.HALF_UP);
        item.setTotal(newLineTotal);

        recalcTotals(sale);
        var saved = saleRepository.save(sale);
        return toResponse(saved);
    }

    @Transactional
    public void removeItem(Long saleId, Long itemId) {
        var sale = getOrThrow(saleId);
        requireDraft(sale);

        var item = saleItemRepository.findByIdAndSale_Id(itemId, saleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sale item not found"));

        sale.getItems().removeIf(i -> i.getId().equals(item.getId()));

        recalcTotals(sale);
        saleRepository.save(sale);
    }

    @Transactional
    public SaleResponse setDiscount(Long saleId, BigDecimal discount) {
        var sale = getOrThrow(saleId);
        requireDraft(sale);

        BigDecimal d = nvl(discount);
        if (d.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "discount must be >= 0");
        }

        sale.setDiscount(d.setScale(2, RoundingMode.HALF_UP));
        recalcTotals(sale);
        return toResponse(saleRepository.save(sale));
    }

    @Transactional
    public SaleResponse confirm(Long saleId) {
        Long userId = AuthContext.requireUserId();
        var sale = getOrThrow(saleId);
        requireDraft(sale);

        if (sale.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale must have at least one item");
        }

        // Regra de fechamento: só confirma (fecha) venda se estiver totalmente paga
        boolean hasPending = paymentRepository.findBySale_Id(saleId).stream().anyMatch(p -> p.getStatus() == PaymentStatus.PENDING);
        if (hasPending) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale has pending payments");
        }

        BigDecimal paidSoFar = paymentRepository.findBySale_Id(saleId).stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .map(SalePaymentEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = nvl(sale.getTotal()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal remaining = total.subtract(paidSoFar).setScale(2, RoundingMode.HALF_UP);
        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale cannot be confirmed until fully paid. Remaining: " + remaining);
        }

        for (var it : sale.getItems()) {
            if (it.getItemType() == ItemType.PRODUCT) {
                stockService.createMovement(
                        new CreateStockMovementRequest(
                                it.getProductId(),
                                StockMovementType.EXIT_SALE,
                                it.getQuantity().negate(),
                                null,
                                "Sale #" + sale.getId(),
                                "SALE",
                                sale.getId()
                        ),
                        userId
                );
            }
        }

        sale.setStatus(SaleStatus.CONFIRMED);
        sale.setConfirmedBy(userId);
        sale.setConfirmedAt(OffsetDateTime.now());

        return toResponse(saleRepository.save(sale));
    }

    @Transactional
    public SaleResponse cancel(Long saleId, CancelSaleRequest req) {
        Long userId = AuthContext.requireUserId();
        var sale = getOrThrow(saleId);

        if (sale.getStatus() == SaleStatus.CANCELED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale already canceled");
        }

        if (sale.getStatus() == SaleStatus.CONFIRMED) {
            for (var it : sale.getItems()) {
                if (it.getItemType() == ItemType.PRODUCT) {
                    stockService.createMovement(
                            new CreateStockMovementRequest(
                                    it.getProductId(),
                                    StockMovementType.ADJUSTMENT,
                                    it.getQuantity(),
                                    null,
                                    "Reversal of sale #" + sale.getId() + ": " + req.reason().trim(),
                                    "REVERSAL",
                                    sale.getId()
                            ),
                            userId
                    );
                }
            }
        }

        sale.setStatus(SaleStatus.CANCELED);
        sale.setCanceledBy(userId);
        sale.setCanceledAt(OffsetDateTime.now());

        // opcional: manter observação do cancelamento
        String reason = req.reason() == null ? null : req.reason().trim();
        if (reason != null && !reason.isBlank()) {
            String prefix = "[CANCELED] ";
            sale.setNotes(
                    blankToNull(prefix + reason + (sale.getNotes() != null ? " | " + sale.getNotes() : ""))
            );
        }

        return toResponse(saleRepository.save(sale));
    }

    @Transactional
    public SalePaymentResponse addPayment(Long saleId, CreatePaymentRequest req) {
        Long userId = AuthContext.requireUserId();
        var sale = getOrThrow(saleId);

        if (sale.getStatus() == SaleStatus.CANCELED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Canceled sale cannot receive payments");
        }

        // Regra POS: pagamento só é lançado de forma consistente (nunca "pendente" sem querer).
        // - Sempre gravamos como PAID (se no futuro precisar PENDING, criamos um endpoint específico)
        // - Se for dinheiro, permitimos valor maior e calculamos troco
        // - Se for cartão/pix/etc, não permitimos exceder o valor restante

        var amountReceived = req.amount();
        if (amountReceived == null || amountReceived.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be > 0");
        }

        BigDecimal paidSoFar = paymentRepository.findBySale_Id(saleId).stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .map(SalePaymentEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = nvl(sale.getTotal()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal remaining = total.subtract(paidSoFar).setScale(2, RoundingMode.HALF_UP);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale is already fully paid");
        }

        BigDecimal amountApplied = amountReceived.setScale(2, RoundingMode.HALF_UP);
        String notes = blankToNull(req.notes());

        if (req.method() == PaymentMethod.CASH) {
            // dinheiro: se passou do restante, calcula troco e aplica somente o restante
            if (amountApplied.compareTo(remaining) > 0) {
                BigDecimal change = amountApplied.subtract(remaining).setScale(2, RoundingMode.HALF_UP);
                amountApplied = remaining;
                String auto = "received=" + amountReceived.setScale(2, RoundingMode.HALF_UP) + ", change=" + change;
                notes = (notes == null) ? auto : (notes + " | " + auto);
            }
        } else {
            // outros meios: não pode exceder o restante
            if (amountApplied.compareTo(remaining) > 0) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Payment amount exceeds remaining: " + remaining);
            }
        }

        var pay = new SalePaymentEntity();
        pay.setSale(sale);
        pay.setMethod(req.method());
        pay.setAmount(amountApplied);
        pay.setCreatedBy(userId);

        pay.setStatus(PaymentStatus.PAID);
        pay.setNotes(notes);

        var saved = paymentRepository.save(pay);
        return toPaymentResponse(saved);
    }

    // helpers

    private SaleEntity getOrThrow(Long id) {
        return saleRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sale not found"));
    }

    private void requireDraft(SaleEntity sale) {
        if (sale.getStatus() != SaleStatus.DRAFT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only DRAFT sales can be edited");
        }
    }

    private void recalcTotals(SaleEntity sale) {
        BigDecimal subtotal = sale.getItems().stream()
                .map(SaleItemEntity::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal discount = nvl(sale.getDiscount()).setScale(2, RoundingMode.HALF_UP);
        if (discount.compareTo(subtotal) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "discount cannot exceed subtotal");
        }

        BigDecimal total = subtotal.subtract(discount).setScale(2, RoundingMode.HALF_UP);

        sale.setSubtotal(subtotal);
        sale.setDiscount(discount);
        sale.setTotal(total);
    }

    private SaleResponse toResponse(SaleEntity s) {
        List<SaleItemResponse> items = s.getItems().stream()
                .map(i -> new SaleItemResponse(
                        i.getId(),
                        i.getProductId(),
                        i.getItemType(),
                        i.getDescriptionSnapshot(),
                        i.getUnitSnapshot(),
                        i.getQuantity(),
                        i.getUnitPrice(),
                        i.getTotal(),
                        i.getCreatedAt()
                ))
                .toList();

        List<SalePaymentResponse> payments = s.getPayments().stream()
                .map(this::toPaymentResponse)
                .toList();

        BigDecimal paidTotal = payments.stream()
                .filter(p -> p.status() == PaymentStatus.PAID)
                .map(SalePaymentResponse::amount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal total = nvl(s.getTotal()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal remaining = total.subtract(paidTotal);
        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            remaining = BigDecimal.ZERO;
        }

        return new SaleResponse(
                s.getId(),
                s.getCompanyId(),
                s.getTutorId(),
                s.getCustomerCompanyId(),
                s.getAppointmentId(),
                s.getStatus(),
                s.getSubtotal(),
                s.getDiscount(),
                s.getTotal(),
                paidTotal,
                remaining,
                s.getNotes(),
                s.getCreatedBy(),
                s.getConfirmedBy(),
                s.getConfirmedAt(),
                s.getCanceledBy(),
                s.getCanceledAt(),
                s.getCreatedAt(),
                s.getUpdatedAt(),
                items,
                payments
        );
    }

    private SalePaymentResponse toPaymentResponse(SalePaymentEntity p) {
        return new SalePaymentResponse(
                p.getId(),
                p.getMethod(),
                p.getStatus(),
                p.getAmount(),
                p.getPaidAt(),
                p.getCreatedBy(),
                p.getCreatedAt(),
                p.getNotes()
        );
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private String blankToNull(String s) {
        if (s == null) return null;
        var t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
