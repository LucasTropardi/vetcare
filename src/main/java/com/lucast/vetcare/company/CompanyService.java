package com.lucast.vetcare.company;

import com.lucast.vetcare.auth.AuthContext;
import com.lucast.vetcare.common.enums.Role;
import com.lucast.vetcare.company.dto.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.OffsetDateTime;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final CompanyAddressRepository addressRepository;
    private final CompanyFiscalConfigRepository fiscalConfigRepository;

    public CompanyService(
            CompanyRepository companyRepository,
            CompanyAddressRepository addressRepository,
            CompanyFiscalConfigRepository fiscalConfigRepository
    ) {
        this.companyRepository = companyRepository;
        this.addressRepository = addressRepository;
        this.fiscalConfigRepository = fiscalConfigRepository;
    }

    @Transactional(readOnly = true)
    public CompanyProfileResponse getCurrentProfile() {
        var company = getCurrentCompanyOrThrow();

        return toProfileResponse(company);
    }

    @Transactional
    public CompanyProfileResponse updateCurrentProfile(UpdateCompanyProfileRequest request) {
        requireEditPermission();

        var now = OffsetDateTime.now();
        var company = getCurrentCompanyOrThrow();

        company.setLegalName(request.legalName());
        company.setTradeName(request.tradeName());
        company.setCnpj(request.cnpj());
        company.setPhone(request.phone());
        company.setEmail(request.email());
        company.setHeadquarter(Boolean.TRUE.equals(request.headquarter()));
        company.setUpdatedAt(now);

        companyRepository.save(company);

        if (request.address() != null) {
            var address = addressRepository.findById(company.getId())
                    .orElseGet(() -> {
                        var a = new CompanyAddressEntity();
                        a.setCompany(company);
                        return a;
                    });

            address.setCompany(company);
            address.setCompanyId(company.getId());
            address.setZipCode(request.address().zipCode());
            address.setStreet(request.address().street());
            address.setNumber(request.address().number());
            address.setComplement(request.address().complement());
            address.setNeighborhood(request.address().neighborhood());
            address.setCityName(request.address().cityName());
            address.setCityIbge(request.address().cityIbge());
            address.setStateUf(request.address().stateUf());
            address.setCountry(request.address().country() == null || request.address().country().isBlank() ? "BR" : request.address().country());

            addressRepository.save(address);
        }

        if (request.fiscalConfig() != null) {
            var fiscal = fiscalConfigRepository.findById(company.getId())
                    .orElseGet(() -> {
                        var f = new CompanyFiscalConfigEntity();
                        f.setCompany(company);
                        return f;
                    });

            fiscal.setCompany(company);
            fiscal.setCompanyId(company.getId());
            fiscal.setIe(request.fiscalConfig().ie());
            fiscal.setIeIndicator(request.fiscalConfig().ieIndicator());
            fiscal.setCrt(request.fiscalConfig().crt());
            fiscalConfigRepository.save(fiscal);
        }

        return toProfileResponse(company);
    }

    private void requireEditPermission() {
        var role = AuthContext.requireUser().getRole();
        if (role != Role.ADMIN && role != Role.VET) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Only ADMIN and VET can update company profile");
        }
    }

    private CompanyEntity getCurrentCompanyOrThrow() {
        return companyRepository.findFirstByHeadquarterTrueOrderByIdAsc()
                .or(() -> companyRepository.findFirstByOrderByIdAsc())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Issuer company not found"));
    }

    private CompanyProfileResponse toProfileResponse(CompanyEntity company) {
        var address = addressRepository.findById(company.getId()).orElse(null);
        var fiscalConfig = fiscalConfigRepository.findById(company.getId()).orElse(null);

        return new CompanyProfileResponse(
                company.getId(),
                company.getLegalName(),
                company.getTradeName(),
                company.getCnpj(),
                company.getPhone(),
                company.getEmail(),
                company.isHeadquarter(),
                company.getParentCompany() == null ? null : company.getParentCompany().getId(),
                address == null ? null : new CompanyAddressResponse(
                        address.getCompanyId(),
                        address.getZipCode(),
                        address.getStreet(),
                        address.getNumber(),
                        address.getComplement(),
                        address.getNeighborhood(),
                        address.getCityName(),
                        address.getCityIbge(),
                        address.getStateUf(),
                        address.getCountry()
                ),
                fiscalConfig == null ? null : new CompanyFiscalConfigResponse(
                        fiscalConfig.getCompanyId(),
                        fiscalConfig.getIe(),
                        fiscalConfig.getIeIndicator(),
                        fiscalConfig.getCrt()
                ),
                company.getCreatedAt(),
                company.getUpdatedAt()
        );
    }
}
