package com.lucast.vetcare.customers.pet;

import com.lucast.vetcare.auth.AuthContext;
import com.lucast.vetcare.common.enums.Role;
import com.lucast.vetcare.customers.pet.dto.*;
import com.lucast.vetcare.customers.tutor.TutorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.Set;

@Service
public class PetService {

    private final PetRepository petRepository;
    private final TutorRepository tutorRepository;

    public PetService(PetRepository petRepository, TutorRepository tutorRepository) {
        this.petRepository = petRepository;
        this.tutorRepository = tutorRepository;
    }

    @Transactional
    public PetResponse create(CreatePetRequest req) {
        // garantir que o tutor existe
        tutorRepository.findById(req.tutorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor not found"));

        var now = OffsetDateTime.now();

        var p = new PetEntity();
        p.setTutorId(req.tutorId());
        p.setName(req.name());
        p.setSpecies(req.species());
        p.setBreed(req.breed());
        p.setSex(req.sex());
        p.setBirthDate(req.birthDate());
        p.setWeightKg(req.weightKg());
        p.setNotes(req.notes());
        p.setActive(true);
        p.setCreatedAt(now);
        p.setUpdatedAt(now);

        return toResponse(petRepository.save(p));
    }

    @Transactional(readOnly = true)
    public PetResponse getById(Long id) {
        var p = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));
        return toResponse(p);
    }

    @Transactional(readOnly = true)
    public Page<PetListItemResponse> list(Long tutorId, String query, Pageable pageable) {
        boolean hasTutor = tutorId != null;
        boolean hasQuery = query != null && !query.isBlank();
        String q = hasQuery ? query.trim() : null;

        Page<PetEntity> page;
        if (hasTutor && hasQuery) {
            page = petRepository.findByActiveTrueAndTutorIdAndNameContainingIgnoreCase(tutorId, q, pageable);
        } else if (hasTutor) {
            page = petRepository.findByActiveTrueAndTutorId(tutorId, pageable);
        } else if (hasQuery) {
            page = petRepository.findByActiveTrueAndNameContainingIgnoreCase(q, pageable);
        } else {
            page = petRepository.findByActiveTrue(pageable);
        }

        return page.map(this::toListItem);
    }

    @Transactional
    public PetResponse update(Long id, UpdatePetRequest req) {

        tutorRepository.findById(req.tutorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor not found"));

        var p = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));

        var now = OffsetDateTime.now();

        p.setTutorId(req.tutorId());
        p.setName(req.name());
        p.setSpecies(req.species());
        p.setBreed(req.breed());
        p.setSex(req.sex());
        p.setBirthDate(req.birthDate());
        p.setWeightKg(req.weightKg());
        p.setNotes(req.notes());
        p.setUpdatedAt(now);

        return toResponse(petRepository.save(p));
    }

    @Transactional
    public void deleteLogical(Long id) {
        var role = AuthContext.requireUser().getRole();
        if (role != Role.ADMIN && role != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        var p = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pet not found"));

        p.setActive(false);
        p.setUpdatedAt(OffsetDateTime.now());
        petRepository.save(p);
    }

    private PetListItemResponse toListItem(PetEntity p) {
        return new PetListItemResponse(
                p.getId(),
                p.getTutorId(),
                p.getName(),
                p.getSpecies(),
                p.isActive()
        );
    }

    private PetResponse toResponse(PetEntity p) {
        return new PetResponse(
                p.getId(),
                p.getTutorId(),
                p.getName(),
                p.getSpecies(),
                p.getBreed(),
                p.getSex(),
                p.getBirthDate(),
                p.getWeightKg(),
                p.getNotes(),
                p.isActive(),
                p.getCreatedAt(),
                p.getUpdatedAt()
        );
    }
}
