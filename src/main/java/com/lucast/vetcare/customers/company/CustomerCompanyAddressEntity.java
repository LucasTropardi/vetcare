package com.lucast.vetcare.customers.company;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "customer_company_address")
public class CustomerCompanyAddressEntity {

    @Id
    @Column(name = "customer_company_id")
    private Long customerCompanyId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "customer_company_id")
    private CustomerCompanyEntity company;

    @Column(name = "zip_code", nullable = false, length = 8)
    private String zipCode;

    @Column(name = "street", nullable = false, length = 160)
    private String street;

    @Column(name = "number", length = 30)
    private String number;

    @Column(name = "complement", length = 120)
    private String complement;

    @Column(name = "neighborhood", length = 120)
    private String neighborhood;

    @Column(name = "city_name", nullable = false, length = 120)
    private String cityName;

    @Column(name = "city_ibge", length = 7)
    private String cityIbge;

    @Column(name = "state_uf", nullable = false, length = 2)
    private String stateUf;

    @Column(name = "country", nullable = false, length = 60)
    private String country;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
