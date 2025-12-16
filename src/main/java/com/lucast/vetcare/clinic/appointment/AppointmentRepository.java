package com.lucast.vetcare.clinic.appointment;

import com.lucast.vetcare.common.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long> {
    Optional<AppointmentEntity> findFirstByPetIdAndStatus(Long petId, AppointmentStatus status);
    Page<AppointmentEntity> findByStatus(AppointmentStatus status, Pageable pageable);
    Page<AppointmentEntity> findByPetId(Long petId, Pageable pageable);
    Page<AppointmentEntity> findByVeterinarianUserId(Long vetUserId, Pageable pageable);
}
