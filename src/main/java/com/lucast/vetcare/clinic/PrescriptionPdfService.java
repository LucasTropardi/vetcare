package com.lucast.vetcare.clinic;

import com.lucast.vetcare.auth.AuthContext;
import com.lucast.vetcare.auth.UserRepository;
import com.lucast.vetcare.clinic.appointment.AppointmentRepository;
import com.lucast.vetcare.clinic.prescription.PrescriptionItemRepository;
import com.lucast.vetcare.clinic.prescription.PrescriptionRepository;
import com.lucast.vetcare.common.enums.AppointmentType;
import com.lucast.vetcare.common.enums.Role;
import com.lucast.vetcare.company.CompanyService;
import com.lucast.vetcare.customers.pet.PetRepository;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.InputStream;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PrescriptionPdfService {

    private static final DateTimeFormatter DATE_TIME_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm").withZone(ZoneId.systemDefault());
    private static final DateTimeFormatter DATE_BR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final PrescriptionItemRepository prescriptionItemRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final CompanyService companyService;

    private volatile JasperReport report;

    public PrescriptionPdfService(
            AppointmentRepository appointmentRepository,
            PrescriptionRepository prescriptionRepository,
            PrescriptionItemRepository prescriptionItemRepository,
            PetRepository petRepository,
            UserRepository userRepository,
            CompanyService companyService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.prescriptionItemRepository = prescriptionItemRepository;
        this.petRepository = petRepository;
        this.userRepository = userRepository;
        this.companyService = companyService;
    }

    @Transactional(readOnly = true)
    public byte[] generatePrescriptionPdf(Long appointmentId, Long prescriptionId) {
        var current = AuthContext.requireUser();
        if (current.getRole() != Role.ADMIN && current.getRole() != Role.VET && current.getRole() != Role.RECEPTION) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        var appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));

        if (appointment.getAppointmentType() != AppointmentType.VET) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Prescription is available only for VET appointments");
        }

        if (current.getRole() == Role.VET && appointment.getVeterinarianUserId() != null
                && !appointment.getVeterinarianUserId().equals(current.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You are not the assigned veterinarian");
        }

        var prescription = prescriptionRepository.findById(prescriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Prescription not found"));

        if (!prescription.getAppointmentId().equals(appointmentId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Prescription does not belong to this appointment");
        }

        var items = prescriptionItemRepository.findByPrescriptionId(prescriptionId);
        if (items.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Prescription has no items");
        }

        var pet = petRepository.findById(appointment.getPetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));

        var vet = userRepository.findById(prescription.getVeterinarianUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Veterinarian not found"));

        var company = companyService.getCurrentProfile();

        Map<String, Object> params = new HashMap<>();
        params.put("LOGO_IMAGE", loadImage("/images/report-logo.png"));
        params.put("SIGNATURE_IMAGE", decodeBase64Image(vet.getSignatureImageBase64()));
        params.put("COMPANY_NAME", company.tradeName() != null && !company.tradeName().isBlank() ? company.tradeName() : company.legalName());
        params.put("COMPANY_CNPJ", company.cnpj());
        params.put("COMPANY_PHONE", company.phone());
        params.put("COMPANY_EMAIL", company.email());

        params.put("PRESCRIPTION_TITLE", prescription.getTitle());
        params.put("PRESCRIPTION_CREATED_AT", DATE_TIME_BR.format(prescription.getCreatedAt()));
        params.put("PRESCRIPTION_VALID_UNTIL", prescription.getValidUntil() == null ? null : DATE_BR.format(prescription.getValidUntil()));
        params.put("PRESCRIPTION_GUIDANCE", prescription.getGuidance());

        params.put("PET_NAME", pet.getName());
        params.put("PET_SPECIES", pet.getSpecies() == null ? null : pet.getSpecies().name());
        params.put("TUTOR_NAME", pet.getTutor() == null ? null : pet.getTutor().getName());
        params.put("VET_NAME", vet.getName());
        params.put("VET_LICENSE", vet.getProfessionalLicense());

        List<PrescriptionItemRow> rows = items.stream().map(item -> new PrescriptionItemRow(
                item.getMedicationName(),
                item.getDosage(),
                item.getFrequency(),
                item.getDuration(),
                item.getRoute(),
                item.getNotes()
        )).toList();

        try {
            JasperPrint print = JasperFillManager.fillReport(
                    loadReport(),
                    params,
                    new JRBeanCollectionDataSource(rows)
            );
            return JasperExportManager.exportReportToPdf(print);
        } catch (JRException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to generate prescription PDF: " + e.getMessage());
        }
    }

    private JasperReport loadReport() {
        if (report != null) return report;
        synchronized (this) {
            if (report != null) return report;
            try (InputStream in = PrescriptionPdfService.class.getResourceAsStream("/jrxml/clinic/prescription.jrxml")) {
                if (in == null) {
                    throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Prescription template not found");
                }
                report = JasperCompileManager.compileReport(in);
                return report;
            } catch (Exception e) {
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to compile prescription template: " + e.getMessage());
            }
        }
    }

    private Image loadImage(String classpath) {
        try (InputStream in = PrescriptionPdfService.class.getResourceAsStream(classpath)) {
            if (in == null) return null;
            return ImageIO.read(in);
        } catch (Exception e) {
            return null;
        }
    }

    private Image decodeBase64Image(String base64) {
        if (base64 == null || base64.isBlank()) return null;
        try {
            byte[] bytes = Base64.getDecoder().decode(base64);
            return ImageIO.read(new java.io.ByteArrayInputStream(bytes));
        } catch (Exception e) {
            return null;
        }
    }

    public static class PrescriptionItemRow {
        private final String medicationName;
        private final String dosage;
        private final String frequency;
        private final String duration;
        private final String route;
        private final String notes;

        public PrescriptionItemRow(String medicationName, String dosage, String frequency, String duration, String route, String notes) {
            this.medicationName = medicationName;
            this.dosage = dosage;
            this.frequency = frequency;
            this.duration = duration;
            this.route = route;
            this.notes = notes;
        }

        public String getMedicationName() {
            return medicationName;
        }

        public String getDosage() {
            return dosage;
        }

        public String getFrequency() {
            return frequency;
        }

        public String getDuration() {
            return duration;
        }

        public String getRoute() {
            return route;
        }

        public String getNotes() {
            return notes;
        }
    }
}
