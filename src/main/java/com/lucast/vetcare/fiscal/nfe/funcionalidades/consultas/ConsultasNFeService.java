package com.lucast.vetcare.fiscal.nfe.funcionalidades.consultas;

import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.consultas.requests.RequestConsultaCadastro;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.consultas.requests.RequestConsultaNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.consultas.services.ConsultaCadastroService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.consultas.services.ConsultaNFeService;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("prototype")
public class ConsultasNFeService {

    private final ConsultaNFeService consultaNFeService;
    private final ConsultaCadastroService consultaCadastroService;

    public ConsultasNFeService(ConsultaNFeService consultaNFeService,
                               ConsultaCadastroService consultaCadastroService) {
        this.consultaNFeService = consultaNFeService;
        this.consultaCadastroService = consultaCadastroService;
    }

    public List<String> consultaNFe(RequestConsultaNFe request) throws FiscalException {
        return this.consultaNFeService.consultaNFe(request);
    }

    public List<String> consultaCadastro(RequestConsultaCadastro request) throws FiscalException {
        return this.consultaCadastroService.consultaCadastro(request);
    }
}
