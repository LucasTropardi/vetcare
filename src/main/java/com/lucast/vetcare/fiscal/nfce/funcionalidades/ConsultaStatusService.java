package com.lucast.vetcare.fiscal.nfce.funcionalidades;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.application.port.out.SefazGateway;
import com.lucast.vetcare.fiscal.enums.TipoServicoEnum;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfce.result.NfceStatusResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ConsultaStatusService {

    private final SefazGateway sefazGateway;

    public ConsultaStatusService(SefazGateway sefazGateway) {
        this.sefazGateway = sefazGateway;
    }

    public ArrayList<String> consultaStatus(String uf, String url, TipoAmbienteEnum tipoAmbiente, Certificado certificado) throws FiscalException {
        return consultaStatusResult(uf, url, tipoAmbiente, certificado).toLegacyList();
    }

    public NfceStatusResult consultaStatusResult(String uf, String url, TipoAmbienteEnum tipoAmbiente, Certificado certificado) throws FiscalException {
        if (!url.isEmpty()) {
            String xml = xmlConsulta(tipoAmbiente.getCodigo(), uf, url);

            String retornoConsulta = sefazGateway.consulta(url, xml, certificado, "http://www.portalfiscal.inf.br/nfe/wsdl/NFeStatusServico4/nfeStatusServicoNF");

            return montaRetorno(sefazGateway.pegaTag(retornoConsulta, "retConsStatServ"));
        }else {
            throw new FiscalException("Erro", "URL de consulta não encontrada!");
        }
    }

    private NfceStatusResult montaRetorno(String respostaConsulta) {
        return new NfceStatusResult(
                sefazGateway.pegaTag(respostaConsulta, "tpAmb"),
                sefazGateway.pegaTag(respostaConsulta, "verAplic"),
                sefazGateway.pegaTag(respostaConsulta, "cStat"),
                sefazGateway.pegaTag(respostaConsulta, "xMotivo"),
                sefazGateway.pegaTag(respostaConsulta, "cUF"),
                sefazGateway.pegaTag(respostaConsulta, "tMed")
        );
    }

    private String xmlConsulta(String tipoAmbiente, String uf, String url) {
        String dadosMsg = "<consStatServ xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">" ;
        dadosMsg +=         "<tpAmb>" + tipoAmbiente +"</tpAmb>";
        dadosMsg +=         "<cUF>" + sefazGateway.ufToCodUf(uf) +"</cUF>";
        dadosMsg +=         "<xServ>STATUS</xServ>";
        dadosMsg +=       "</consStatServ>";

        String xmlConsulta = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
        xmlConsulta +=           "<soap12:Envelope xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\"";
        xmlConsulta +=           	           "   xmlns:nfe=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeStatusServico4\">";
        xmlConsulta +=           	"<soap12:Body>";
        xmlConsulta +=           		"<nfe:nfeDadosMsg xmlns=\"" + url + "\">";
        xmlConsulta +=           			dadosMsg;
        xmlConsulta +=           		"</nfe:nfeDadosMsg>";
        xmlConsulta +=           	"</soap12:Body>";
        xmlConsulta +=           "</soap12:Envelope>";

        return xmlConsulta;
    }
}
