package com.lucast.vetcare.clinic.dto;

public record UpsertMedicalRecordRequest(
        String chiefComplaint,
        String clinicalNotes
) {}
