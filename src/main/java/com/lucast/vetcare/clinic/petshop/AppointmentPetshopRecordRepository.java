package com.lucast.vetcare.clinic.petshop;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentPetshopRecordRepository extends JpaRepository<AppointmentPetshopRecordEntity, Long> {
    Optional<AppointmentPetshopRecordEntity> findByAppointmentId(Long appointmentId);
}
