package com.lucast.vetcare.clinic.medical;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter @Setter
@Entity
@Table(name = "medical_record_procedures")
public class MedicalRecordProcedureEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="medical_record_id", nullable = false)
    private Long medicalRecordId;

    @Column(nullable = false, length = 300)
    private String description;

    @Column(columnDefinition = "text")
    private String notes;

    @Column(name="performed_at", nullable = false)
    private OffsetDateTime performedAt;

    @Column(name="created_at", nullable = false)
    private OffsetDateTime createdAt;

    @PrePersist
    void onCreate() {
        var now = OffsetDateTime.now();
        if (performedAt == null) performedAt = now;
        createdAt = now;
    }
}
