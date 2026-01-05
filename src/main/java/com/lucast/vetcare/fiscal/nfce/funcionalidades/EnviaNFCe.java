package com.lucast.vetcare.fiscal.nfce.funcionalidades;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.util.FiscalUtil;

import java.util.ArrayList;

public class EnviaNFCe {

    private ArrayList<String> retorno = new ArrayList<String>();

    private FiscalUtil nFeUtil = new FiscalUtil();

    public ArrayList<String> enviaNFCe(String xml, Long numeroLote, String codigoUF, TipoAmbienteEnum tipoAmbiente, Certificado certificado) throws FiscalException, InterruptedException {
        String conteudoXml = nFeUtil.removeXMLTag(xml);

        conteudoXml = nFeUtil.pegaTag2(conteudoXml, "NFe");

        String xmlEvento = montaXmlEvento(numeroLote, conteudoXml);

        String url = nFeUtil.getUrlNFCe("NFeAutorizacao", codigoUF, Integer.parseInt(tipoAmbiente.getCodigo()));

        String retornoConsulta = nFeUtil.consulta(url, xmlEvento, certificado, "http://www.portalfiscal.inf.br/nfe/wsdl/NFeAutorizacao4/nfeAutorizacaoLote");

        if (retornoConsulta != null && !retornoConsulta.isBlank()) {
            String infProt = retornoConsulta.contains("infProt") ? nFeUtil.pegaTag(retornoConsulta, "infProt") : nFeUtil.pegaTag(retornoConsulta, "retEnviNFe");

            if (nFeUtil.pegaTag(infProt, "cStat").equals("100") && nFeUtil.pegaTag(infProt, "xMotivo").equals("Autorizado o uso da NF-e")) {
                montaRetorno(nFeUtil.pegaTag2(retornoConsulta, "protNFe"));
                return retorno;
            } else {
                throw new FiscalException("Aviso", "CSTAT - " + nFeUtil.pegaTag(infProt, "cStat") + " <br> Motivo - " + nFeUtil.pegaTag(infProt, "xMotivo"));
            }
        } else {
            throw new FiscalException("Erro", "Erro ao realizar a consulta");
        }
    }

    private String montaXmlEvento(Long numeroLote, String conteudoXml) {
        String xml = "<enviNFe xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">"
                + "<idLote>" + numeroLote + "</idLote>"
                + "<indSinc>1</indSinc>"
                + conteudoXml
                + "</enviNFe>";

        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"
                + "<soap12:Header>"
                + "<nfeCabecMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeAutorizacao4\">"
                + "</nfeCabecMsg>"
                + "</soap12:Header>"
                + "<soap12:Body>"
                + "<nfeDadosMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeAutorizacao4\">"
                + xml
                + "</nfeDadosMsg>"
                + "</soap12:Body>"
                + "</soap12:Envelope>";
    }

    private void montaRetorno(String respostaConsulta) {
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "tpAmb"));            // Tipo ambiente
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "verAplic"));         // Versão
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "dhRecbto"));         // Data e Hora do Recebimento
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "nProt"));            // Número do Protocolo
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "digVal"));           // DigVal
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "cStat"));            // Código do Status
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "xMotivo"));          // Motivo
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "cUF"));              // UF
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "chNFe"));            // Chave NFe
        retorno.add(nFeUtil.pegaTag(respostaConsulta, "versao").isEmpty() ? "Sem Versao" : nFeUtil.pegaTag(respostaConsulta, "versao"));         //Versão
        retorno.add(respostaConsulta);
    }
}
