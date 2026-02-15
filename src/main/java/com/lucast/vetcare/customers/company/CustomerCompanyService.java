package com.lucast.vetcare.customers.company;

import com.lucast.vetcare.auth.AuthContext;
import com.lucast.vetcare.common.enums.Role;
import com.lucast.vetcare.customers.company.dto.*;
import com.lucast.vetcare.customers.tutor.TutorRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class CustomerCompanyService {

    private final CustomerCompanyRepository companyRepository;
    private final CustomerCompanyAddressRepository addressRepository;
    private final CustomerCompanyFiscalRepository fiscalRepository;
    private final TutorRepository tutorRepository;

    public CustomerCompanyService(
            CustomerCompanyRepository companyRepository,
            CustomerCompanyAddressRepository addressRepository,
            CustomerCompanyFiscalRepository fiscalRepository,
            TutorRepository tutorRepository
    ) {
        this.companyRepository = companyRepository;
        this.addressRepository = addressRepository;
        this.fiscalRepository = fiscalRepository;
        this.tutorRepository = tutorRepository;
    }

    @Transactional
    public CustomerCompanyResponse create(CreateCustomerCompanyRequest req) {
        tutorRepository.findById(req.tutorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tutor not found"));

        companyRepository.findByTutorIdAndActiveTrue(req.tutorId())
                .ifPresent(c -> {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Tutor already has an active company");
                });

        var now = OffsetDateTime.now();

        var c = new CustomerCompanyEntity();
        c.setTutorId(req.tutorId());
        c.setLegalName(req.legalName());
        c.setTradeName(req.tradeName());
        c.setCnpj(req.cnpj());
        c.setPhone(req.phone());
        c.setEmail(req.email());
        c.setActive(true);
        c.setCreatedAt(now);
        c.setUpdatedAt(now);

        var saved = companyRepository.save(c);

        CustomerCompanyAddressResponse addrResp = null;
        if (req.address() != null) {
            var addr = new CustomerCompanyAddressEntity();
            addr.setCompany(saved);
            applyAddress(addr, req.address());
            addr.setCreatedAt(now);
            addr.setUpdatedAt(now);
            addrResp = toAddressResponse(addressRepository.save(addr));
        }

        CustomerCompanyFiscalResponse fiscalResp = null;
        if (req.fiscal() != null) {
            var fiscal = new CustomerCompanyFiscalEntity();
            fiscal.setCompany(saved);
            applyFiscal(fiscal, req.fiscal());
            fiscal.setCreatedAt(now);
            fiscal.setUpdatedAt(now);
            fiscalResp = toFiscalResponse(fiscalRepository.save(fiscal));
        }

        return toResponse(saved, addrResp, fiscalResp);
    }

    @Transactional(readOnly = true)
    public CustomerCompanyResponse getById(Long id) {
        var c = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer company not found"));

        var addr = addressRepository.findById(c.getId()).orElse(null);
        var fiscal = fiscalRepository.findById(c.getId()).orElse(null);
        return toResponse(
                c,
                addr == null ? null : toAddressResponse(addr),
                fiscal == null ? null : toFiscalResponse(fiscal)
        );
    }

    @Transactional(readOnly = true)
    public Page<CustomerCompanyListItemResponse> list(
            Long tutorId,
            String query,
            Boolean active,
            Boolean hasAddress,
            Boolean hasFiscal,
            Boolean hasContact,
            Pageable pageable
    ) {
        boolean hasQuery = query != null && !query.isBlank();
        String q = hasQuery ? query.trim() : "";

        return companyRepository
                .search(hasQuery, q, tutorId, active, hasAddress, hasFiscal, hasContact, pageable)
                .map(this::toListItem);
    }

    @Transactional(readOnly = true)
    public CustomerCompanyStatsResponse stats() {
        var total = companyRepository.count();
        var active = companyRepository.countByActive(true);
        var inactive = companyRepository.countByActive(false);
        var withAddress = companyRepository.countWithAddress();
        var withFiscal = companyRepository.countWithFiscal();
        var withoutContact = companyRepository.countWithoutContact();

        return new CustomerCompanyStatsResponse(total, active, inactive, withAddress, withFiscal, withoutContact);
    }

    @Transactional(readOnly = true)
    public List<CustomerCompanyListItemResponse> listByTutor(Long tutorId) {
        return companyRepository.findByActiveTrueAndTutorIdOrderByLegalNameAsc(tutorId).stream()
                .map(this::toListItem)
                .toList();
    }

    @Transactional(readOnly = true)
    public Page<CustomerCompanyListItemResponse> listByTutorPaged(Long tutorId, Pageable pageable) {
        return companyRepository.findByActiveTrueAndTutorIdOrderByLegalNameAsc(tutorId, pageable)
                .map(this::toListItem);
    }

    @Transactional
    public CustomerCompanyResponse update(Long id, UpdateCustomerCompanyRequest req) {
        var now = OffsetDateTime.now();

        var c = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer company not found"));

        c.setLegalName(req.legalName());
        c.setTradeName(req.tradeName());
        c.setCnpj(req.cnpj());
        c.setPhone(req.phone());
        c.setEmail(req.email());
        c.setUpdatedAt(now);

        var saved = companyRepository.save(c);

        CustomerCompanyAddressResponse addrResp = null;
        if (req.address() != null) {
            var addr = addressRepository.findById(saved.getId())
                    .orElseGet(() -> {
                        var a = new CustomerCompanyAddressEntity();
                        a.setCompany(saved);
                        a.setCreatedAt(now);
                        return a;
                    });

            addr.setCompany(saved);
            addr.setCustomerCompanyId(saved.getId());
            applyAddress(addr, req.address());
            addr.setUpdatedAt(now);
            addrResp = toAddressResponse(addressRepository.save(addr));
        }

        CustomerCompanyFiscalResponse fiscalResp = null;
        if (req.fiscal() != null) {
            var fiscal = fiscalRepository.findById(saved.getId())
                    .orElseGet(() -> {
                        var f = new CustomerCompanyFiscalEntity();
                        f.setCompany(saved);
                        f.setCreatedAt(now);
                        return f;
                    });

            fiscal.setCompany(saved);
            fiscal.setCustomerCompanyId(saved.getId());
            applyFiscal(fiscal, req.fiscal());
            fiscal.setUpdatedAt(now);
            fiscalResp = toFiscalResponse(fiscalRepository.save(fiscal));
        }

        return toResponse(saved, addrResp, fiscalResp);
    }

    @Transactional
    public void deleteLogical(Long id) {
        var role = AuthContext.requireUser().getRole();
        if (role != Role.ADMIN && role != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
        }

        var c = companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer company not found"));

        c.setActive(false);
        c.setUpdatedAt(OffsetDateTime.now());
        companyRepository.save(c);
    }

    private void applyAddress(CustomerCompanyAddressEntity a, CustomerCompanyAddressRequest req) {
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

    private void applyFiscal(CustomerCompanyFiscalEntity f, CustomerCompanyFiscalRequest req) {
        f.setIe(req.ie());
        f.setIeIndicator(req.ieIndicator());
    }

    private CustomerCompanyListItemResponse toListItem(CustomerCompanyEntity c) {
        return new CustomerCompanyListItemResponse(
                c.getId(),
                c.getTutorId(),
                c.getLegalName(),
                c.getTradeName(),
                c.getCnpj(),
                c.isActive()
        );
    }

    private CustomerCompanyResponse toResponse(
            CustomerCompanyEntity c,
            CustomerCompanyAddressResponse addr,
            CustomerCompanyFiscalResponse fiscal
    ) {
        return new CustomerCompanyResponse(
                c.getId(),
                c.getTutorId(),
                c.getLegalName(),
                c.getTradeName(),
                c.getCnpj(),
                c.getPhone(),
                c.getEmail(),
                c.isActive(),
                addr,
                fiscal,
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }

    private CustomerCompanyAddressResponse toAddressResponse(CustomerCompanyAddressEntity a) {
        return new CustomerCompanyAddressResponse(
                a.getCustomerCompanyId(),
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

    private CustomerCompanyFiscalResponse toFiscalResponse(CustomerCompanyFiscalEntity f) {
        return new CustomerCompanyFiscalResponse(
                f.getCustomerCompanyId(),
                f.getIe(),
                f.getIeIndicator(),
                f.getCreatedAt(),
                f.getUpdatedAt()
        );
    }
}
