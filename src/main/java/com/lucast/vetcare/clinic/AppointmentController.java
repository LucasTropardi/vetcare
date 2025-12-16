package com.lucast.vetcare.clinic;

import com.lucast.vetcare.clinic.dto.*;
import com.lucast.vetcare.common.enums.AppointmentStatus;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService service;

    public AppointmentController(AppointmentService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AppointmentResponse open(@RequestBody @Valid OpenAppointmentRequest req) {
        return service.open(req);
    }

    @GetMapping("/{id}")
    public AppointmentResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    public Page<AppointmentResponse> list(
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long vetUserId,
            @RequestParam(required = false) AppointmentStatus status,
            Pageable pageable
    ) {
        return service.list(petId, vetUserId, status, pageable);
    }

    @PatchMapping("/{id}/assign-vet")
    public AppointmentResponse assignVet(
            @PathVariable Long id,
            @RequestParam Long veterinarianUserId
    ) {
        return service.assignVet(id, veterinarianUserId);
    }

    @PatchMapping("/{id}/finish")
    public AppointmentResponse finish(@PathVariable Long id) {
        return service.finish(id);
    }

    @PatchMapping("/{id}/cancel")
    public AppointmentResponse cancel(@PathVariable Long id, @RequestBody @Valid CancelAppointmentRequest req) {
        return service.cancel(id, req);
    }

    // --------- prontuário ---------

    @GetMapping("/{id}/medical-record")
    public MedicalRecordResponse getMedicalRecord(@PathVariable Long id) {
        return service.getMedicalRecord(id);
    }

    @PutMapping("/{id}/medical-record")
    public MedicalRecordResponse upsertMedicalRecord(@PathVariable Long id, @RequestBody @Valid UpsertMedicalRecordRequest req) {
        return service.upsertMedicalRecord(id, req);
    }

    @PostMapping("/{id}/diagnoses")
    @ResponseStatus(HttpStatus.CREATED)
    public DiagnosisResponse addDiagnosis(@PathVariable Long id, @RequestBody @Valid AddDiagnosisRequest req) {
        return service.addDiagnosis(id, req);
    }

    @DeleteMapping("/{id}/diagnoses/{diagnosisId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDiagnosis(@PathVariable Long id, @PathVariable Long diagnosisId) {
        service.deleteDiagnosis(id, diagnosisId);
    }

    @PostMapping("/{id}/procedures")
    @ResponseStatus(HttpStatus.CREATED)
    public ProcedureResponse addProcedure(@PathVariable Long id, @RequestBody @Valid AddProcedureRequest req) {
        return service.addProcedure(id, req);
    }

    @DeleteMapping("/{id}/procedures/{procedureId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProcedure(@PathVariable Long id, @PathVariable Long procedureId) {
        service.deleteProcedure(id, procedureId);
    }
}
