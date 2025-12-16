package com.lucast.vetcare.clinic.medical;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordProcedureRepository extends JpaRepository<MedicalRecordProcedureEntity, Long> {
    List<MedicalRecordProcedureEntity> findByMedicalRecordId(Long medicalRecordId);
}
