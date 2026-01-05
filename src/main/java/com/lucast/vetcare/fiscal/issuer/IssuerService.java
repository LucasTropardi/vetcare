package com.lucast.vetcare.fiscal.issuer;

import com.lucast.vetcare.common.enums.Crt;
import com.lucast.vetcare.common.enums.IeIndicator;
import com.lucast.vetcare.company.CompanyAddressRepository;
import com.lucast.vetcare.company.CompanyFiscalConfigRepository;
import com.lucast.vetcare.company.CompanyRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class IssuerService {

    private final CompanyRepository companyRepository;
    private final CompanyAddressRepository addressRepository;
    private final CompanyFiscalConfigRepository fiscalRepository;

    public IssuerService(
            CompanyRepository companyRepository,
            CompanyAddressRepository addressRepository,
            CompanyFiscalConfigRepository fiscalRepository
    ) {
        this.companyRepository = companyRepository;
        this.addressRepository = addressRepository;
        this.fiscalRepository = fiscalRepository;
    }

    public IssuerData resolveIssuer(Long companyId) {
        if (companyId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "companyId is required to resolve issuer");
        }

        var company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Company não encontrada: " + companyId));

        var address = addressRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "CompanyAddress não encontrada para companyId=" + companyId));

        var fiscal = fiscalRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "CompanyFiscalConfig não encontrada para companyId=" + companyId));

        requireFilled(company.getCnpj(), "company.cnpj");
        requireFilled(company.getLegalName(), "company.legal_name");
        requireFilled(address.getStreet(), "company_address.street");
        requireFilled(address.getNeighborhood(), "company_address.neighborhood");
        requireFilled(address.getCityName(), "company_address.city_name");
        requireFilled(address.getStateUf(), "company_address.state_uf");
        requireFilled(address.getZipCode(), "company_address.zip_code");
        requireFilled(address.getCityIbge(), "company_address.city_ibge (obrigatório para emitir NFC-e/NF-e)");

        String ie = null;
        if (fiscal.getIeIndicator() == IeIndicator.CONTRIBUTOR) {
            ie = normalize(fiscal.getIe());
            requireFilled(ie, "company_fiscal_config.ie (obrigatório quando ie_indicator=CONTRIBUTOR)");
        }

        String crt = mapCrt(fiscal.getCrt());

        var end = new IssuerData.Endereco(
                normalize(address.getStreet()),
                normalize(address.getNumber()),
                normalize(address.getComplement()),
                normalize(address.getNeighborhood()),
                normalize(address.getCityIbge()),
                normalize(address.getCityName()),
                normalize(address.getStateUf()),
                normalize(address.getZipCode())
        );

        return new IssuerData(
                normalize(company.getCnpj()),
                normalize(company.getLegalName()),
                normalize(company.getTradeName()),
                ie,
                crt,
                end
        );
    }

    private static String mapCrt(Crt crt) {
        // NFC-e/NF-e: 1=Simples Nacional, 3=Regime Normal
        if (crt == null) return "1";
        return switch (crt) {
            case SIMPLES_NACIONAL -> "1";
            case REGIME_NORMAL -> "3";
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
