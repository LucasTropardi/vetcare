package com.lucast.vetcare.customers.tutor;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Entity
@Table(name = "tutor_address")
@Getter @Setter
public class TutorAddressEntity {

    @Id
    @Column(name = "tutor_id")
    private Long tutorId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "tutor_id")
    private TutorEntity tutor;

    @Column(name = "zip_code", length = 8)
    private String zipCode;

    private String street;

    @Column(name = "number")
    private String number;

    private String complement;
    private String neighborhood;

    @Column(name = "city_name")
    private String cityName;

    @Column(name = "city_ibge")
    private String cityIbge;

    @Column(name = "state_uf")
    private String stateUf;

    private String country;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;
}
