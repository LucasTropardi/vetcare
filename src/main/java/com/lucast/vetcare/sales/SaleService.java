package com.lucast.vetcare.sales;

import com.lucast.vetcare.auth.AuthContext;
import com.lucast.vetcare.catalog.ProductRepository;
import com.lucast.vetcare.cashregister.CashRegisterRepository;
import com.lucast.vetcare.cashregister.sale.CashRegisterSaleEntity;
import com.lucast.vetcare.cashregister.sale.CashRegisterSaleRepository;
import com.lucast.vetcare.company.CompanyRepository;
import com.lucast.vetcare.common.enums.*;
import com.lucast.vetcare.customers.tutor.TutorRepository;
import com.lucast.vetcare.customers.company.CustomerCompanyRepository;
import com.lucast.vetcare.fiscal.domain.FiscalDocumentEntity;
import com.lucast.vetcare.fiscal.domain.FiscalDocumentStatus;
import com.lucast.vetcare.fiscal.domain.FiscalDocumentType;
import com.lucast.vetcare.fiscal.repository.FiscalDocumentRepository;
import com.lucast.vetcare.sales.dto.*;
import com.lucast.vetcare.stock.StockService;
import com.lucast.vetcare.stock.dto.CreateStockMovementRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class SaleService {

    private final SaleRepository saleRepository;
    private final SaleItemRepository saleItemRepository;
    private final ProductRepository productRepository;
    private final StockService stockService;
    private final SalePaymentRepository paymentRepository;
    private final CustomerCompanyRepository customerCompanyRepository;
    private final CashRegisterRepository cashRegisterRepository;
    private final CashRegisterSaleRepository cashRegisterSaleRepository;
    private final CompanyRepository companyRepository;
    private final TutorRepository tutorRepository;
    private final FiscalDocumentRepository fiscalDocumentRepository;
    private final SaleFiscalEstimateService saleFiscalEstimateService;

    public SaleService(
            SaleRepository saleRepository,
            SaleItemRepository saleItemRepository,
            ProductRepository productRepository,
            StockService stockService,
            SalePaymentRepository paymentRepository,
            CustomerCompanyRepository customerCompanyRepository,
            CashRegisterRepository cashRegisterRepository,
            CashRegisterSaleRepository cashRegisterSaleRepository,
            CompanyRepository companyRepository,
            TutorRepository tutorRepository,
            FiscalDocumentRepository fiscalDocumentRepository,
            SaleFiscalEstimateService saleFiscalEstimateService
    ) {
        this.saleRepository = saleRepository;
        this.saleItemRepository = saleItemRepository;
        this.productRepository = productRepository;
        this.stockService = stockService;
        this.paymentRepository = paymentRepository;
        this.customerCompanyRepository = customerCompanyRepository;
        this.cashRegisterRepository = cashRegisterRepository;
        this.cashRegisterSaleRepository = cashRegisterSaleRepository;
        this.companyRepository = companyRepository;
        this.tutorRepository = tutorRepository;
        this.fiscalDocumentRepository = fiscalDocumentRepository;
        this.saleFiscalEstimateService = saleFiscalEstimateService;
    }

    @Transactional
    public SaleResponse create(CreateSaleRequest req) {
        Long userId = AuthContext.requireUserId();

        if (req.tutorId() != null && req.customerCompanyId() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide tutorId OR customerCompanyId, not both");
        }

        if (req.customerCompanyId() != null) {
            var company = customerCompanyRepository.findById(req.customerCompanyId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer company not found"));
            if (!company.isActive()) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Inactive customer company cannot be used");
            }
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
        linkCashRegisterSale(saved, req.cashRegisterId(), userId);
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

    @Transactional(readOnly = true)
    public Page<SaleListItemResponse> list(String query, SaleStatus status, LocalDate dateFrom, LocalDate dateTo, Pageable pageable) {
        String normalizedQuery = blankToNull(query);
        String queryLike = normalizedQuery != null ? "%" + normalizedQuery.toLowerCase(Locale.ROOT) + "%" : null;
        Long queryNumber = tryParseLong(normalizedQuery);
        OffsetDateTime startAt = dateFrom != null
                ? dateFrom.atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime()
                : null;
        OffsetDateTime endBefore = dateTo != null
                ? dateTo.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toOffsetDateTime()
                : null;

        return saleRepository.listSales(
                queryLike,
                queryNumber,
                status != null ? status.name() : null,
                startAt,
                endBefore,
                pageable
        ).map(this::toListItemResponse);
    }

    @Transactional
    public SaleResponse update(Long id, UpdateSaleRequest req) {
        var sale = getOrThrow(id);
        requireDraft(sale);
        applyRecipientAndNotes(sale, req.tutorId(), req.customerCompanyId(), req.clearRecipient(), req.notes());

        var saved = saleRepository.save(sale);
        syncCashRegisterSaleSnapshot(saved);
        return toResponse(saved);
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
        syncCashRegisterSaleSnapshot(saved);
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
        syncCashRegisterSaleSnapshot(saved);
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
        var saved = saleRepository.save(sale);
        syncCashRegisterSaleSnapshot(saved);
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
        var saved = saleRepository.save(sale);
        syncCashRegisterSaleSnapshot(saved);
        return toResponse(saved);
    }

    @Transactional
    public SaleResponse confirm(Long saleId) {
        Long userId = AuthContext.requireUserId();
        var sale = getOrThrow(saleId);
        var saved = confirmSale(sale, userId);
        closeLinkedCashRegisterSale(saved.getId(), userId, CashRegisterStatus.CLOSED);
        return toResponse(saved);
    }

    @Transactional
    public FinalizeSaleResponse checkout(Long saleId, FinalizeSaleRequest req) {
        Long userId = AuthContext.requireUserId();
        var sale = getOrThrow(saleId);
        requireDraft(sale);

        applyRecipientAndNotes(sale, req.tutorId(), req.customerCompanyId(), req.clearRecipient(), req.notes());
        sale.getPayments().clear();

        BigDecimal paidTotal = BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        for (var payment : req.payments()) {
            var created = createPaymentEntity(sale, payment.method(), payment.amount(), payment.notes(), paidTotal, userId);
            sale.getPayments().add(created);
            paidTotal = paidTotal.add(created.getAmount()).setScale(2, RoundingMode.HALF_UP);
        }

        saleRepository.saveAndFlush(sale);

        var confirmedSale = confirmSale(sale, userId);
        syncFiscalReceiptMetadata(confirmedSale);
        closeLinkedCashRegisterSale(confirmedSale.getId(), userId, CashRegisterStatus.CLOSED);
        return new FinalizeSaleResponse(toResponse(confirmedSale), buildReceipt(confirmedSale));
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

        var saved = saleRepository.save(sale);
        closeLinkedCashRegisterSale(saved.getId(), userId, CashRegisterStatus.CANCELED);
        return toResponse(saved);
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

        BigDecimal paidSoFar = paymentRepository.findBySale_Id(saleId).stream()
                .filter(p -> p.getStatus() == PaymentStatus.PAID)
                .map(SalePaymentEntity::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);
        var pay = createPaymentEntity(sale, req.method(), req.amount(), req.notes(), paidSoFar, userId);
        var saved = paymentRepository.save(pay);
        return toPaymentResponse(saved);
    }

    @Transactional
    public SaleReceiptResponse getReceipt(Long saleId) {
        var sale = getOrThrow(saleId);
        if (sale.getStatus() != SaleStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only CONFIRMED sales can be reprinted");
        }
        return buildReceipt(sale);
    }

    @Transactional
    public String getFiscalXml(Long saleId) {
        var sale = getOrThrow(saleId);
        if (sale.getStatus() != SaleStatus.CONFIRMED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only CONFIRMED sales have XML available");
        }

        var latestDocument = fiscalDocumentRepository.findTopBySaleIdOrderByIdDesc(saleId)
                .orElseGet(() -> syncFiscalReceiptMetadata(sale));

        String xml = firstNonBlank(latestDocument.getXmlProc(), latestDocument.getXmlSigned(), latestDocument.getXml());
        if (xml == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Fiscal XML not found for sale");
        }
        return xml;
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

    private void applyRecipientAndNotes(
            SaleEntity sale,
            Long tutorId,
            Long customerCompanyId,
            Boolean clearRecipient,
            String notes
    ) {
        boolean shouldClearRecipient = Boolean.TRUE.equals(clearRecipient);

        if (shouldClearRecipient && (tutorId != null || customerCompanyId != null)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "clearRecipient cannot be combined with tutorId/customerCompanyId");
        }

        if (shouldClearRecipient) {
            sale.setTutorId(null);
            sale.setCustomerCompanyId(null);
        }

        boolean hasRecipientChange = tutorId != null || customerCompanyId != null;
        if (hasRecipientChange) {
            if (tutorId != null && customerCompanyId != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provide tutorId OR customerCompanyId, not both");
            }
            if (customerCompanyId != null) {
                var company = customerCompanyRepository.findById(customerCompanyId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer company not found"));
                if (!company.isActive()) {
                    throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Inactive customer company cannot be used");
                }
            }
            if (tutorId != null && !tutorRepository.existsById(tutorId)) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor not found");
            }
            sale.setTutorId(tutorId);
            sale.setCustomerCompanyId(customerCompanyId);
        }

        if (notes != null) {
            sale.setNotes(blankToNull(notes));
        }
    }

    private SalePaymentEntity createPaymentEntity(
            SaleEntity sale,
            PaymentMethod method,
            BigDecimal amountReceived,
            String rawNotes,
            BigDecimal paidSoFar,
            Long userId
    ) {
        if (method == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "method is required");
        }
        if (amountReceived == null || amountReceived.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be > 0");
        }

        BigDecimal total = nvl(sale.getTotal()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal remaining = total.subtract(nvl(paidSoFar)).setScale(2, RoundingMode.HALF_UP);
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale is already fully paid");
        }

        BigDecimal amountApplied = amountReceived.setScale(2, RoundingMode.HALF_UP);
        String notes = blankToNull(rawNotes);

        if (method == PaymentMethod.CASH) {
            if (amountApplied.compareTo(remaining) > 0) {
                BigDecimal change = amountApplied.subtract(remaining).setScale(2, RoundingMode.HALF_UP);
                amountApplied = remaining;
                String auto = "received=" + amountReceived.setScale(2, RoundingMode.HALF_UP) + ", change=" + change;
                notes = notes == null ? auto : notes + " | " + auto;
            }
        } else if (amountApplied.compareTo(remaining) > 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Payment amount exceeds remaining: " + remaining);
        }

        var pay = new SalePaymentEntity();
        pay.setSale(sale);
        pay.setMethod(method);
        pay.setAmount(amountApplied);
        pay.setCreatedBy(userId);
        pay.setStatus(PaymentStatus.PAID);
        pay.setNotes(notes);
        return pay;
    }

    private SaleEntity confirmSale(SaleEntity sale, Long userId) {
        requireDraft(sale);

        if (sale.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale must have at least one item");
        }

        boolean hasPending = sale.getPayments().stream().anyMatch(p -> p.getStatus() == PaymentStatus.PENDING);
        if (hasPending) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale has pending payments");
        }

        BigDecimal paidSoFar = sale.getPayments().stream()
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
        return saleRepository.save(sale);
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

    private SaleListItemResponse toListItemResponse(SaleListProjection row) {
        return new SaleListItemResponse(
                row.getId(),
                row.getCompanyId(),
                row.getTutorId(),
                row.getCustomerCompanyId(),
                row.getAppointmentId(),
                SaleStatus.valueOf(row.getStatus()),
                nvl(row.getSubtotal()).setScale(2, RoundingMode.HALF_UP),
                nvl(row.getDiscount()).setScale(2, RoundingMode.HALF_UP),
                nvl(row.getTotal()).setScale(2, RoundingMode.HALF_UP),
                nvl(row.getPaidTotal()).setScale(2, RoundingMode.HALF_UP),
                nvl(row.getRemaining()).setScale(2, RoundingMode.HALF_UP),
                row.getNotes(),
                row.getCreatedBy(),
                row.getConfirmedBy(),
                toOffsetDateTime(row.getConfirmedAt()),
                row.getCanceledBy(),
                toOffsetDateTime(row.getCanceledAt()),
                toOffsetDateTime(row.getCreatedAt()),
                toOffsetDateTime(row.getUpdatedAt()),
                row.getItemCount(),
                blankToNull(row.getCustomerName()),
                blankToNull(row.getCustomerDocument()),
                blankToNull(row.getRegisterCode()),
                row.getSaleNumber(),
                blankToNull(row.getDocumentNumber()),
                blankToNull(row.getProtocol()),
                Boolean.TRUE.equals(row.getCanCancel()),
                Boolean.TRUE.equals(row.getXmlAvailable()),
                Boolean.TRUE.equals(row.getReceiptAvailable())
        );
    }

    private OffsetDateTime toOffsetDateTime(java.time.Instant value) {
        return value == null ? null : value.atOffset(ZoneId.systemDefault().getRules().getOffset(value));
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

    private SaleReceiptResponse buildReceipt(SaleEntity sale) {
        var company = companyRepository.findById(sale.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Issuer company not found"));
        var link = cashRegisterSaleRepository.findBySaleId(sale.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Sale is not linked to a cash register"));
        var taxEstimate = saleFiscalEstimateService.estimate(sale);
        var receiptDocument = syncFiscalReceiptMetadata(sale);
        String registerCode = cashRegisterRepository.findById(link.getCashRegisterId())
                .map(cashRegister -> cashRegister.getRegisterCode())
                .orElse("PDV");

        var companyResponse = new SaleReceiptCompanyResponse(
                company.getId(),
                blankToNull(company.getTradeName()) != null ? company.getTradeName() : company.getLegalName(),
                company.getLegalName(),
                company.getCnpj(),
                company.getPhone()
        );

        var tutor = Optional.ofNullable(sale.getTutorId())
                .flatMap(tutorRepository::findById)
                .orElse(null);

        var customerResponse = new SaleReceiptCustomerResponse(
                tutor != null,
                tutor != null ? tutor.getName() : Objects.requireNonNullElse(link.getCustomerName(), "Consumidor nao identificado"),
                tutor != null ? tutor.getDocument() : link.getCustomerDocument()
        );

        List<SaleReceiptItemResponse> items = new ArrayList<>();
        for (int index = 0; index < sale.getItems().size(); index++) {
            var item = sale.getItems().get(index);
            items.add(new SaleReceiptItemResponse(
                    item.getId(),
                    index + 1,
                    item.getProductId(),
                    item.getItemType(),
                    item.getDescriptionSnapshot(),
                    item.getUnitSnapshot(),
                    Optional.ofNullable(taxEstimate.itemsByProductId().get(item.getId())).map(SaleFiscalEstimateService.ItemFiscalEstimate::ncm).orElse(null),
                    Optional.ofNullable(taxEstimate.itemsByProductId().get(item.getId())).map(SaleFiscalEstimateService.ItemFiscalEstimate::cfop).orElse(null),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getTotal(),
                    Optional.ofNullable(taxEstimate.itemsByProductId().get(item.getId())).map(SaleFiscalEstimateService.ItemFiscalEstimate::estimatedTaxTotal).orElse(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
            ));
        }

        List<SaleReceiptPaymentResponse> payments = sale.getPayments().stream()
                .map(payment -> new SaleReceiptPaymentResponse(
                        payment.getId(),
                        payment.getMethod(),
                        payment.getStatus(),
                        payment.getAmount(),
                        payment.getNotes(),
                        payment.getPaidAt()
                ))
                .toList();

        BigDecimal change = payments.stream()
                .map(payment -> parseAutoValue(payment.notes(), "change"))
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal receivedTotal = nvl(sale.getTotal()).add(change).setScale(2, RoundingMode.HALF_UP);

        return new SaleReceiptResponse(
                sale.getId(),
                link.getSaleNumber(),
                registerCode,
                "DEV_HOMOLOG",
                "Documento fiscal emitido em ambiente de homologacao dev. Nenhuma transmissao para SEFAZ foi realizada.",
                "Comprovante fiscal simulado",
                link.getFiscalDocumentNumber(),
                link.getFiscalDocumentSeries(),
                link.getFiscalDocumentKey(),
                receiptDocument.getProtocol(),
                sale.getConfirmedAt(),
                companyResponse,
                customerResponse,
                items,
                payments,
                sale.getSubtotal(),
                sale.getDiscount(),
                sale.getTotal(),
                new SaleReceiptTaxBreakdownResponse(
                        taxEstimate.estimatedFederalTax(),
                        taxEstimate.estimatedStateTax(),
                        taxEstimate.estimatedMunicipalTax(),
                        taxEstimate.estimatedTaxTotal()
                ),
                receivedTotal,
                change
        );
    }

    private FiscalDocumentEntity syncFiscalReceiptMetadata(SaleEntity sale) {
        var link = cashRegisterSaleRepository.findBySaleId(sale.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.CONFLICT, "Sale is not linked to a cash register"));
        var company = companyRepository.findById(sale.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Issuer company not found"));
        var tutor = Optional.ofNullable(sale.getTutorId())
                .flatMap(tutorRepository::findById)
                .orElse(null);

        String customerName = tutor != null ? tutor.getName() : "Consumidor nao identificado";
        String customerDocument = tutor != null ? tutor.getDocument() : null;
        String documentNumber = String.format(Locale.ROOT, "%06d", link.getSaleNumber());
        String series = "DEV";
        String accessKey = buildDevAccessKey(company.getCnpj(), sale.getId(), link.getSaleNumber(), sale.getConfirmedAt());
        String protocol = "DEV-" + sale.getId() + "-" + documentNumber;
        var taxEstimate = saleFiscalEstimateService.estimate(sale);

        link.setCustomerName(customerName);
        link.setCustomerDocument(customerDocument);
        link.setFiscalDocumentType("NFCE");
        link.setFiscalDocumentNumber(documentNumber);
        link.setFiscalDocumentSeries(series);
        link.setFiscalDocumentKey(accessKey);
        cashRegisterSaleRepository.save(link);

        var doc = fiscalDocumentRepository.findTopBySaleIdOrderByIdDesc(sale.getId()).orElseGet(FiscalDocumentEntity::new);
        doc.setSaleId(sale.getId());
        doc.setDocType(FiscalDocumentType.NFCE);
        doc.setStatus(FiscalDocumentStatus.AUTHORIZED);
        doc.setEnvironment("DEV_HOMOLOG");
        doc.setAccessKey(accessKey);
        doc.setProtocol(protocol);
        String xml = buildDevFiscalXml(sale, company, customerName, customerDocument, documentNumber, series, accessKey, protocol, taxEstimate);
        doc.setXml(xml);
        doc.setXmlSigned(xml);
        doc.setXmlProc(xml);
        doc.setLastResponse("DEV_HOMOLOG validated locally without SEFAZ transmission");
        return fiscalDocumentRepository.save(doc);
    }

    private String buildDevFiscalXml(
            SaleEntity sale,
            com.lucast.vetcare.company.CompanyEntity company,
            String customerName,
            String customerDocument,
            String documentNumber,
            String series,
            String accessKey,
            String protocol,
            SaleFiscalEstimateService.SaleFiscalEstimate taxEstimate
    ) {
        StringBuilder xml = new StringBuilder();
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
                .append("<NFeDevHomolog versao=\"4.00\">")
                .append(tag("tpAmb", "2"))
                .append(tag("indEmulacao", "1"))
                .append(tag("natOp", "VENDA"))
                .append(tag("mod", "65"))
                .append(tag("serie", series))
                .append(tag("nNF", documentNumber))
                .append(tag("dhEmi", (sale.getConfirmedAt() == null ? OffsetDateTime.now() : sale.getConfirmedAt()).toString()))
                .append(tag("chNFe", accessKey))
                .append(tag("nProt", protocol))
                .append("<emit>")
                .append(tag("CNPJ", digitsOnly(company.getCnpj())))
                .append(tag("xNome", escape(company.getLegalName())))
                .append(tagIfNotBlank("xFant", escape(company.getTradeName())))
                .append("</emit>")
                .append("<dest>")
                .append(tagIfNotBlank(customerDocument != null && customerDocument.replaceAll("\\D", "").length() > 11 ? "CNPJ" : "CPF", digitsOnly(customerDocument)))
                .append(tag("xNome", escape(customerName)))
                .append("</dest>");

        int line = 1;
        for (var item : sale.getItems()) {
            var estimate = taxEstimate.itemsByProductId().get(item.getId());
            xml.append("<det nItem=\"").append(line++).append("\">")
                    .append("<prod>")
                    .append(tag("cProd", String.valueOf(item.getProductId())))
                    .append(tag("xProd", escape(item.getDescriptionSnapshot())))
                    .append(tag("NCM", estimate == null || blankToNull(estimate.ncm()) == null ? "00000000" : estimate.ncm()))
                    .append(tagIfNotBlank("CEST", estimate == null ? null : estimate.cest()))
                    .append(tag("CFOP", estimate == null || blankToNull(estimate.cfop()) == null ? "5102" : estimate.cfop()))
                    .append(tag("uCom", escape(nullToEmpty(item.getUnitSnapshot(), "UN"))))
                    .append(tag("qCom", fmt(nvl(item.getQuantity()))))
                    .append(tag("vUnCom", fmt(nvl(item.getUnitPrice()))))
                    .append(tag("vProd", fmt(nvl(item.getTotal()))))
                    .append(tag("uTrib", escape(estimate == null ? nullToEmpty(item.getUnitSnapshot(), "UN") : nullToEmpty(estimate.unitTrib(), item.getUnitSnapshot()))))
                    .append(tag("qTrib", fmt(nvl(item.getQuantity()))))
                    .append(tag("vUnTrib", fmt(nvl(item.getUnitPrice()))))
                    .append("</prod>")
                    .append("<imposto>")
                    .append(tag("vTotTrib", fmt(estimate == null ? BigDecimal.ZERO : estimate.estimatedTaxTotal())))
                    .append("<ICMS>")
                    .append(tag("orig", estimate == null || blankToNull(estimate.origin()) == null ? "0" : estimate.origin()))
                    .append(tagIfNotBlank("CST", estimate == null ? null : estimate.icmsCode()))
                    .append(tagIfNotBlank("pICMS", estimate == null || estimate.icmsRate() == null ? null : estimate.icmsRate().toPlainString()))
                    .append(tag("vICMS", fmt(estimate == null ? BigDecimal.ZERO : estimate.estimatedStateTax())))
                    .append("</ICMS>")
                    .append("<PIS>")
                    .append(tagIfNotBlank("CST", estimate == null ? null : estimate.pisCode()))
                    .append(tagIfNotBlank("pPIS", estimate == null || estimate.pisRate() == null ? null : estimate.pisRate().toPlainString()))
                    .append(tag("vPIS", fmt(estimate == null ? BigDecimal.ZERO : estimate.estimatedPisTax())))
                    .append("</PIS>")
                    .append("<COFINS>")
                    .append(tagIfNotBlank("CST", estimate == null ? null : estimate.cofinsCode()))
                    .append(tagIfNotBlank("pCOFINS", estimate == null || estimate.cofinsRate() == null ? null : estimate.cofinsRate().toPlainString()))
                    .append(tag("vCOFINS", fmt(estimate == null ? BigDecimal.ZERO : estimate.estimatedCofinsTax())))
                    .append("</COFINS>")
                    .append("<IPI>")
                    .append(tagIfNotBlank("CST", estimate == null ? null : estimate.ipiCode()))
                    .append(tagIfNotBlank("pIPI", estimate == null || estimate.ipiRate() == null ? null : estimate.ipiRate().toPlainString()))
                    .append(tag("vIPI", fmt(estimate == null ? BigDecimal.ZERO : estimate.estimatedIpiTax())))
                    .append("</IPI>")
                    .append("</imposto>")
                    .append("</det>");
        }

        xml.append("<total>")
                .append(tag("vProd", fmt(nvl(sale.getSubtotal()))))
                .append(tag("vDesc", fmt(nvl(sale.getDiscount()))))
                .append(tag("vNF", fmt(nvl(sale.getTotal()))))
                .append(tag("vTotTrib", fmt(taxEstimate.estimatedTaxTotal())))
                .append("</total>")
                .append("</NFeDevHomolog>");

        return xml.toString();
    }

    private BigDecimal parseAutoValue(String notes, String field) {
        if (notes == null || notes.isBlank()) return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        Pattern pattern = Pattern.compile(field + "=([0-9]+(?:\\.[0-9]{1,2})?)");
        Matcher matcher = pattern.matcher(notes);
        if (!matcher.find()) {
            return BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP);
        }
        return new BigDecimal(matcher.group(1)).setScale(2, RoundingMode.HALF_UP);
    }

    private Long tryParseLong(String value) {
        if (value == null) return null;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    private String firstNonBlank(String... values) {
        for (String value : values) {
            if (value != null && !value.isBlank()) {
                return value;
            }
        }
        return null;
    }

    private String buildDevAccessKey(String cnpj, Long saleId, Long saleNumber, OffsetDateTime confirmedAt) {
        String cleanCnpj = Objects.requireNonNullElse(cnpj, "").replaceAll("\\D", "");
        String datePart = (confirmedAt != null ? confirmedAt : OffsetDateTime.now()).format(DateTimeFormatter.ofPattern("yyMMdd"));
        String digits = cleanCnpj + datePart
                + String.format(Locale.ROOT, "%09d", Objects.requireNonNullElse(saleId, 0L))
                + String.format(Locale.ROOT, "%09d", Objects.requireNonNullElse(saleNumber, 0L))
                + "9001";

        if (digits.length() >= 44) {
            return digits.substring(0, 44);
        }
        return (digits + "0".repeat(44 - digits.length())).substring(0, 44);
    }

    private static String tag(String name, String value) {
        return "<" + name + ">" + (value == null ? "" : value) + "</" + name + ">";
    }

    private static String tagIfNotBlank(String name, String value) {
        if (value == null || value.isBlank()) return "";
        return tag(name, value);
    }

    private static String digitsOnly(String value) {
        if (value == null) return "";
        return value.replaceAll("\\D", "");
    }

    private static String escape(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static String nullToEmpty(String value, String fallback) {
        if (value == null || value.isBlank()) return fallback == null ? "" : fallback;
        return value;
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }

    private static String fmt(BigDecimal value) {
        return (value == null ? BigDecimal.ZERO : value).setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private String blankToNull(String s) {
        if (s == null) return null;
        var t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private void linkCashRegisterSale(SaleEntity sale, Long cashRegisterId, Long userId) {
        if (cashRegisterId == null) return;

        var cashRegister = cashRegisterRepository.findById(cashRegisterId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Cash register not found"));

        if (cashRegister.getStatus() != CashRegisterStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cash register must be OPEN to link sale");
        }

        if (!cashRegister.getCompanyId().equals(sale.getCompanyId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sale company must match cash register company");
        }

        if (cashRegisterSaleRepository.findBySaleId(sale.getId()).isPresent()) {
            return;
        }

        Long nextNumber = cashRegisterSaleRepository.nextSaleNumber(cashRegisterId);
        if (nextNumber == null || nextNumber < 1) nextNumber = 1L;

        var link = new CashRegisterSaleEntity();
        link.setCashRegisterId(cashRegisterId);
        link.setSaleId(sale.getId());
        link.setSaleNumber(nextNumber);
        link.setStatus(CashRegisterStatus.OPEN);
        link.setSubtotalSnapshot(nvl(sale.getSubtotal()).setScale(2, RoundingMode.HALF_UP));
        link.setDiscountSnapshot(nvl(sale.getDiscount()).setScale(2, RoundingMode.HALF_UP));
        link.setSurchargeSnapshot(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP));
        link.setTotalSnapshot(nvl(sale.getTotal()).setScale(2, RoundingMode.HALF_UP));
        link.setNotes(sale.getNotes());
        link.setCreatedBy(userId);
        cashRegisterSaleRepository.save(link);
    }

    private void syncCashRegisterSaleSnapshot(SaleEntity sale) {
        cashRegisterSaleRepository.findBySaleId(sale.getId()).ifPresent(link -> {
            if (link.getStatus() != CashRegisterStatus.OPEN) return;
            link.setSubtotalSnapshot(nvl(sale.getSubtotal()).setScale(2, RoundingMode.HALF_UP));
            link.setDiscountSnapshot(nvl(sale.getDiscount()).setScale(2, RoundingMode.HALF_UP));
            link.setTotalSnapshot(nvl(sale.getTotal()).setScale(2, RoundingMode.HALF_UP));
            link.setNotes(sale.getNotes());
            cashRegisterSaleRepository.save(link);
        });
    }

    private void closeLinkedCashRegisterSale(Long saleId, Long userId, CashRegisterStatus status) {
        cashRegisterSaleRepository.findBySaleId(saleId).ifPresent(link -> {
            if (link.getStatus() != CashRegisterStatus.OPEN) return;
            link.setStatus(status);
            link.setClosedBy(userId);
            link.setClosedAt(OffsetDateTime.now());
            cashRegisterSaleRepository.save(link);
        });
    }
}
