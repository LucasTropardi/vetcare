package com.lucast.vetcare.customers.company;

import com.lucast.vetcare.common.enums.IeIndicator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "customer_company_fiscal")
public class CustomerCompanyFiscalEntity {

    @Id
    @Column(name = "customer_company_id")
    private Long customerCompanyId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "customer_company_id")
    private CustomerCompanyEntity company;

    @Column(name = "ie", length = 20)
    private String ie;

    @Enumerated(EnumType.STRING)
    @Column(name = "ie_indicator", nullable = false, length = 30)
    private IeIndicator ieIndicator;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
