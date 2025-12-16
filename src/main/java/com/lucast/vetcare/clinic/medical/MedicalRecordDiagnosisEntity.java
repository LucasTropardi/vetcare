package com.lucast.vetcare.clinic.medical;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter @Setter
@Entity
@Table(name = "medical_record_diagnoses")
public class MedicalRecordDiagnosisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="medical_record_id", nullable = false)
    private Long medicalRecordId;

    @Column(length = 30)
    private String code;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(name="is_primary", nullable = false)
    private boolean primary = false;

    @Column(name="created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
