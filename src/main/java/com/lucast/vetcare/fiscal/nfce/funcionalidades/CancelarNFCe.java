package com.lucast.vetcare.fiscal.nfce.funcionalidades;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.AssinaturaEnum;
import com.lucast.vetcare.fiscal.enums.ServicosNFeEnum;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.util.FiscalUtil;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class CancelarNFCe {

    private FiscalUtil fiscalUtil = new FiscalUtil();

    private ArrayList<String> retorno = new ArrayList<>();

    public ArrayList<String> cancelarNFCe(String justificativa, String uf, String cnpj, String chaveNFCe, String protocolo, LocalDateTime dataEvento, String tipoEmissao, TipoAmbienteEnum tipoAmbiente, Certificado certificado) throws FiscalException {
        String url = fiscalUtil.getUrlNFCe("RecepcaoEvento", uf, Integer.parseInt(tipoAmbiente.getCodigo()));

        String xmlEvento = createXml(fiscalUtil.ufToCodUf(uf), cnpj, chaveNFCe, 1L, "110111", 1L, protocolo, dataEvento, tipoAmbiente, justificativa, certificado);

        boolean validada = new ValidaNFCe().validaXml(xmlEvento, ServicosNFeEnum.CANCELAMENTO);

        if (validada) {
            String xmlConsulta = montaXmlConsulta(xmlEvento);

            String retornoConsulta = fiscalUtil.consulta(url, xmlConsulta, certificado, "http://www.portalfiscal.inf.br/nfe/wsdl/NFeRecepcaoEvento4/nfeRecepcaoEvento");

            String retEnvEvento = fiscalUtil.pegaTag(retornoConsulta, "retEnvEvento");

            String infEvento = fiscalUtil.pegaTag(retornoConsulta, "infEvento");

            montaRetorno(montaXmlFinal(fiscalUtil.pegaTag2(xmlEvento, "evento"), fiscalUtil.pegaTag2(retornoConsulta, "retEvento")), url, xmlConsulta);

            if (!fiscalUtil.pegaTag(retEnvEvento, "cStat").equals("128")) {
                throw new FiscalException("Aviso", "Retorno do Evento ->  cSat - " + fiscalUtil.pegaTag(retEnvEvento, "cStat") + " <br> Motivo - " + fiscalUtil.pegaTag(retEnvEvento, "xMotivo") +
                        "<br> Informações do Evento ->  cSat - " + fiscalUtil.pegaTag(infEvento, "cStat") + " <br> Motivo - " + fiscalUtil.pegaTag(infEvento, "xMotivo"));
            } else if (!retorno.get(5).equals("135")) {
                throw new FiscalException("Erro", "Informações do Evento ->  cSat - " + fiscalUtil.pegaTag(infEvento, "cStat") + " <br> Motivo - " + fiscalUtil.pegaTag(infEvento, "xMotivo"));
            }

            return retorno;
        } else {
            throw new FiscalException("Aviso", "Cancelamento não validado!");
        }
    }

    private String montaXmlFinal(String evento, String retornoEvento) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
                "<ProcEventoNFe versao=\"1.00\" xmlns=\"http://www.portalfiscal.inf.br/nfe\">" +
                evento +
                retornoEvento +
                "</ProcEventoNFe>";
    }

    private String montaXmlConsulta(String xmlEvento) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap12:Header>" +
                "<nfeCabecMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeRecepcaoEvento4\">" +
                "</nfeCabecMsg>" +
                "</soap12:Header>" +
                "<soap12:Body>" +
                "<nfeDadosMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeRecepcaoEvento4\">" +
                xmlEvento +
                "</nfeDadosMsg>" +
                "</soap12:Body>" +
                "</soap12:Envelope>";
    }

    private String createXml(Integer codigoUF, String cnpj, String chaveNFCe, Long lote, String codigoEvento, Long sequenciaEvento, String protocolo, LocalDateTime dataEvento, TipoAmbienteEnum tipoAmbiente, String justificativa, Certificado certificado) {
        String dhEvento = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX").format(dataEvento.atOffset(ZoneOffset.ofHours(-3)));

        String dadosXml = "<envEvento xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"1.00\">" +
                "<idLote>" + lote + "</idLote>";

        String id = "ID" + codigoEvento + chaveNFCe + String.format("%02d", sequenciaEvento);


        String dadosMsg = "<evento xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"1.00\">" +
                "<infEvento Id=\"" + id + "\">" +
                "<cOrgao>" + codigoUF + "</cOrgao>" +
                "<tpAmb>" + tipoAmbiente.getCodigo() + "</tpAmb>" +
                "<CNPJ>" + cnpj + "</CNPJ>" +
                "<chNFe>" + chaveNFCe + "</chNFe>" +
                "<dhEvento>" + dhEvento + "</dhEvento>" +
                "<tpEvento>" + codigoEvento + "</tpEvento>" +
                "<nSeqEvento>" + sequenciaEvento + "</nSeqEvento>" +
                "<verEvento>1.00</verEvento>" +
                "<detEvento versao=\"1.00\">" +
                "<descEvento>Cancelamento</descEvento>" +
                "<nProt>" + protocolo + "</nProt>" +
                "<xJust>" + justificativa + "</xJust>" +
                "</detEvento>" +
                "</infEvento>" +
                "</evento>";

        dadosMsg = new AssinarNFCe().assinaEvento(dadosMsg, certificado, AssinaturaEnum.EVENTO);

        dadosXml += dadosMsg + "</envEvento>";

        return dadosXml;
    }

    private void montaRetorno(String respostaConsulta, String url, String xmlConsulta) {
        retorno.add(fiscalUtil.pegaTag(respostaConsulta, "tpAmb"));            // Tipo ambiente
        retorno.add(fiscalUtil.pegaTag(respostaConsulta, "verAplic"));         // Versão
        retorno.add(fiscalUtil.pegaTag(respostaConsulta, "dhRegEvento"));      // Data e hora do registro do evento
        retorno.add(fiscalUtil.pegaTag(respostaConsulta, "tpEvento"));         // Tipo Evento
        retorno.add(fiscalUtil.pegaTag(respostaConsulta, "xEvento"));          // Evento
        retorno.add(fiscalUtil.pegaTag(respostaConsulta, "cStat"));            // Código do Status
        retorno.add(fiscalUtil.pegaTag(respostaConsulta, "xMotivo"));          // Motivo
        retorno.add(fiscalUtil.pegaTag(respostaConsulta, "nSeqEvento"));       // UF
        retorno.add(fiscalUtil.pegaTag(respostaConsulta, "chNFe"));            // Sequência Evento
        retorno.add(fiscalUtil.pegaTag(respostaConsulta, "cOrgao"));           // Orgão
        retorno.add(respostaConsulta);
        retorno.add(url);                                                           // Url da consulta
        retorno.add(xmlConsulta);
    }
}
