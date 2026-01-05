package com.lucast.vetcare.fiscal.nfe;

import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.consultas.ConsultasNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.consultas.requests.RequestConsultaCadastro;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.consultas.requests.RequestConsultaNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.EventosNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.requests.RequestImprimirCCeNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.requests.RequestImprimirNFCe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.OperacoesNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests.RequestAssinarNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests.RequestCancelarNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests.RequestCartaCorrecaoNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests.RequestInutilizaNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests.RequestRetornoRecepcaoNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests.RequestValidaNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.ImpressaoNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests.RequestEnviaNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.requests.RequestImprimirNFe;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("prototype")
public class NotaFiscalFacade {

    private final OperacoesNFeService operacoes;
    private final EventosNFeService eventos;
    private final ImpressaoNFeService impressao;
    private final ConsultasNFeService consultas;

    public NotaFiscalFacade(OperacoesNFeService operacoes,
                            EventosNFeService eventos,
                            ImpressaoNFeService impressao,
                            ConsultasNFeService consultas) {
        this.operacoes = operacoes;
        this.eventos = eventos;
        this.impressao = impressao;
        this.consultas = consultas;
    }

    // Operações Básicas
    public Boolean validaNFe(RequestValidaNFe request) throws FiscalException {
        return operacoes.validaNFe(request);
    }

    public String assinaNFe(RequestAssinarNFe request) throws FiscalException {
        return operacoes.assinaNFe(request);
    }

    public List<String> enviaNFe(RequestEnviaNFe request) throws FiscalException {
        return operacoes.enviaNFe(request);
    }

    public List<String> consultaRetornoRecepcao(RequestRetornoRecepcaoNFe request) throws FiscalException {
        return operacoes.consultaRetornoRecepcao(request);
    }

    // Eventos
    public String cancelarNFe(RequestCancelarNFe request) throws FiscalException {
        return eventos.cancelarNFe(request);
    }

    public List<String> cartaCorrecao(RequestCartaCorrecaoNFe request) throws FiscalException {
        return eventos.cartaCorrecao(request);
    }

    public String inutilizarNFe(RequestInutilizaNFe request) throws FiscalException {
        return eventos.inutilizarNFe(request);
    }

    // Impressão
    public String imprimirNFe(RequestImprimirNFe request) throws FiscalException {
        return impressao.imprimirNFe(request);
    }

    public String imprimirCCe(RequestImprimirCCeNFe request) throws FiscalException {
        return impressao.imprimirCCe(request);
    }

    public String imprimirNFCe(RequestImprimirNFCe request) throws FiscalException {
        return impressao.imprimirNFCe(request);
    }

    // Consultas
    public List<String> consultaNFe(RequestConsultaNFe request) throws FiscalException {
        return consultas.consultaNFe(request);
    }

    public List<String> consultaCadastro(RequestConsultaCadastro request) throws FiscalException {
        return consultas.consultaCadastro(request);
    }
}
