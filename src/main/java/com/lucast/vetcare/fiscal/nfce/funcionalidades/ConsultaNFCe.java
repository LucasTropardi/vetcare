package com.lucast.vetcare.fiscal.nfce.funcionalidades;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.util.FiscalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class ConsultaNFCe {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaNFCe.class);

    private ArrayList<String> retorno = new ArrayList<>();

    private FiscalUtil nFeUtil = new FiscalUtil();

    public ArrayList<String> consultaNFCe(String uf, TipoAmbienteEnum tipoAmbiente, String chaveNFe, Certificado certificado) throws FiscalException {
        String url = nFeUtil.getUrlNFCe("ConsultaProtocolo", uf, Integer.parseInt(tipoAmbiente.getCodigo()));
        logger.info("Iniciando consultaNFCe url: {}", url);

        if (!url.isEmpty()) {
            String xml = xmlConsulta(tipoAmbiente.getCodigo(), chaveNFe);
            logger.info("xml da consulta: {}", xml);

            String retornoConsulta = nFeUtil.consulta(url, xml, certificado, "http://www.portalfiscal.inf.br/nfe/wsdl/NfeConsulta2");
            logger.error("retornoConsulta: {}", retornoConsulta);

            montaRetorno(nFeUtil.pegaTag(retornoConsulta, "retConsSitNFe"), url, xml);

            return retorno;
        }else {
            logger.error("URL de consulta não encontrada.");
            throw new FiscalException("Erro", "URL de consulta não encontrada!");
        }
    }

    private void montaRetorno(String respostaConsulta, String url, String xmlConsulta) {
        logger.info("Iniciando montaRetorno");
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "tpAmb"));            // Tipo ambiente
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "verAplic"));         // Versão
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "dhRecbto"));         // Data e Hora do Recebimento
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "nProt"));            // Número do Protocolo
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "digVal"));           // DigVal
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "cStat"));            // Código do Status
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "xMotivo"));          // Motivo
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "cUF"));              // UF
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "chNFe"));            // Chave NFe
        retorno.add(nFeUtil.pegaTag2(respostaConsulta, "protNFe"));         // Prototocolo NFe
        retorno.add(nFeUtil.pegaTag2(respostaConsulta, "infProt"));         // Informações do protocolo
        retorno.add(nFeUtil.pegaTag2(respostaConsulta, "retCancNFe"));      // Retonro Cancelamento NFe
        retorno.add(nFeUtil.pegaTag2(respostaConsulta, "procEventoNFe"));   // Evento NFe
        retorno.add(respostaConsulta);                                           // Retorno da Connsulta completa
        retorno.add(url);                                                        // Url da consulta
        retorno.add(xmlConsulta);                                                // Xml da consulta
        logger.info("montaRetorno: {}", retorno);
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