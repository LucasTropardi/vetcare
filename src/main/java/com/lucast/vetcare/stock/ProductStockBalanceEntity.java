package com.lucast.vetcare.stock;

import com.lucast.vetcare.catalog.ProductEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter
@Entity
@Table(name = "product_stock_balance")
public class ProductStockBalanceEntity {

    @Id
    @Column(name = "product_id")
    private Long productId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "product_id")
    private ProductEntity product;

    @Column(name = "on_hand", nullable = false, precision = 12, scale = 3)
    private BigDecimal onHand = BigDecimal.ZERO;

    @Column(name = "avg_cost", nullable = false, precision = 12, scale = 2)
    private BigDecimal avgCost = BigDecimal.ZERO;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        updatedAt = OffsetDateTime.now();
        if (onHand == null) onHand = BigDecimal.ZERO;
        if (avgCost == null) avgCost = BigDecimal.ZERO;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
