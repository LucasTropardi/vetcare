package com.lucast.vetcare.sales;

import com.lucast.vetcare.common.enums.PaymentMethod;
import com.lucast.vetcare.common.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter
@Entity
@Table(name = "sale_payments")
public class SalePaymentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sale_id", nullable = false)
    private SaleEntity sale;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PaymentMethod method;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private PaymentStatus status = PaymentStatus.PAID;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @Column(name="paid_at")
    private OffsetDateTime paidAt;

    @Column(name = "notes", length = 200)
    private String notes;

    @Column(name="created_by")
    private Long createdBy;

    @Column(name="created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now();
        if (paidAt == null && status == PaymentStatus.PAID) {
            paidAt = createdAt;
        }
    }
}
