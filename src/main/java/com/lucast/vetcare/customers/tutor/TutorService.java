package com.lucast.vetcare.customers.tutor;

import com.lucast.vetcare.auth.AuthContext;
import com.lucast.vetcare.common.enums.Role;
import com.lucast.vetcare.customers.tutor.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
public class TutorService {

    private final TutorRepository tutorRepository;
    private final TutorAddressRepository addressRepository;

    public TutorService(TutorRepository tutorRepository, TutorAddressRepository addressRepository) {
        this.tutorRepository = tutorRepository;
        this.addressRepository = addressRepository;
    }

    @Transactional
    public TutorResponse create(CreateTutorRequest req) {
        var now = OffsetDateTime.now();

        var t = new TutorEntity();
        t.setName(req.name());
        t.setDocument(req.document());
        t.setPhone(req.phone());
        t.setEmail(req.email());
        t.setActive(true);
        t.setCreatedAt(now);
        t.setUpdatedAt(now);

        var saved = tutorRepository.save(t);

        TutorAddressResponse addrResp = null;
        if (req.address() != null) {
            var addr = new TutorAddressEntity();
            addr.setTutor(saved);
            applyAddress(addr, req.address(), now);
            addr.setCreatedAt(now);
            addr.setUpdatedAt(now);
            addrResp = toAddressResponse(addressRepository.save(addr));
        }

        return toResponse(saved, addrResp);
    }

    @Transactional(readOnly = true)
    public TutorResponse getById(Long id) {
        var t = tutorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor not found"));

        var addr = addressRepository.findById(t.getId()).orElse(null);
        return toResponse(t, addr == null ? null : toAddressResponse(addr));
    }

    @Transactional(readOnly = true)
    public Page<TutorListItemResponse> list(
            String query,
            Boolean active,
            Boolean hasCompany,
            Boolean hasPet,
            Boolean hasContact,
            Pageable pageable
    ) {
        boolean hasQuery = query != null && !query.isBlank();
        String q = hasQuery ? query.trim() : "";

        return tutorRepository
                .search(hasQuery, q, active, hasCompany, hasPet, hasContact, pageable)
                .map(this::toListItem);
    }

    @Transactional(readOnly = true)
    public TutorStatsResponse stats() {
        var total = tutorRepository.count();
        var active = tutorRepository.countByActive(true);
        var inactive = tutorRepository.countByActive(false);
        var withCompany = tutorRepository.countWithActiveCompany();
        var withPet = tutorRepository.countWithActivePet();
        var withoutContact = tutorRepository.countWithoutContact();

        return new TutorStatsResponse(total, active, inactive, withCompany, withPet, withoutContact);
    }

    @Transactional
    public TutorResponse update(Long id, UpdateTutorRequest req) {
        var now = OffsetDateTime.now();

        var t = tutorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor not found"));

        t.setName(req.name());
        t.setDocument(req.document());
        t.setPhone(req.phone());
        t.setEmail(req.email());
        t.setUpdatedAt(now);

        var saved = tutorRepository.save(t);

        TutorAddressResponse addrResp = null;
        if (req.address() != null) {
            var addr = addressRepository.findById(saved.getId())
                    .orElseGet(() -> {
                        var a = new TutorAddressEntity();
                        a.setTutor(saved);
                        a.setCreatedAt(now);
                        return a;
                    });

            addr.setTutor(saved);
            addr.setTutorId(saved.getId());
            applyAddress(addr, req.address(), now);
            addr.setUpdatedAt(now);
            addrResp = toAddressResponse(addressRepository.save(addr));
        }

        return toResponse(saved, addrResp);
    }

    @Transactional
    public void deleteLogical(Long id) {
        var role = AuthContext.requireUser().getRole();
        if (role != Role.ADMIN && role != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        var now = OffsetDateTime.now();

        var t = tutorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor not found"));

        t.setActive(false);
        t.setUpdatedAt(now);
        tutorRepository.save(t);
    }

    private void applyAddress(TutorAddressEntity a, TutorAddressRequest req, OffsetDateTime now) {
        a.setZipCode(req.zipCode());
        a.setStreet(req.street());
        a.setNumber(req.number());
        a.setComplement(req.complement());
        a.setNeighborhood(req.neighborhood());
        a.setCityName(req.cityName());
        a.setCityIbge(req.cityIbge());
        a.setStateUf(req.stateUf());
        a.setCountry(req.country() == null || req.country().isBlank() ? "BR" : req.country());
    }

    private TutorListItemResponse toListItem(TutorEntity t) {
        return new TutorListItemResponse(
                t.getId(),
                t.getName(),
                t.getDocument(),
                t.getPhone(),
                t.getEmail(),
                t.isActive()
        );
    }

    private TutorResponse toResponse(TutorEntity t, TutorAddressResponse addr) {
        return new TutorResponse(
                t.getId(),
                t.getName(),
                t.getDocument(),
                t.getPhone(),
                t.getEmail(),
                t.isActive(),
                addr,
                t.getCreatedAt(),
                t.getUpdatedAt()
        );
    }

    private TutorAddressResponse toAddressResponse(TutorAddressEntity a) {
        return new TutorAddressResponse(
                a.getTutorId(),
                a.getZipCode(),
                a.getStreet(),
                a.getNumber(),
                a.getComplement(),
                a.getNeighborhood(),
                a.getCityName(),
                a.getCityIbge(),
                a.getStateUf(),
                a.getCountry(),
                a.getCreatedAt(),
                a.getUpdatedAt()
        );
    }
}
