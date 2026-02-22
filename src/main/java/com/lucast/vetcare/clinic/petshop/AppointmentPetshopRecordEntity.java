package com.lucast.vetcare.clinic.petshop;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter
@Setter
@Entity
@Table(name = "appointment_petshop_records")
public class AppointmentPetshopRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "appointment_id", nullable = false, unique = true)
    private Long appointmentId;

    @Column(name = "attended_by_user_id")
    private Long attendedByUserId;

    @Column(name = "service_report", columnDefinition = "text")
    private String serviceReport;

    @Column(name = "products_used", columnDefinition = "text")
    private String productsUsed;

    @Column(name = "checkin_notes", columnDefinition = "text")
    private String checkinNotes;

    @Column(name = "checkout_notes", columnDefinition = "text")
    private String checkoutNotes;

    @Column(name = "started_at")
    private OffsetDateTime startedAt;

    @Column(name = "finished_at")
    private OffsetDateTime finishedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
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
