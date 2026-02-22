package com.lucast.vetcare.clinic.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record UpsertMedicalRecordRequest(
        String chiefComplaint,
        String clinicalNotes,
        BigDecimal weightKg,
        BigDecimal temperatureC,
        Integer heartRateBpm,
        Integer respiratoryRateRpm,
        String initialAssessment,
        String diagnosisSummary,
        String treatmentPlan,
        String usedMedications,
        Boolean hospitalizationIndicated,
        String hospitalizationNotes,
        String dischargeInstructions,
        OffsetDateTime followUpAt
) {}
