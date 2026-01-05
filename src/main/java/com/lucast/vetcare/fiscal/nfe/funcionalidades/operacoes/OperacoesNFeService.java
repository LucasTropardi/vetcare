package com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes;

import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.services.EnviaNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.services.RetornoRecepcaoNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests.RequestAssinarNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests.RequestValidaNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.services.AssinarNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.services.ValidaNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests.RequestEnviaNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests.RequestRetornoRecepcaoNFe;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("prototype")
public class OperacoesNFeService {

    private final ValidaNFeService validaNFeService;
    private final AssinarNFeService assinarNFeService;
    private final EnviaNFeService enviaNFeService;
    private final RetornoRecepcaoNFeService retornoRecepcaoNFeService;

    public OperacoesNFeService(ValidaNFeService validaNFeService,
                               AssinarNFeService assinarNFeService,
                               EnviaNFeService enviaNFeService,
                               RetornoRecepcaoNFeService retornoRecepcaoNFeService) {
        this.validaNFeService = validaNFeService;
        this.assinarNFeService = assinarNFeService;
        this.enviaNFeService = enviaNFeService;
        this.retornoRecepcaoNFeService = retornoRecepcaoNFeService;
    }

    public Boolean validaNFe(RequestValidaNFe request) throws FiscalException {
        return this.validaNFeService.validaXml(request);
    }

    public String assinaNFe(RequestAssinarNFe request) throws FiscalException {
        return this.assinarNFeService.assinaNfe(request);
    }

    public List<String> enviaNFe(RequestEnviaNFe request) throws FiscalException {
        return this.enviaNFeService.enviaNFe(request);
    }

    public List<String> consultaRetornoRecepcao(RequestRetornoRecepcaoNFe request) throws FiscalException {
        return this.retornoRecepcaoNFeService.retornoNFCe2(request);
    }

}
