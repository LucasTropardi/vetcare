package com.lucast.vetcare.catalog;

import com.lucast.vetcare.common.enums.FiscalOrigin;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "product_fiscal")
public class ProductFiscalEntity {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(length = 8)
    private String ncm;

    @Column(length = 7)
    private String cest;

    @Enumerated(EnumType.STRING)
    @Column(name = "origin", nullable = false, length = 20)
    FiscalOrigin origin;

    @Column(name = "gtin_ean", length = 14)
    private String gtinEan;

    @Column(name = "gtin_ean_trib", length = 14)
    private String gtinEanTrib;

    @Column(name = "u_trib", length = 10)
    private String unitTrib;

    @Column(name = "trib_factor", precision = 18, scale = 6)
    private BigDecimal tribFactor;

    @Column(length = 20)
    private String cbenef;

    @Column(name = "service_list_code", length = 20)
    private String serviceListCode;

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
