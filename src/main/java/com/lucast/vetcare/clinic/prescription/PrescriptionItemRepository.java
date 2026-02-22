package com.lucast.vetcare.clinic.prescription;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PrescriptionItemRepository extends JpaRepository<PrescriptionItemEntity, Long> {
    List<PrescriptionItemEntity> findByPrescriptionIdIn(List<Long> prescriptionIds);
    List<PrescriptionItemEntity> findByPrescriptionId(Long prescriptionId);
}
