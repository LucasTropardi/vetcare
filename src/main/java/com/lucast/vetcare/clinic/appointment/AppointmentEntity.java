package com.lucast.vetcare.clinic.appointment;

import com.lucast.vetcare.common.enums.AppointmentStatus;
import com.lucast.vetcare.common.enums.AppointmentType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

@Getter @Setter
@Entity
@Table(name = "appointments")
public class AppointmentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="pet_id", nullable = false)
    private Long petId;

    @Column(name="veterinarian_user_id")
    private Long veterinarianUserId;

    @Enumerated(EnumType.STRING)
    @Column(name = "appointment_type", nullable = false, length = 20)
    private AppointmentType appointmentType;

    @Column(name = "scheduled_start_at", nullable = false)
    private OffsetDateTime scheduledStartAt;

    @Column(name = "scheduled_end_at", nullable = false)
    private OffsetDateTime scheduledEndAt;

    @Column(name = "service_product_id")
    private Long serviceProductId;

    @Column(length = 500)
    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AppointmentStatus status;

    @Column(name="opened_at", nullable = false)
    private OffsetDateTime openedAt;

    @Column(name="finished_at")
    private OffsetDateTime finishedAt;

    @Column(name="canceled_at")
    private OffsetDateTime canceledAt;

    @Column(name="cancel_reason", length = 300)
    private String cancelReason;

    @Column(name="created_by")
    private Long createdBy;

    @Column(name="finished_by")
    private Long finishedBy;

    @Column(name="canceled_by")
    private Long canceledBy;

    @Column(name="created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name="updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    void onCreate() {
        var now = OffsetDateTime.now();
        if (openedAt == null) openedAt = now;
        if (appointmentType == null) appointmentType = AppointmentType.VET;
        if (scheduledStartAt == null) scheduledStartAt = openedAt;
        if (scheduledEndAt == null) scheduledEndAt = scheduledStartAt.plusMinutes(30);
        if (status == null) status = AppointmentStatus.OPEN;
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
