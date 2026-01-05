package com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao;

import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.services.ImprimirNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.requests.RequestImprimirCCeNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.requests.RequestImprimirNFCe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.requests.RequestImprimirNFe;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class ImpressaoNFeService {

    private final ImprimirNFeService imprimirNFeService;

    public ImpressaoNFeService(ImprimirNFeService imprimirNFeService) {
        this.imprimirNFeService = imprimirNFeService;
    }

    public String imprimirNFe(RequestImprimirNFe request) throws FiscalException {
        return this.imprimirNFeService.imprimirNFe(request);
    }

    public String imprimirCCe(RequestImprimirCCeNFe request) throws FiscalException {
        return this.imprimirNFeService.imprimirCCe(request);
    }

    public String imprimirNFCe(RequestImprimirNFCe request) throws FiscalException {
        return this.imprimirNFeService.imprimirNFCe(request);
    }
}