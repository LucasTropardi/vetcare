package com.lucast.vetcare.fiscal.nfce.funcionalidades;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.util.FiscalUtil;

import java.util.ArrayList;

public class ConsultaStatusService {

    private ArrayList<String> retorno = new ArrayList<String>();

    private FiscalUtil nFeUtil = new FiscalUtil();

    public ArrayList<String> consultaStatus(String uf, String url, TipoAmbienteEnum tipoAmbiente, Certificado certificado) throws FiscalException {
        if (!url.isEmpty()) {
            String xml = xmlConsulta(tipoAmbiente.getCodigo(), uf, url);

            String retornoConsulta = nFeUtil.consulta(url, xml, certificado, "http://www.portalfiscal.inf.br/nfe/wsdl/NFeStatusServico4/nfeStatusServicoNF");

            montaRetorno(nFeUtil.pegaTag(retornoConsulta, "retConsStatServ"));

            return retorno;
        }else {
            throw new FiscalException("Erro", "URL de consulta não encontrada!");
        }
    }

    private void montaRetorno(String respostaConsulta) {
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "tpAmb"));          //Tipo ambiente
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "verAplic"));       //Versão
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "cStat"));          //Código do Status
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "xMotivo"));        // Motivo
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "cUF"));            //UF
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "tMed"));           //tMed
    }

    private String xmlConsulta(String tipoAmbiente, String uf, String url) {
        String dadosMsg = "<consStatServ xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">" ;
        dadosMsg +=         "<tpAmb>" + tipoAmbiente +"</tpAmb>";
        dadosMsg +=         "<cUF>" + new FiscalUtil().ufToCodUf(uf) +"</cUF>";
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
