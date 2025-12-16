package com.lucast.vetcare.clinic.medical;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalRecordDiagnosisRepository extends JpaRepository<MedicalRecordDiagnosisEntity, Long> {
    List<MedicalRecordDiagnosisEntity> findByMedicalRecordId(Long medicalRecordId);
}
