package com.lucast.vetcare.clinic;

import com.lucast.vetcare.auth.AuthContext;
import com.lucast.vetcare.auth.UserRepository;
import com.lucast.vetcare.clinic.appointment.AppointmentEntity;
import com.lucast.vetcare.clinic.appointment.AppointmentRepository;
import com.lucast.vetcare.clinic.dto.*;
import com.lucast.vetcare.clinic.medical.*;
import com.lucast.vetcare.common.enums.AppointmentStatus;
import com.lucast.vetcare.common.enums.Role;
import com.lucast.vetcare.customers.pet.PetRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    private final MedicalRecordRepository medicalRecordRepository;
    private final MedicalRecordDiagnosisRepository diagnosisRepository;
    private final MedicalRecordProcedureRepository procedureRepository;

    public AppointmentService(
            AppointmentRepository appointmentRepository,
            PetRepository petRepository,
            UserRepository userRepository,
            MedicalRecordRepository medicalRecordRepository,
            MedicalRecordDiagnosisRepository diagnosisRepository,
            MedicalRecordProcedureRepository procedureRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.medicalRecordRepository = medicalRecordRepository;
        this.diagnosisRepository = diagnosisRepository;
        this.procedureRepository = procedureRepository;
    }

    @Transactional
    public AppointmentResponse open(OpenAppointmentRequest req) {
        var current = AuthContext.requireUser();
        if (current.getRole() != Role.ADMIN && current.getRole() != Role.RECEPTION && current.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        var pet = petRepository.findById(req.petId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));
        if (!pet.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Inactive pet cannot be attended");
        }

        appointmentRepository.findFirstByPetIdAndStatus(req.petId(), AppointmentStatus.OPEN)
                .ifPresent(a -> { throw new ResponseStatusException(HttpStatus.CONFLICT, "There is already an OPEN appointment for this pet"); });

        Long vetId = req.veterinarianUserId();
        if (vetId != null) {
            var vet = userRepository.findById(vetId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found"));
            if (!vet.isActive() || vet.getRole() != Role.VET) {
                throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "veterinarianUserId must be an active VET");
            }
        }

        var a = new AppointmentEntity();
        a.setPetId(req.petId());
        a.setVeterinarianUserId(vetId);
        a.setStatus(AppointmentStatus.OPEN);
        a.setOpenedAt(OffsetDateTime.now());
        a.setCreatedBy(current.getId());

        var saved = appointmentRepository.save(a);

        // cria prontuário vazio na abertura
        var mr = new MedicalRecordEntity();
        mr.setAppointmentId(saved.getId());
        mr.setChiefComplaint(blankToNull(req.chiefComplaint()));
        medicalRecordRepository.save(mr);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public AppointmentResponse getById(Long id) {
        var a = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
        return toResponse(a);
    }

    @Transactional(readOnly = true)
    public Page<AppointmentResponse> list(Long petId, Long vetUserId, AppointmentStatus status, Pageable pageable) {
        Page<AppointmentEntity> page;

        if (petId != null) page = appointmentRepository.findByPetId(petId, pageable);
        else if (vetUserId != null) page = appointmentRepository.findByVeterinarianUserId(vetUserId, pageable);
        else if (status != null) page = appointmentRepository.findByStatus(status, pageable);
        else page = appointmentRepository.findAll(pageable);

        return page.map(this::toResponse);
    }

    @Transactional
    public AppointmentResponse assignVet(Long appointmentId, Long veterinarianUserId) {
        var current = AuthContext.requireUser();
        if (current.getRole() != Role.ADMIN && current.getRole() != Role.RECEPTION) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN/RECEPTION can assign vet");
        }

        var a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (a.getStatus() != AppointmentStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only OPEN appointments can be assigned");
        }

        var vet = userRepository.findById(veterinarianUserId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found"));

        if (!vet.isActive() || vet.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "veterinarianUserId must be an active VET");
        }

        a.setVeterinarianUserId(vet.getId());
        return toResponse(appointmentRepository.save(a));
    }

    @Transactional
    public AppointmentResponse finish(Long appointmentId) {
        var current = AuthContext.requireUser();
        if (current.getRole() != Role.ADMIN && current.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN/VET can finish");
        }

        var a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (a.getStatus() != AppointmentStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only OPEN appointments can be finished");
        }

        // se for VET, o atendimento é atribuído a ele
        if (current.getRole() == Role.VET && a.getVeterinarianUserId() != null && !a.getVeterinarianUserId().equals(current.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the assigned veterinarian");
        }

        a.setStatus(AppointmentStatus.FINISHED);
        a.setFinishedAt(OffsetDateTime.now());
        a.setFinishedBy(current.getId());

        return toResponse(appointmentRepository.save(a));
    }

    @Transactional
    public AppointmentResponse cancel(Long appointmentId, CancelAppointmentRequest req) {
        var current = AuthContext.requireUser();
        if (current.getRole() != Role.ADMIN && current.getRole() != Role.RECEPTION) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN/RECEPTION can cancel");
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

    // PRONTUÁRIO

    @Transactional
    public MedicalRecordResponse upsertMedicalRecord(Long appointmentId, UpsertMedicalRecordRequest req) {
        requireVetOrAdminAndAssigned(appointmentId);

        var mr = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseGet(() -> {
                    var m = new MedicalRecordEntity();
                    m.setAppointmentId(appointmentId);
                    return m;
                });

        if (req.chiefComplaint() != null) mr.setChiefComplaint(req.chiefComplaint());
        if (req.clinicalNotes() != null) mr.setClinicalNotes(req.clinicalNotes());

        var saved = medicalRecordRepository.save(mr);
        return toMedicalRecordResponse(saved);
    }

    @Transactional(readOnly = true)
    public MedicalRecordResponse getMedicalRecord(Long appointmentId) {
        var mr = medicalRecordRepository.findByAppointmentId(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Medical record not found"));
        return toMedicalRecordResponse(mr);
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

    // helpers

    private void requireVetOrAdminAndAssigned(Long appointmentId) {
        var current = AuthContext.requireUser();
        if (current.getRole() != Role.ADMIN && current.getRole() != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN/VET can edit medical record");
        }

        var a = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (a.getStatus() != AppointmentStatus.OPEN) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Only OPEN appointments can be edited");
        }

        if (current.getRole() == Role.VET && a.getVeterinarianUserId() != null && !a.getVeterinarianUserId().equals(current.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the assigned veterinarian");
        }
    }

    private AppointmentResponse toResponse(AppointmentEntity a) {
        return new AppointmentResponse(
                a.getId(),
                a.getPetId(),
                a.getVeterinarianUserId(),
                a.getStatus(),
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
                m.getCreatedAt(),
                m.getUpdatedAt()
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

    private String blankToNull(String s) {
        if (s == null) return null;
        var t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
