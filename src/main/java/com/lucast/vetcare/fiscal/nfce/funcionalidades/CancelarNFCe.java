package com.lucast.vetcare.fiscal.nfce.funcionalidades;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.application.port.out.SefazGateway;
import com.lucast.vetcare.fiscal.enums.AssinaturaEnum;
import com.lucast.vetcare.fiscal.enums.ServicosNFeEnum;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.enums.TipoServicoEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfce.result.NfceCancellationResult;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Service
public class CancelarNFCe {

    private final SefazGateway sefazGateway;
    private final ValidaNFCe validaNFCe;
    private final AssinarNFCe assinarNFCe;

    public CancelarNFCe(SefazGateway sefazGateway, ValidaNFCe validaNFCe, AssinarNFCe assinarNFCe) {
        this.sefazGateway = sefazGateway;
        this.validaNFCe = validaNFCe;
        this.assinarNFCe = assinarNFCe;
    }

    public ArrayList<String> cancelarNFCe(String justificativa, String uf, String cnpj, String chaveNFCe, String protocolo, LocalDateTime dataEvento, String tipoEmissao, TipoAmbienteEnum tipoAmbiente, Certificado certificado) throws FiscalException {
        return cancelarNfceResult(justificativa, uf, cnpj, chaveNFCe, protocolo, dataEvento, tipoEmissao, tipoAmbiente, certificado).toLegacyList();
    }

    public NfceCancellationResult cancelarNfceResult(String justificativa, String uf, String cnpj, String chaveNFCe, String protocolo, LocalDateTime dataEvento, String tipoEmissao, TipoAmbienteEnum tipoAmbiente, Certificado certificado) throws FiscalException {
        String url = sefazGateway.getUrlNFCe(TipoServicoEnum.NFCE_RECEPCAO_EVENTO, uf, Integer.parseInt(tipoAmbiente.getCodigo()));

        String xmlEvento = createXml(sefazGateway.ufToCodUf(uf), cnpj, chaveNFCe, 1L, "110111", 1L, protocolo, dataEvento, tipoAmbiente, justificativa, certificado);

        boolean validada = validaNFCe.validaXml(xmlEvento, ServicosNFeEnum.CANCELAMENTO);

        if (validada) {
            String xmlConsulta = montaXmlConsulta(xmlEvento);

            String retornoConsulta = sefazGateway.consulta(url, xmlConsulta, certificado, "http://www.portalfiscal.inf.br/nfe/wsdl/NFeRecepcaoEvento4/nfeRecepcaoEvento");

            String retEnvEvento = sefazGateway.pegaTag(retornoConsulta, "retEnvEvento");

            String infEvento = sefazGateway.pegaTag(retornoConsulta, "infEvento");

            NfceCancellationResult retorno = montaRetorno(montaXmlFinal(sefazGateway.pegaTag2(xmlEvento, "evento"), sefazGateway.pegaTag2(retornoConsulta, "retEvento")), url, xmlConsulta);

            if (!sefazGateway.pegaTag(retEnvEvento, "cStat").equals("128")) {
                throw new FiscalException("Aviso", "Retorno do Evento ->  cSat - " + sefazGateway.pegaTag(retEnvEvento, "cStat") + " <br> Motivo - " + sefazGateway.pegaTag(retEnvEvento, "xMotivo") +
                        "<br> Informações do Evento ->  cSat - " + sefazGateway.pegaTag(infEvento, "cStat") + " <br> Motivo - " + sefazGateway.pegaTag(infEvento, "xMotivo"));
            } else if (!"135".equals(retorno.cStat())) {
                throw new FiscalException("Erro", "Informações do Evento ->  cSat - " + sefazGateway.pegaTag(infEvento, "cStat") + " <br> Motivo - " + sefazGateway.pegaTag(infEvento, "xMotivo"));
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

        dadosMsg = assinarNFCe.assinaEvento(dadosMsg, certificado, AssinaturaEnum.EVENTO);

        dadosXml += dadosMsg + "</envEvento>";

        return dadosXml;
    }

    private NfceCancellationResult montaRetorno(String respostaConsulta, String url, String xmlConsulta) {
        return new NfceCancellationResult(
                sefazGateway.pegaTag(respostaConsulta, "tpAmb"),
                sefazGateway.pegaTag(respostaConsulta, "verAplic"),
                sefazGateway.pegaTag(respostaConsulta, "dhRegEvento"),
                sefazGateway.pegaTag(respostaConsulta, "tpEvento"),
                sefazGateway.pegaTag(respostaConsulta, "xEvento"),
                sefazGateway.pegaTag(respostaConsulta, "cStat"),
                sefazGateway.pegaTag(respostaConsulta, "xMotivo"),
                sefazGateway.pegaTag(respostaConsulta, "nSeqEvento"),
                sefazGateway.pegaTag(respostaConsulta, "chNFe"),
                sefazGateway.pegaTag(respostaConsulta, "cOrgao"),
                respostaConsulta,
                url,
                xmlConsulta
        );
    }
}
