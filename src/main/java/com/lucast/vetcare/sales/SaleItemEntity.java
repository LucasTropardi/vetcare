package com.lucast.vetcare.sales;

import com.lucast.vetcare.common.enums.ItemType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter
@Entity
@Table(name = "sale_items")
public class SaleItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="sale_id", nullable = false)
    private SaleEntity sale;

    @Column(name="product_id", nullable = false)
    private Long productId;

    @Enumerated(EnumType.STRING)
    @Column(name="item_type", nullable = false, length = 20)
    private ItemType itemType; // snapshot

    @Column(name="description_snapshot", nullable = false, length = 200)
    private String descriptionSnapshot;

    @Column(name="unit_snapshot", nullable = false, length = 10)
    private String unitSnapshot;

    @Column(nullable = false, precision = 12, scale = 3)
    private BigDecimal quantity;

    @Column(name="unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Column(name="created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
