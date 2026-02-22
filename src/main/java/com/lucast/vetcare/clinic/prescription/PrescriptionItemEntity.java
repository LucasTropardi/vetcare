package com.lucast.vetcare.clinic.prescription;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "prescription_items")
public class PrescriptionItemEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "prescription_id", nullable = false)
    private Long prescriptionId;

    @Column(name = "medication_name", nullable = false, length = 180)
    private String medicationName;

    @Column(name = "dosage", length = 140)
    private String dosage;

    @Column(name = "frequency", length = 140)
    private String frequency;

    @Column(name = "duration", length = 140)
    private String duration;

    @Column(name = "route", length = 80)
    private String route;

    @Column(name = "notes", columnDefinition = "text")
    private String notes;
}
