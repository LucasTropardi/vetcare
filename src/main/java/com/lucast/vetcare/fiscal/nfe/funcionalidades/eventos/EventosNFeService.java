package com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos;

import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.services.CancelarNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.services.CartaCorrecaoNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.services.InutilizaNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests.RequestCancelarNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests.RequestCartaCorrecaoNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests.RequestInutilizaNFe;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventosNFeService {

    private final CancelarNFeService cancelarNFeService;
    private final CartaCorrecaoNFeService cartaCorrecaoNFeService;
    private final InutilizaNFeService inutilizaNFeService;

    public EventosNFeService(CancelarNFeService cancelarNFeService,
                             CartaCorrecaoNFeService cartaCorrecaoNFeService,
                             InutilizaNFeService inutilizaNFeService) {
        this.cancelarNFeService = cancelarNFeService;
        this.cartaCorrecaoNFeService = cartaCorrecaoNFeService;
        this.inutilizaNFeService = inutilizaNFeService;
    }

    public String cancelarNFe(RequestCancelarNFe request) throws FiscalException {
        return this.cancelarNFeService.cancelaNFe(request);
    }

    public List<String> cartaCorrecao(RequestCartaCorrecaoNFe request) throws FiscalException {
        return this.cartaCorrecaoNFeService.cce(request);
    }

    public String inutilizarNFe(RequestInutilizaNFe request) throws FiscalException {
        return this.inutilizaNFeService.inutilizar(request);
    }

}
