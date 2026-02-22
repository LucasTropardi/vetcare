package com.lucast.vetcare.clinic.prescription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionRepository extends JpaRepository<PrescriptionEntity, Long> {
    List<PrescriptionEntity> findByAppointmentIdOrderByCreatedAtDesc(Long appointmentId);
}
