package com.lucast.vetcare.catalog;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "tax_profiles")
public class TaxProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, length = 30)
    private String operation;

    @Column(length = 4)
    private String cfop;

    @Column(name = "icms_code", length = 10)
    private String icmsCode;

    @Column(name = "icms_rate", precision = 6, scale = 4)
    private BigDecimal icmsRate;

    @Column(name = "pis_code", length = 10)
    private String pisCode;

    @Column(name = "pis_rate", precision = 6, scale = 4)
    private BigDecimal pisRate;

    @Column(name = "cofins_code", length = 10)
    private String cofinsCode;

    @Column(name = "cofins_rate", precision = 6, scale = 4)
    private BigDecimal cofinsRate;

    @Column(name = "ipi_code", length = 10)
    private String ipiCode;

    @Column(name = "ipi_rate", precision = 6, scale = 4)
    private BigDecimal ipiRate;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
