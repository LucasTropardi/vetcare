package com.lucast.vetcare.clinic.medical;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecordEntity, Long> {
    Optional<MedicalRecordEntity> findByAppointmentId(Long appointmentId);
}
