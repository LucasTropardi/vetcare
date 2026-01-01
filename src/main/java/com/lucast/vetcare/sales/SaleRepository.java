package com.lucast.vetcare.sales;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SaleRepository extends JpaRepository<SaleEntity, Long> {
    Optional<SaleEntity> findByAppointmentId(Long appointmentId);
}
