package com.lucast.vetcare.cashregister.occurrence;

import com.lucast.vetcare.common.enums.CashRegisterOccurrenceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "cash_register_occurrences")
public class CashRegisterOccurrenceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cash_register_id", nullable = false)
    private Long cashRegisterId;

    @Enumerated(EnumType.STRING)
    @Column(name = "event_type", nullable = false, length = 30)
    private CashRegisterOccurrenceType eventType;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(length = 200)
    private String description;

    @Column(name = "performed_by", nullable = false)
    private Long performedBy;

    @Column(name = "approved_by")
    private Long approvedBy;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        if (amount == null) amount = BigDecimal.ZERO;
        if (createdAt == null) createdAt = OffsetDateTime.now();
    }
}
