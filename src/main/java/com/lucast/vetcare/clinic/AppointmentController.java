package com.lucast.vetcare.clinic;

import com.lucast.vetcare.clinic.dto.*;
import com.lucast.vetcare.common.enums.AppointmentStatus;
import com.lucast.vetcare.common.enums.AppointmentType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@Tag(
        name = "Appointments",
        description = "Operations related to VET/PETSHOP appointments and medical records"
)
public class AppointmentController {

    private final AppointmentService service;
    private final PrescriptionPdfService prescriptionPdfService;

    public AppointmentController(AppointmentService service, PrescriptionPdfService prescriptionPdfService) {
        this.service = service;
        this.prescriptionPdfService = prescriptionPdfService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Open appointment",
            description = "Create a new appointment"
    )
    public AppointmentResponse open(@RequestBody @Valid OpenAppointmentRequest req) {
        return service.open(req);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get appointment by ID",
            description = "Retrieve an appointment by its ID"
    )
    public AppointmentResponse getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @GetMapping
    @Operation(
            summary = "List appointments",
            description = "List appointments with pagination, sorting and optional filters"
    )
    public Page<AppointmentResponse> list(
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) Long vetUserId,
            @RequestParam(required = false) Long serviceProductId,
            @RequestParam(required = false) AppointmentType appointmentType,
            @RequestParam(required = false) AppointmentStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime scheduledFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) OffsetDateTime scheduledTo,
            @ParameterObject Pageable pageable
    ) {
        return service.list(petId, vetUserId, serviceProductId, appointmentType, status, scheduledFrom, scheduledTo, pageable);
    }

    @PatchMapping("/{id}/assign-vet")
    @Operation(
            summary = "Assign veterinarian",
            description = "Assign a veterinarian to an appointment"
    )
    public AppointmentResponse assignVet(
            @PathVariable Long id,
            @RequestParam Long veterinarianUserId
    ) {
        return service.assignVet(id, veterinarianUserId);
    }

    @PatchMapping("/{id}/finish")
    @Operation(
            summary = "Finish appointment",
            description = "Finish an appointment"
    )
    public AppointmentResponse finish(@PathVariable Long id) {
        return service.finish(id);
    }

    @PatchMapping("/{id}/cancel")
    @Operation(
            summary = "Cancel appointment",
            description = "Cancel an appointment"
    )
    public AppointmentResponse cancel(
            @PathVariable Long id,
            @RequestBody @Valid CancelAppointmentRequest req
    ) {
        return service.cancel(id, req);
    }

    @GetMapping("/{id}/medical-record")
    @Operation(
            summary = "Get medical record",
            description = "Retrieve the medical record of an appointment"
    )
    public MedicalRecordResponse getMedicalRecord(@PathVariable Long id) {
        return service.getMedicalRecord(id);
    }

    @PutMapping("/{id}/medical-record")
    @Operation(
            summary = "Upsert medical record",
            description = "Create or update the medical record of an appointment"
    )
    public MedicalRecordResponse upsertMedicalRecord(
            @PathVariable Long id,
            @RequestBody @Valid UpsertMedicalRecordRequest req
    ) {
        return service.upsertMedicalRecord(id, req);
    }

    @GetMapping("/{id}/petshop-record")
    @Operation(
            summary = "Get petshop record",
            description = "Retrieve the petshop execution record of an appointment"
    )
    public PetshopRecordResponse getPetshopRecord(@PathVariable Long id) {
        return service.getPetshopRecord(id);
    }

    @PutMapping("/{id}/petshop-record")
    @Operation(
            summary = "Upsert petshop record",
            description = "Create or update the petshop execution record of an appointment"
    )
    public PetshopRecordResponse upsertPetshopRecord(
            @PathVariable Long id,
            @RequestBody @Valid UpsertPetshopRecordRequest req
    ) {
        return service.upsertPetshopRecord(id, req);
    }

    @GetMapping("/{id}/prescriptions")
    @Operation(
            summary = "List prescriptions",
            description = "List prescriptions linked to a VET appointment"
    )
    public List<PrescriptionResponse> listPrescriptions(@PathVariable Long id) {
        return service.listPrescriptions(id);
    }

    @PostMapping("/{id}/prescriptions")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create prescription",
            description = "Create a prescription for a VET appointment"
    )
    public PrescriptionResponse createPrescription(
            @PathVariable Long id,
            @RequestBody @Valid CreatePrescriptionRequest req
    ) {
        return service.createPrescription(id, req);
    }

    @GetMapping("/{appointmentId}/prescriptions/{prescriptionId}/pdf")
    @Operation(
            summary = "Export prescription PDF",
            description = "Generate a PDF for a specific prescription"
    )
    public ResponseEntity<byte[]> exportPrescriptionPdf(
            @PathVariable Long appointmentId,
            @PathVariable Long prescriptionId,
            @RequestParam(defaultValue = "inline") String disposition
    ) {
        byte[] bytes = prescriptionPdfService.generatePrescriptionPdf(appointmentId, prescriptionId);

        String mode = "attachment".equalsIgnoreCase(disposition) ? "attachment" : "inline";
        String filename = "receita-" + appointmentId + "-" + prescriptionId + ".pdf";

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, mode + "; filename=\"" + filename + "\"")
                .body(bytes);
    }

    @PostMapping("/{id}/diagnoses")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Add diagnosis",
            description = "Add a diagnosis to an appointment medical record"
    )
    public DiagnosisResponse addDiagnosis(
            @PathVariable Long id,
            @RequestBody @Valid AddDiagnosisRequest req
    ) {
        return service.addDiagnosis(id, req);
    }

    @DeleteMapping("/{id}/diagnoses/{diagnosisId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete diagnosis",
            description = "Delete a diagnosis from an appointment medical record"
    )
    public void deleteDiagnosis(
            @PathVariable Long id,
            @PathVariable Long diagnosisId
    ) {
        service.deleteDiagnosis(id, diagnosisId);
    }

    @PostMapping("/{id}/procedures")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Add procedure",
            description = "Add a procedure to an appointment medical record"
    )
    public ProcedureResponse addProcedure(
            @PathVariable Long id,
            @RequestBody @Valid AddProcedureRequest req
    ) {
        return service.addProcedure(id, req);
    }

    @DeleteMapping("/{id}/procedures/{procedureId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete procedure",
            description = "Delete a procedure from an appointment medical record"
    )
    public void deleteProcedure(
            @PathVariable Long id,
            @PathVariable Long procedureId
    ) {
        service.deleteProcedure(id, procedureId);
    }
}
