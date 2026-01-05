package com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos;

import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.services.AssinarNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.services.CancelarNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.services.CartaCorrecaoNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.services.InutilizaNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.services.ValidaNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests.RequestCancelarNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests.RequestCartaCorrecaoNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests.RequestInutilizaNFe;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Scope("prototype")
public class EventosNFeService {

    private final CancelarNFeService cancelarNFeService;
    private final CartaCorrecaoNFeService cartaCorrecaoNFeService;
    private final InutilizaNFeService inutilizaNFeService;
    private final ValidaNFeService validaNFeService;
    private final AssinarNFeService assinarNFeService;

    public EventosNFeService(CancelarNFeService cancelarNFeService,
                             CartaCorrecaoNFeService cartaCorrecaoNFeService,
                             InutilizaNFeService inutilizaNFeService,
                             ValidaNFeService validaNFeService,
                             AssinarNFeService assinarNFeService) {
        this.cancelarNFeService = cancelarNFeService;
        this.cartaCorrecaoNFeService = cartaCorrecaoNFeService;
        this.inutilizaNFeService = inutilizaNFeService;
        this.validaNFeService = validaNFeService;
        this.assinarNFeService = assinarNFeService;
    }

    public String cancelarNFe(RequestCancelarNFe request) throws FiscalException {
        return this.cancelarNFeService.cancelaNFe(request, validaNFeService, assinarNFeService);
    }

    public List<String> cartaCorrecao(RequestCartaCorrecaoNFe request) throws FiscalException {
        return this.cartaCorrecaoNFeService.cce(request, validaNFeService, assinarNFeService);
    }

    public String inutilizarNFe(RequestInutilizaNFe request) throws FiscalException {
        return this.inutilizaNFeService.inutilizar(request, validaNFeService, assinarNFeService);
    }

}
