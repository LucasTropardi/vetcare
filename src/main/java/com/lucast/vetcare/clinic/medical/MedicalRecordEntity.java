package com.lucast.vetcare.clinic.medical;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Getter @Setter
@Entity
@Table(name = "medical_records")
public class MedicalRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="appointment_id", nullable = false, unique = true)
    private Long appointmentId;

    @Column(name="chief_complaint", columnDefinition = "text")
    private String chiefComplaint;

    @Column(name="clinical_notes", columnDefinition = "text")
    private String clinicalNotes;

    @Column(name = "attended_by_user_id")
    private Long attendedByUserId;

    @Column(name = "weight_kg", precision = 8, scale = 3)
    private BigDecimal weightKg;

    @Column(name = "temperature_c", precision = 4, scale = 1)
    private BigDecimal temperatureC;

    @Column(name = "heart_rate_bpm")
    private Integer heartRateBpm;

    @Column(name = "respiratory_rate_rpm")
    private Integer respiratoryRateRpm;

    @Column(name = "initial_assessment", columnDefinition = "text")
    private String initialAssessment;

    @Column(name = "diagnosis_summary", columnDefinition = "text")
    private String diagnosisSummary;

    @Column(name = "treatment_plan", columnDefinition = "text")
    private String treatmentPlan;

    @Column(name = "used_medications", columnDefinition = "text")
    private String usedMedications;

    @Column(name = "hospitalization_indicated")
    private Boolean hospitalizationIndicated;

    @Column(name = "hospitalization_notes", columnDefinition = "text")
    private String hospitalizationNotes;

    @Column(name = "discharge_instructions", columnDefinition = "text")
    private String dischargeInstructions;

    @Column(name = "follow_up_at")
    private OffsetDateTime followUpAt;

    @Column(name="created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name="updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        var now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
