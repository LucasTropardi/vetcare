package com.lucast.vetcare.fiscal.nfce.funcionalidades;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.application.port.out.SefazGateway;
import com.lucast.vetcare.fiscal.enums.TipoServicoEnum;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfce.result.NfceProtocolQueryResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class ConsultaNFCe {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaNFCe.class);

    private final SefazGateway sefazGateway;

    public ConsultaNFCe(SefazGateway sefazGateway) {
        this.sefazGateway = sefazGateway;
    }

    public ArrayList<String> consultaNFCe(String uf, TipoAmbienteEnum tipoAmbiente, String chaveNFe, Certificado certificado) throws FiscalException {
        return consultaNfceResult(uf, tipoAmbiente, chaveNFe, certificado).toLegacyList();
    }

    public NfceProtocolQueryResult consultaNfceResult(String uf, TipoAmbienteEnum tipoAmbiente, String chaveNFe, Certificado certificado) throws FiscalException {
        String url = sefazGateway.getUrlNFCe(TipoServicoEnum.NFCE_CONSULTA_PROTOCOLO, uf, Integer.parseInt(tipoAmbiente.getCodigo()));
        logger.info("Iniciando consultaNFCe url: {}", url);

        if (!url.isEmpty()) {
            String xml = xmlConsulta(tipoAmbiente.getCodigo(), chaveNFe);
            logger.info("xml da consulta: {}", xml);

            String retornoConsulta = sefazGateway.consulta(url, xml, certificado, "http://www.portalfiscal.inf.br/nfe/wsdl/NfeConsulta2");
            logger.error("retornoConsulta: {}", retornoConsulta);

            return montaRetorno(sefazGateway.pegaTag(retornoConsulta, "retConsSitNFe"), url, xml);
        }else {
            logger.error("URL de consulta não encontrada.");
            throw new FiscalException("Erro", "URL de consulta não encontrada!");
        }
    }

    private NfceProtocolQueryResult montaRetorno(String respostaConsulta, String url, String xmlConsulta) {
        logger.info("Iniciando montaRetorno");
        NfceProtocolQueryResult retorno = new NfceProtocolQueryResult(
                sefazGateway.pegaTag(respostaConsulta, "tpAmb"),
                sefazGateway.pegaTag(respostaConsulta, "verAplic"),
                sefazGateway.pegaTag(respostaConsulta, "dhRecbto"),
                sefazGateway.pegaTag(respostaConsulta, "nProt"),
                sefazGateway.pegaTag(respostaConsulta, "digVal"),
                sefazGateway.pegaTag(respostaConsulta, "cStat"),
                sefazGateway.pegaTag(respostaConsulta, "xMotivo"),
                sefazGateway.pegaTag(respostaConsulta, "cUF"),
                sefazGateway.pegaTag(respostaConsulta, "chNFe"),
                sefazGateway.pegaTag2(respostaConsulta, "protNFe"),
                sefazGateway.pegaTag2(respostaConsulta, "infProt"),
                sefazGateway.pegaTag2(respostaConsulta, "retCancNFe"),
                sefazGateway.pegaTag2(respostaConsulta, "procEventoNFe"),
                respostaConsulta,
                url,
                xmlConsulta
        );
        logger.info("montaRetorno: {}", retorno);

        return retorno;
    }

    private String xmlConsulta(String tipoAmbiente, String chaveNFe) {
        String dadosMsg = "<consSitNFe versao=\"4.00\" xmlns=\"http://www.portalfiscal.inf.br/nfe\">" ;
        dadosMsg +=         "<tpAmb>" + tipoAmbiente +"</tpAmb>";
        dadosMsg +=         "<xServ>CONSULTAR</xServ>";
        dadosMsg += 		"<chNFe>"+chaveNFe+"</chNFe>";
        dadosMsg +=       "</consSitNFe>";

        String xmlConsulta = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";
        xmlConsulta +=           "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">";
        xmlConsulta +=           	"<soap12:Body>";
        xmlConsulta +=           		"<nfeDadosMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeConsultaProtocolo4\">";
        xmlConsulta +=           			dadosMsg;
        xmlConsulta +=           		"</nfeDadosMsg>";
        xmlConsulta +=           	"</soap12:Body>";
        xmlConsulta +=           "</soap12:Envelope>";

        return xmlConsulta;
    }
}
