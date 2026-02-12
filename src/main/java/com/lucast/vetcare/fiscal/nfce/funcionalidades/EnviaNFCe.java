package com.lucast.vetcare.fiscal.nfce.funcionalidades;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.application.port.out.SefazGateway;
import com.lucast.vetcare.fiscal.enums.TipoServicoEnum;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfce.result.NfceAuthorizationResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class EnviaNFCe {

    private final SefazGateway sefazGateway;

    public EnviaNFCe(SefazGateway sefazGateway) {
        this.sefazGateway = sefazGateway;
    }

    public ArrayList<String> enviaNFCe(String xml, Long numeroLote, String codigoUF, TipoAmbienteEnum tipoAmbiente, Certificado certificado) throws FiscalException, InterruptedException {
        return enviaNfceResult(xml, numeroLote, codigoUF, tipoAmbiente, certificado).toLegacyList();
    }

    public NfceAuthorizationResult enviaNfceResult(String xml, Long numeroLote, String codigoUF, TipoAmbienteEnum tipoAmbiente, Certificado certificado) throws FiscalException, InterruptedException {
        String conteudoXml = sefazGateway.removeXmlTag(xml);

        conteudoXml = sefazGateway.pegaTag2(conteudoXml, "NFe");

        String xmlEvento = montaXmlEvento(numeroLote, conteudoXml);

        String url = sefazGateway.getUrlNFCe(TipoServicoEnum.NFCE_NFE_AUTORIZACAO, codigoUF, Integer.parseInt(tipoAmbiente.getCodigo()));

        String retornoConsulta = sefazGateway.consulta(url, xmlEvento, certificado, "http://www.portalfiscal.inf.br/nfe/wsdl/NFeAutorizacao4/nfeAutorizacaoLote");

        if (retornoConsulta != null && !retornoConsulta.isBlank()) {
            String infProt = retornoConsulta.contains("infProt") ? sefazGateway.pegaTag(retornoConsulta, "infProt") : sefazGateway.pegaTag(retornoConsulta, "retEnviNFe");

            if (sefazGateway.pegaTag(infProt, "cStat").equals("100") && sefazGateway.pegaTag(infProt, "xMotivo").equals("Autorizado o uso da NF-e")) {
                return montaRetorno(sefazGateway.pegaTag2(retornoConsulta, "protNFe"));
            } else {
                throw new FiscalException("Aviso", "CSTAT - " + sefazGateway.pegaTag(infProt, "cStat") + " <br> Motivo - " + sefazGateway.pegaTag(infProt, "xMotivo"));
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

    private NfceAuthorizationResult montaRetorno(String respostaConsulta) {
        return new NfceAuthorizationResult(
                sefazGateway.pegaTag(respostaConsulta, "tpAmb"),
                sefazGateway.pegaTag(respostaConsulta, "verAplic"),
                sefazGateway.pegaTag(respostaConsulta, "dhRecbto"),
                sefazGateway.pegaTag(respostaConsulta, "nProt"),
                sefazGateway.pegaTag(respostaConsulta, "digVal"),
                sefazGateway.pegaTag(respostaConsulta, "cStat"),
                sefazGateway.pegaTag(respostaConsulta, "xMotivo"),
                sefazGateway.pegaTag(respostaConsulta, "cUF"),
                sefazGateway.pegaTag(respostaConsulta, "chNFe"),
                sefazGateway.pegaTag(respostaConsulta, "versao").isEmpty() ? "Sem Versao" : sefazGateway.pegaTag(respostaConsulta, "versao"),
                respostaConsulta
        );
    }
}
