package com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.services;

import com.lucast.vetcare.fiscal.enums.AssinaturaEnum;
import com.lucast.vetcare.fiscal.enums.ServicosNFeEnum;
import com.lucast.vetcare.fiscal.enums.TipoServicoEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests.RequestCancelarNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests.RequestValidaNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.services.AssinarNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.services.ValidaNFeService;
import com.lucast.vetcare.fiscal.util.FiscalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
public class CancelarNFeService {

    private static final Logger logger = LoggerFactory.getLogger(CancelarNFeService.class);

    public static final String STR_X_MOTIVO = "xMotivo";
    public static final String STR_C_STAT = "cStat";

    public String cancelaNFe(RequestCancelarNFe request, ValidaNFeService validaNFeService, AssinarNFeService assinarNFeService) throws FiscalException {

        long startTime = System.currentTimeMillis();
        logger.info("Início do Cancelamento NFe. Chave: {}, CNPJ: {}", request.getChaveNFe(), request.getCnpj());

        try {
            String url = FiscalUtils.getUrlNFe(TipoServicoEnum.EVENTO, request.getCodigoUF(), request.getTipoAmbiente().getCodigo(), request.getTipoEmissao());
            String xmlEvento = criaXmlEvento(request, assinarNFeService);

            boolean validada = validaNFeService.validaXml(montaRequestValidaNFe(xmlEvento, ServicosNFeEnum.CANCELAMENTO));
            if (!validada) {
                logger.warn("Cancelamento não validado. Chave: {}", request.getChaveNFe());
                throw new FiscalException("Aviso", "Cancelamento não validado!");
            }

            String xmlConsulta = montaXmlConsulta(xmlEvento);
            String retornoConsulta = FiscalUtils.consulta(url, xmlConsulta, request.getCertificado(), "http://www.portalfiscal.inf.br/nfe/wsdl/NFeRecepcaoEvento4/nfeRecepcaoEvento");

            String retEnvEvento = FiscalUtils.pegaTag(retornoConsulta, "retEnvEvento");
            String infEvento = FiscalUtils.pegaTag(retornoConsulta, "infEvento");

            List<String> retorno = montaRetorno(infEvento);

            if (!"128".equals(FiscalUtils.pegaTag(retEnvEvento, STR_C_STAT))) {
                String cStat = FiscalUtils.pegaTag(retEnvEvento, STR_C_STAT);

                String xMotivo = FiscalUtils.pegaTag(retEnvEvento, STR_X_MOTIVO);
                logger.error("Erro no envio do Cancelamento. Retorno: {} Motivo: {}", cStat, xMotivo);
                throw new FiscalException("Aviso", "Retorno do Evento -> cStat: " + cStat
                        + " | Motivo: " + xMotivo
                        + " | Informações do Evento -> cStat: " + FiscalUtils.pegaTag(infEvento, STR_C_STAT)
                        + " | Motivo: " + FiscalUtils.pegaTag(infEvento, STR_X_MOTIVO));
            } else if (!"135".equals(retorno.get(3))) {
                String cStat = FiscalUtils.pegaTag(infEvento, STR_C_STAT);
                String xMotivo = FiscalUtils.pegaTag(infEvento, STR_X_MOTIVO);

                logger.error("Erro no processamento do evento Cancelamento. InfEvento: cStat={}, xMotivo={}", cStat, xMotivo);
                throw new FiscalException("Erro", "Informações do Evento -> cStat: " + cStat
                        + " | Motivo: " + FiscalUtils.pegaTag(infEvento, STR_X_MOTIVO));
            }

            logger.info("Cancelamento NFe processado com sucesso. Chave: {}", request.getChaveNFe());
            return montaXmlFinal(FiscalUtils.pegaTag2(xmlEvento, "evento"), FiscalUtils.pegaTag2(retornoConsulta, "retEvento"));

        } catch (Exception e) {
            throw new FiscalException("Erro", "Erro inesperado no Cancelamento: " + e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("Fim do Cancelamento NFe. Tempo total: {} ms", (endTime - startTime));
        }
    }

    private RequestValidaNFe montaRequestValidaNFe(String xml, ServicosNFeEnum servico) {

        return new RequestValidaNFe.Builder()
                .xmlAssinado(xml)
                .servico(servico)
                .build();
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

    private String criaXmlEvento(RequestCancelarNFe request, AssinarNFeService assinarNFeService) throws FiscalException {
        try {
            String dhEvento = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX").format(request.getDataEvento().atOffset(ZoneOffset.ofHours(-3)));

            String dadosXml = "<envEvento xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"1.00\">" +
                    "<idLote>" + 1L + "</idLote>";

            String id = "ID" + "110111" + request.getChaveNFe() + String.format("%02d", 1L);

            String dadosMsg = "<evento xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"1.00\">" +
                    "<infEvento Id=\"" + id + "\">" +
                    "<cOrgao>" + request.getCodigoUF() + "</cOrgao>" +
                    "<tpAmb>" + request.getTipoAmbiente().getCodigo() + "</tpAmb>" +
                    "<CNPJ>" + request.getCnpj() + "</CNPJ>" +
                    "<chNFe>" + request.getChaveNFe() + "</chNFe>" +
                    "<dhEvento>" + dhEvento + "</dhEvento>" +
                    "<tpEvento>" + "110111" + "</tpEvento>" +
                    "<nSeqEvento>" + 1L + "</nSeqEvento>" +
                    "<verEvento>1.00</verEvento>" +
                    "<detEvento versao=\"1.00\">" +
                    "<descEvento>Cancelamento</descEvento>" +
                    "<nProt>" + request.getProtocolo() + "</nProt>" +
                    "<xJust>" + request.getJustificativa() + "</xJust>" +
                    "</detEvento>" +
                    "</infEvento>" +
                    "</evento>";

            dadosMsg = assinarNFeService.assinaEvento(dadosMsg, request.getCertificado(), AssinaturaEnum.EVENTO);
            dadosXml += dadosMsg + "</envEvento>";

            return dadosXml;
        } catch (Exception e) {
            throw new FiscalException("Erro ao assinar o XML - Cancelamento: " + e.getMessage(), e);
        }
    }

    private List<String> montaRetorno(String respostaConsulta) {
        List<String> retorno = new ArrayList<>();

        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "tpAmb"));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "verAplic"));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "cOrgao"));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, STR_C_STAT));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, STR_X_MOTIVO));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "chNFe"));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "tpEvento"));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "xEvento"));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "nSeqEvento"));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "dhRegEvento"));

        logger.info("Retorno Cancelamento montado: {}", retorno);

        return retorno;
    }
}
