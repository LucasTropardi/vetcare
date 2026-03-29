package com.lucast.vetcare.cashregister.sale;

import com.lucast.vetcare.common.enums.CashRegisterStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "cash_register_sales")
public class CashRegisterSaleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cash_register_id", nullable = false)
    private Long cashRegisterId;

    @Column(name = "sale_id", nullable = false, unique = true)
    private Long saleId;

    @Column(name = "sale_number", nullable = false)
    private Long saleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CashRegisterStatus status = CashRegisterStatus.OPEN;

    @Column(name = "issued_at", nullable = false)
    private OffsetDateTime issuedAt;

    @Column(name = "customer_name", length = 120)
    private String customerName;

    @Column(name = "customer_document", length = 20)
    private String customerDocument;

    @Column(name = "fiscal_document_type", nullable = false, length = 10)
    private String fiscalDocumentType = "NONE";

    @Column(name = "fiscal_document_number", length = 20)
    private String fiscalDocumentNumber;

    @Column(name = "fiscal_document_series", length = 10)
    private String fiscalDocumentSeries;

    @Column(name = "fiscal_document_key", length = 44)
    private String fiscalDocumentKey;

    @Column(name = "subtotal_snapshot", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotalSnapshot = BigDecimal.ZERO;

    @Column(name = "discount_snapshot", nullable = false, precision = 12, scale = 2)
    private BigDecimal discountSnapshot = BigDecimal.ZERO;

    @Column(name = "surcharge_snapshot", nullable = false, precision = 12, scale = 2)
    private BigDecimal surchargeSnapshot = BigDecimal.ZERO;

    @Column(name = "total_snapshot", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalSnapshot = BigDecimal.ZERO;

    @Column(name = "exclude_services_from_fiscal", nullable = false)
    private boolean excludeServicesFromFiscal = true;

    @Column(length = 300)
    private String notes;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "closed_by")
    private Long closedBy;

    @Column(name = "closed_at")
    private OffsetDateTime closedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        var now = OffsetDateTime.now();
        if (issuedAt == null) issuedAt = now;
        createdAt = now;
        updatedAt = now;
        if (status == null) status = CashRegisterStatus.OPEN;
        if (fiscalDocumentType == null || fiscalDocumentType.isBlank()) fiscalDocumentType = "NONE";
        if (subtotalSnapshot == null) subtotalSnapshot = BigDecimal.ZERO;
        if (discountSnapshot == null) discountSnapshot = BigDecimal.ZERO;
        if (surchargeSnapshot == null) surchargeSnapshot = BigDecimal.ZERO;
        if (totalSnapshot == null) totalSnapshot = BigDecimal.ZERO;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
