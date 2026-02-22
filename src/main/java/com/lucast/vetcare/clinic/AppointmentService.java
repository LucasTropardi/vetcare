package com.lucast.vetcare.clinic;

import com.lucast.vetcare.auth.AuthContext;
import com.lucast.vetcare.auth.UserRepository;
import com.lucast.vetcare.catalog.ProductRepository;
import com.lucast.vetcare.clinic.appointment.AppointmentEntity;
import com.lucast.vetcare.clinic.appointment.AppointmentRepository;
import com.lucast.vetcare.clinic.dto.*;
import com.lucast.vetcare.clinic.medical.MedicalRecordDiagnosisEntity;
import com.lucast.vetcare.clinic.medical.MedicalRecordDiagnosisRepository;
import com.lucast.vetcare.clinic.medical.MedicalRecordEntity;
import com.lucast.vetcare.clinic.medical.MedicalRecordProcedureEntity;
import com.lucast.vetcare.clinic.medical.MedicalRecordProcedureRepository;
import com.lucast.vetcare.clinic.medical.MedicalRecordRepository;
import com.lucast.vetcare.clinic.petshop.AppointmentPetshopRecordEntity;
import com.lucast.vetcare.clinic.petshop.AppointmentPetshopRecordRepository;
import com.lucast.vetcare.clinic.prescription.PrescriptionEntity;
import com.lucast.vetcare.clinic.prescription.PrescriptionItemEntity;
import com.lucast.vetcare.clinic.prescription.PrescriptionItemRepository;
import com.lucast.vetcare.clinic.prescription.PrescriptionRepository;
import com.lucast.vetcare.common.enums.AppointmentStatus;
import com.lucast.vetcare.common.enums.AppointmentType;
import com.lucast.vetcare.common.enums.ItemType;
import com.lucast.vetcare.common.enums.Role;
import com.lucast.vetcare.customers.pet.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordDiagnosisRepository diagnosisRepository;
    private final MedicalRecordProcedureRepository procedureRepository;
    private final AppointmentPetshopRecordRepository petshopRecordRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PetRepository petRepository,
            UserRepository userRepository,
            ProductRepository productRepository,
            MedicalRecordRepository medicalRecordRepository,
            MedicalRecordDiagnosisRepository diagnosisRepository,
            MedicalRecordProcedureRepository procedureRepository,
            AppointmentPetshopRecordRepository petshopRecordRepository,
            PrescriptionRepository prescriptionRepository,
            PrescriptionItemRepository prescriptionItemRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.procedureRepository = procedureRepository;
        this.petshopRecordRepository = petshopRecordRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
    }

    @Transactional
    public AppointmentResponse open(OpenAppointmentRequest req) {
        var current = AuthContext.requireUser();
        if (current.getRole() != Role.ADMIN && current.getRole() != Role.RECEPTION && current.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        validatePet(req.petId());

        var type = req.appointmentType() != null ? req.appointmentType() : AppointmentType.VET;
        Long vetId = req.veterinarianUserId();
        Long serviceProductId = req.serviceProductId();
        OffsetDateTime scheduledStartAt = req.scheduledStartAt() != null ? req.scheduledStartAt() : OffsetDateTime.now();
        OffsetDateTime scheduledEndAt = req.scheduledEndAt() != null ? req.scheduledEndAt() : scheduledStartAt.plusMinutes(30);

        validateScheduleWindow(scheduledStartAt, scheduledEndAt);

        if (type == AppointmentType.PETSHOP) {
            if (vetId != null) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "PETSHOP appointment cannot have veterinarianUserId");
            }
            if (serviceProductId == null) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "PETSHOP appointment requires serviceProductId");
            }
            if (!isBlank(req.chiefComplaint())) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "chiefComplaint is only valid for VET appointments");
            }
        }

        if (vetId != null) {
            validateVet(vetId);
        }

        if (serviceProductId != null) {
            validateServiceProduct(serviceProductId);
        }

        assertNoScheduleConflict(req.petId(), vetId, scheduledStartAt, scheduledEndAt, null);

        var a = new AppointmentEntity();
        a.setPetId(req.petId());
        a.setAppointmentType(type);
        a.setVeterinarianUserId(vetId);
        a.setServiceProductId(serviceProductId);
        a.setScheduledStartAt(scheduledStartAt);
        a.setScheduledEndAt(scheduledEndAt);
        a.setNotes(blankToNull(req.notes()));
        a.setStatus(AppointmentStatus.OPEN);
        a.setOpenedAt(OffsetDateTime.now());
        a.setCreatedBy(current.getId());

        var saved = appointmentRepository.save(a);

        if (type == AppointmentType.VET) {
            var mr = new MedicalRecordEntity();
            mr.setAppointmentId(saved.getId());
            mr.setChiefComplaint(blankToNull(req.chiefComplaint()));
            medicalRecordRepository.save(mr);
        }

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getById(Long id) {
        var a = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
        return toResponse(a);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> list(
            Long petId,
            Long vetUserId,
            Long serviceProductId,
            AppointmentType appointmentType,
            AppointmentStatus status,
            OffsetDateTime scheduledFrom,
            OffsetDateTime scheduledTo,
            Pageable pageable
    ) {
        Specification<AppointmentEntity> spec = (root, query, cb) -> cb.conjunction();

        if (petId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("petId"), petId));
        }
        if (vetUserId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("veterinarianUserId"), vetUserId));
        }
        if (serviceProductId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("serviceProductId"), serviceProductId));
        }
        if (appointmentType != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("appointmentType"), appointmentType));
        }
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (scheduledFrom != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("scheduledStartAt"), scheduledFrom));
        }
        if (scheduledTo != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("scheduledStartAt"), scheduledTo));
        }

        return appointmentRepository.findAll(spec, pageable).map(this::toResponse);
    }

    @Transactional
    public AppointmentResponse assignVet(Long appointmentId, Long veterinarianUserId) {
        var current = AuthContext.requireUser();
        if (current.getRole() != Role.ADMIN && current.getRole() != Role.RECEPTION) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN/RECEPTION can assign vet");
        }

        var a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (a.getAppointmentType() != AppointmentType.VET) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only VET appointments can assign veterinarian");
        }
        if (a.getStatus() != AppointmentStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only OPEN appointments can be assigned");
        }

        validateVet(veterinarianUserId);
        assertNoScheduleConflict(a.getPetId(), veterinarianUserId, a.getScheduledStartAt(), a.getScheduledEndAt(), a.getId());

        a.setVeterinarianUserId(veterinarianUserId);
        return toResponse(appointmentRepository.save(a));
    }

    @Transactional
    public AppointmentResponse finish(Long appointmentId) {
        var current = AuthContext.requireUser();
        if (current.getRole() != Role.ADMIN && current.getRole() != Role.VET && current.getRole() != Role.RECEPTION) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN/VET/RECEPTION can finish");
        }

        var a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (a.getStatus() != AppointmentStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only OPEN appointments can be finished");
        }

        if (current.getRole() == Role.VET && a.getAppointmentType() == AppointmentType.VET) {
            if (a.getVeterinarianUserId() != null && !a.getVeterinarianUserId().equals(current.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the assigned veterinarian");
            }
        }

        a.setStatus(AppointmentStatus.FINISHED);
        a.setFinishedAt(OffsetDateTime.now());
        a.setFinishedBy(current.getId());

        return toResponse(appointmentRepository.save(a));
    }

    @Transactional
    public AppointmentResponse cancel(Long appointmentId, CancelAppointmentRequest req) {
        var current = AuthContext.requireUser();
        if (current.getRole() != Role.ADMIN && current.getRole() != Role.RECEPTION && current.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN/RECEPTION/VET can cancel");
        }

        var a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (a.getStatus() != AppointmentStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only OPEN appointments can be canceled");
        }

        a.setStatus(AppointmentStatus.CANCELED);
        a.setCanceledAt(OffsetDateTime.now());
        a.setCanceledBy(current.getId());
        a.setCancelReason(req.reason().trim());

        return toResponse(appointmentRepository.save(a));
    }

    @Transactional
    public MedicalRecordResponse upsertMedicalRecord(Long appointmentId, UpsertMedicalRecordRequest req) {
        var current = requireVetOrAdminAndAssigned(appointmentId);

        var mr = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseGet(() -> {
                    var m = new MedicalRecordEntity();
                    m.setAppointmentId(appointmentId);
                    return m;
                });

        if (req.chiefComplaint() != null) mr.setChiefComplaint(req.chiefComplaint());
        if (req.clinicalNotes() != null) mr.setClinicalNotes(req.clinicalNotes());
        if (req.weightKg() != null) mr.setWeightKg(req.weightKg());
        if (req.temperatureC() != null) mr.setTemperatureC(req.temperatureC());
        if (req.heartRateBpm() != null) mr.setHeartRateBpm(req.heartRateBpm());
        if (req.respiratoryRateRpm() != null) mr.setRespiratoryRateRpm(req.respiratoryRateRpm());
        if (req.initialAssessment() != null) mr.setInitialAssessment(req.initialAssessment());
        if (req.diagnosisSummary() != null) mr.setDiagnosisSummary(req.diagnosisSummary());
        if (req.treatmentPlan() != null) mr.setTreatmentPlan(req.treatmentPlan());
        if (req.usedMedications() != null) mr.setUsedMedications(req.usedMedications());
        if (req.hospitalizationIndicated() != null) mr.setHospitalizationIndicated(req.hospitalizationIndicated());
        if (req.hospitalizationNotes() != null) mr.setHospitalizationNotes(req.hospitalizationNotes());
        if (req.dischargeInstructions() != null) mr.setDischargeInstructions(req.dischargeInstructions());
        if (req.followUpAt() != null) mr.setFollowUpAt(req.followUpAt());
        mr.setAttendedByUserId(current.getId());

        var saved = medicalRecordRepository.save(mr);
        return toMedicalRecordResponse(saved);
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponse getMedicalRecord(Long appointmentId) {
        var a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (a.getAppointmentType() != AppointmentType.VET) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Medical record is available only for VET appointments");
        }

        var mr = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found"));
        return toMedicalRecordResponse(mr);
    }

    @Transactional(readOnly = true)
    public PetshopRecordResponse getPetshopRecord(Long appointmentId) {
        var current = AuthContext.requireUser();
        if (current.getRole() != Role.ADMIN && current.getRole() != Role.RECEPTION && current.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        var appointment = requirePetshopAppointment(appointmentId, false);
        if (appointment.getStatus() != AppointmentStatus.OPEN && appointment.getStatus() != AppointmentStatus.FINISHED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Petshop record is available only for OPEN/FINISHED appointments");
        }

        var record = petshopRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Petshop record not found"));

        return toPetshopRecordResponse(record);
    }

    @Transactional
    public PetshopRecordResponse upsertPetshopRecord(Long appointmentId, UpsertPetshopRecordRequest req) {
        var current = AuthContext.requireUser();
        if (current.getRole() != Role.ADMIN && current.getRole() != Role.RECEPTION && current.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN/RECEPTION/VET can edit petshop record");
        }

        var appointment = requirePetshopAppointment(appointmentId, true);
        if (appointment.getStatus() != AppointmentStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only OPEN appointments can be edited");
        }

        var record = petshopRecordRepository.findByAppointmentId(appointmentId)
                .orElseGet(() -> {
                    var r = new AppointmentPetshopRecordEntity();
                    r.setAppointmentId(appointmentId);
                    return r;
                });

        if (req.serviceReport() != null) record.setServiceReport(req.serviceReport());
        if (req.productsUsed() != null) record.setProductsUsed(req.productsUsed());
        if (req.checkinNotes() != null) record.setCheckinNotes(req.checkinNotes());
        if (req.checkoutNotes() != null) record.setCheckoutNotes(req.checkoutNotes());
        if (req.startedAt() != null) record.setStartedAt(req.startedAt());
        if (req.finishedAt() != null) record.setFinishedAt(req.finishedAt());
        record.setAttendedByUserId(current.getId());

        var saved = petshopRecordRepository.save(record);
        return toPetshopRecordResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PrescriptionResponse> listPrescriptions(Long appointmentId) {
        requireVetAppointmentRead(appointmentId);

        var prescriptions = prescriptionRepository.findByAppointmentIdOrderByCreatedAtDesc(appointmentId);
        if (prescriptions.isEmpty()) {
            return List.of();
        }

        List<Long> prescriptionIds = prescriptions.stream().map(PrescriptionEntity::getId).toList();
        var items = prescriptionItemRepository.findByPrescriptionIdIn(prescriptionIds);

        Map<Long, List<PrescriptionItemEntity>> itemsByPrescriptionId = new HashMap<>();
        for (var item : items) {
            itemsByPrescriptionId.computeIfAbsent(item.getPrescriptionId(), k -> new ArrayList<>()).add(item);
        }

        return prescriptions.stream()
                .map(p -> toPrescriptionResponse(p, itemsByPrescriptionId.getOrDefault(p.getId(), List.of())))
                .toList();
    }

    @Transactional
    public PrescriptionResponse createPrescription(Long appointmentId, CreatePrescriptionRequest req) {
        var current = requireVetOrAdminAndAssigned(appointmentId);

        var p = new PrescriptionEntity();
        p.setAppointmentId(appointmentId);
        p.setVeterinarianUserId(current.getRole() == Role.VET ? current.getId() : Optional.ofNullable(
                appointmentRepository.findById(appointmentId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"))
                        .getVeterinarianUserId()
        ).orElse(current.getId()));
        p.setTitle(blankToNull(req.title()));
        p.setGuidance(blankToNull(req.guidance()));
        p.setValidUntil(req.validUntil());

        var savedPrescription = prescriptionRepository.save(p);

        var entities = req.items().stream().map(item -> {
            var e = new PrescriptionItemEntity();
            e.setPrescriptionId(savedPrescription.getId());
            e.setMedicationName(item.medicationName().trim());
            e.setDosage(blankToNull(item.dosage()));
            e.setFrequency(blankToNull(item.frequency()));
            e.setDuration(blankToNull(item.duration()));
            e.setRoute(blankToNull(item.route()));
            e.setNotes(blankToNull(item.notes()));
            return e;
        }).toList();

        var savedItems = prescriptionItemRepository.saveAll(entities);

        return toPrescriptionResponse(savedPrescription, savedItems);
    }

    @Transactional
    public DiagnosisResponse addDiagnosis(Long appointmentId, AddDiagnosisRequest req) {
        requireVetOrAdminAndAssigned(appointmentId);

        var mr = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found"));

        var d = new MedicalRecordDiagnosisEntity();
        d.setMedicalRecordId(mr.getId());
        d.setCode(blankToNull(req.code()));
        d.setDescription(req.description().trim());
        d.setPrimary(req.primary() != null && req.primary());

        var saved = diagnosisRepository.save(d);
        return toDiagnosisResponse(saved);
    }

    @Transactional
    public void deleteDiagnosis(Long appointmentId, Long diagnosisId) {
        requireVetOrAdminAndAssigned(appointmentId);

        var mr = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found"));

        var d = diagnosisRepository.findById(diagnosisId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Diagnosis not found"));

        if (!d.getMedicalRecordId().equals(mr.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Diagnosis does not belong to this appointment");
        }

        diagnosisRepository.delete(d);
    }

    @Transactional
    public ProcedureResponse addProcedure(Long appointmentId, AddProcedureRequest req) {
        requireVetOrAdminAndAssigned(appointmentId);

        var mr = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found"));

        var p = new MedicalRecordProcedureEntity();
        p.setMedicalRecordId(mr.getId());
        p.setDescription(req.description().trim());
        p.setNotes(blankToNull(req.notes()));
        p.setPerformedAt(req.performedAt());

        var saved = procedureRepository.save(p);
        return toProcedureResponse(saved);
    }

    @Transactional
    public void deleteProcedure(Long appointmentId, Long procedureId) {
        requireVetOrAdminAndAssigned(appointmentId);

        var mr = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found"));

        var p = procedureRepository.findById(procedureId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Procedure not found"));

        if (!p.getMedicalRecordId().equals(mr.getId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Procedure does not belong to this appointment");
        }

        procedureRepository.delete(p);
    }

    private com.lucast.vetcare.auth.UserEntity requireVetOrAdminAndAssigned(Long appointmentId) {
        var current = AuthContext.requireUser();
        if (current.getRole() != Role.ADMIN && current.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN/VET can edit medical record");
        }

        var a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (a.getAppointmentType() != AppointmentType.VET) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Medical record is available only for VET appointments");
        }

        if (a.getStatus() != AppointmentStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only OPEN appointments can be edited");
        }

        if (current.getRole() == Role.VET && a.getVeterinarianUserId() != null && !a.getVeterinarianUserId().equals(current.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the assigned veterinarian");
        }
        return current;
    }

    private AppointmentEntity requirePetshopAppointment(Long appointmentId, boolean writable) {
        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (appointment.getAppointmentType() != AppointmentType.PETSHOP) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Petshop record is available only for PETSHOP appointments");
        }

        if (writable && appointment.getStatus() != AppointmentStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only OPEN appointments can be edited");
        }

        return appointment;
    }

    private void requireVetAppointmentRead(Long appointmentId) {
        var current = AuthContext.requireUser();
        if (current.getRole() != Role.ADMIN && current.getRole() != Role.VET && current.getRole() != Role.RECEPTION) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (appointment.getAppointmentType() != AppointmentType.VET) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Prescription is available only for VET appointments");
        }

        if (current.getRole() == Role.VET && appointment.getVeterinarianUserId() != null && !appointment.getVeterinarianUserId().equals(current.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the assigned veterinarian");
        }
    }

    private AppointmentResponse toResponse(AppointmentEntity a) {
        return new AppointmentResponse(
                a.getId(),
                a.getPetId(),
                a.getAppointmentType(),
                a.getVeterinarianUserId(),
                a.getServiceProductId(),
                a.getStatus(),
                a.getScheduledStartAt(),
                a.getScheduledEndAt(),
                a.getNotes(),
                a.getOpenedAt(),
                a.getFinishedAt(),
                a.getCanceledAt(),
                a.getCancelReason(),
                a.getCreatedBy(),
                a.getFinishedBy(),
                a.getCanceledBy(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }

    private MedicalRecordResponse toMedicalRecordResponse(MedicalRecordEntity m) {
        return new MedicalRecordResponse(
                m.getId(),
                m.getAppointmentId(),
                m.getChiefComplaint(),
                m.getClinicalNotes(),
                m.getAttendedByUserId(),
                m.getWeightKg(),
                m.getTemperatureC(),
                m.getHeartRateBpm(),
                m.getRespiratoryRateRpm(),
                m.getInitialAssessment(),
                m.getDiagnosisSummary(),
                m.getTreatmentPlan(),
                m.getUsedMedications(),
                m.getHospitalizationIndicated(),
                m.getHospitalizationNotes(),
                m.getDischargeInstructions(),
                m.getFollowUpAt(),
                m.getCreatedAt(),
                m.getUpdatedAt()
        );
    }

    private PetshopRecordResponse toPetshopRecordResponse(AppointmentPetshopRecordEntity r) {
        return new PetshopRecordResponse(
                r.getId(),
                r.getAppointmentId(),
                r.getAttendedByUserId(),
                r.getServiceReport(),
                r.getProductsUsed(),
                r.getCheckinNotes(),
                r.getCheckoutNotes(),
                r.getStartedAt(),
                r.getFinishedAt(),
                r.getCreatedAt(),
                r.getUpdatedAt()
        );
    }

    private PrescriptionResponse toPrescriptionResponse(PrescriptionEntity p, List<PrescriptionItemEntity> items) {
        return new PrescriptionResponse(
                p.getId(),
                p.getAppointmentId(),
                p.getVeterinarianUserId(),
                p.getTitle(),
                p.getGuidance(),
                p.getValidUntil(),
                p.getCreatedAt(),
                items.stream().map(i -> new PrescriptionItemResponse(
                        i.getId(),
                        i.getMedicationName(),
                        i.getDosage(),
                        i.getFrequency(),
                        i.getDuration(),
                        i.getRoute(),
                        i.getNotes()
                )).toList()
        );
    }

    private DiagnosisResponse toDiagnosisResponse(MedicalRecordDiagnosisEntity d) {
        return new DiagnosisResponse(
                d.getId(),
                d.getMedicalRecordId(),
                d.getCode(),
                d.getDescription(),
                d.isPrimary(),
                d.getCreatedAt()
        );
    }

    private ProcedureResponse toProcedureResponse(MedicalRecordProcedureEntity p) {
        return new ProcedureResponse(
                p.getId(),
                p.getMedicalRecordId(),
                p.getDescription(),
                p.getNotes(),
                p.getPerformedAt(),
                p.getCreatedAt()
        );
    }

    private void validatePet(Long petId) {
        var pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));
        if (!pet.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Inactive pet cannot be attended");
        }
    }

    private void validateVet(Long vetId) {
        var vet = userRepository.findById(vetId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found"));
        if (!vet.isActive() || vet.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "veterinarianUserId must be an active VET");
        }
    }

    private void validateServiceProduct(Long serviceProductId) {
        var service = productRepository.findById(serviceProductId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Service product not found"));
        if (!service.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Inactive service cannot be used");
        }
        if (service.getItemType() != ItemType.SERVICE) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "serviceProductId must reference an active SERVICE item");
        }
    }

    private void validateScheduleWindow(OffsetDateTime scheduledStartAt, OffsetDateTime scheduledEndAt) {
        if (scheduledEndAt.isBefore(scheduledStartAt) || scheduledEndAt.isEqual(scheduledStartAt)) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "scheduledEndAt must be after scheduledStartAt");
        }
    }

    private void assertNoScheduleConflict(
            Long petId,
            Long vetId,
            OffsetDateTime scheduledStartAt,
            OffsetDateTime scheduledEndAt,
            Long excludeAppointmentId
    ) {
        boolean petConflict = appointmentRepository.existsOverlappingPetSchedule(
                petId, scheduledStartAt, scheduledEndAt, excludeAppointmentId
        );
        if (petConflict) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pet already has an overlapping OPEN appointment");
        }

        if (vetId != null) {
            boolean vetConflict = appointmentRepository.existsOverlappingVetSchedule(
                    vetId, scheduledStartAt, scheduledEndAt, excludeAppointmentId
            );
            if (vetConflict) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Veterinarian already has an overlapping OPEN appointment");
            }
        }
    }

    private String blankToNull(String s) {
        if (s == null) return null;
        var t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
