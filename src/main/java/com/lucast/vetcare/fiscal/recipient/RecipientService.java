package com.lucast.vetcare.fiscal.recipient;

import com.lucast.vetcare.common.enums.IeIndicator;
import com.lucast.vetcare.customers.company.CustomerCompanyAddressRepository;
import com.lucast.vetcare.customers.company.CustomerCompanyFiscalRepository;
import com.lucast.vetcare.customers.company.CustomerCompanyRepository;
import com.lucast.vetcare.customers.tutor.TutorAddressRepository;
import com.lucast.vetcare.customers.tutor.TutorRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RecipientService {

    private final CustomerCompanyRepository companyRepository;
    private final CustomerCompanyAddressRepository addressRepository;
    private final CustomerCompanyFiscalRepository fiscalRepository;
    private final TutorRepository tutorRepository;
    private final TutorAddressRepository tutorAddressRepository;

    public RecipientService(
            CustomerCompanyRepository companyRepository,
            CustomerCompanyAddressRepository addressRepository,
            CustomerCompanyFiscalRepository fiscalRepository,
            TutorRepository tutorRepository,
            TutorAddressRepository tutorAddressRepository
    ) {
        this.companyRepository = companyRepository;
        this.addressRepository = addressRepository;
        this.fiscalRepository = fiscalRepository;
        this.tutorRepository = tutorRepository;
        this.tutorAddressRepository = tutorAddressRepository;
    }

    public RecipientData resolveRecipient(Long customerCompanyId, Long tutorId) {
        if (customerCompanyId != null) {
            return resolveCustomerCompany(customerCompanyId);
        }
        if (tutorId != null) {
            return resolveTutor(tutorId);
        }
        return null;
    }

    private RecipientData resolveCustomerCompany(Long customerCompanyId) {
        var company = companyRepository.findById(customerCompanyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer company não encontrada: " + customerCompanyId));

        if (!company.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Customer company inativa: " + customerCompanyId);
        }

        var address = addressRepository.findById(customerCompanyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "CustomerCompanyAddress não encontrada para customerCompanyId=" + customerCompanyId));

        var fiscal = fiscalRepository.findById(customerCompanyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "CustomerCompanyFiscal não encontrada para customerCompanyId=" + customerCompanyId));

        requireFilled(company.getCnpj(), "customer_company.cnpj");
        requireFilled(company.getLegalName(), "customer_company.legal_name");
        requireFilled(address.getStreet(), "customer_company_address.street");
        requireFilled(address.getNeighborhood(), "customer_company_address.neighborhood");
        requireFilled(address.getCityName(), "customer_company_address.city_name");
        requireFilled(address.getStateUf(), "customer_company_address.state_uf");
        requireFilled(address.getZipCode(), "customer_company_address.zip_code");
        requireFilled(address.getCityIbge(), "customer_company_address.city_ibge (obrigatório para emitir NFC-e/NF-e)");

        String ie = null;
        if (fiscal.getIeIndicator() == IeIndicator.CONTRIBUTOR) {
            ie = normalize(fiscal.getIe());
            requireFilled(ie, "customer_company_fiscal.ie (obrigatório quando ie_indicator=CONTRIBUTOR)");
        }

        String indIeDest = mapIndIeDest(fiscal.getIeIndicator());

        var end = new RecipientData.Endereco(
                normalize(address.getStreet()),
                normalize(address.getNumber()),
                normalize(address.getComplement()),
                normalize(address.getNeighborhood()),
                normalize(address.getCityIbge()),
                normalize(address.getCityName()),
                normalize(address.getStateUf()),
                normalize(address.getZipCode())
        );

        return new RecipientData(
                normalize(company.getCnpj()),
                null,
                normalize(company.getLegalName()),
                normalize(company.getTradeName()),
                ie,
                indIeDest,
                normalize(company.getPhone()),
                end
        );
    }

    private RecipientData resolveTutor(Long tutorId) {
        var tutor = tutorRepository.findById(tutorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tutor não encontrado: " + tutorId));

        if (!tutor.isActive()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Tutor inativo: " + tutorId);
        }

        String cpf = normalize(tutor.getDocument());
        if (cpf != null) {
            cpf = cpf.replaceAll("\\D", "");
        }
        if (cpf == null || cpf.length() != 11) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campo obrigatório para emissão: tutor.cpf");
        }

        var address = tutorAddressRepository.findById(tutorId).orElse(null);
        RecipientData.Endereco end = null;
        if (address != null) {
            String street = normalize(address.getStreet());
            String cityIbge = normalize(address.getCityIbge());
            String cityName = normalize(address.getCityName());
            String stateUf = normalize(address.getStateUf());
            String zipCode = normalize(address.getZipCode());
            if (street != null && cityIbge != null && cityName != null && stateUf != null && zipCode != null) {
                end = new RecipientData.Endereco(
                        street,
                        normalize(address.getNumber()),
                        normalize(address.getComplement()),
                        normalize(address.getNeighborhood()),
                        cityIbge,
                        cityName,
                        stateUf,
                        zipCode
                );
            }
        }

        return new RecipientData(
                null,
                cpf,
                normalize(tutor.getName()),
                null,
                null,
                "9",
                normalize(tutor.getPhone()),
                end
        );
    }

    private static String mapIndIeDest(IeIndicator ind) {
        if (ind == null) return "9";
        return switch (ind) {
            case CONTRIBUTOR -> "1";
            case EXEMPT -> "2";
            case NON_CONTRIBUTOR -> "9";
        };
    }

    private static void requireFilled(String v, String field) {
        if (v == null || v.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Campo obrigatório para emissão: " + field);
        }
    }

    private static String normalize(String s) {
        if (s == null) return null;
        var t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
