package com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.services;

import com.lucast.vetcare.fiscal.enums.AssinaturaEnum;
import com.lucast.vetcare.fiscal.enums.ServicosNFeEnum;
import com.lucast.vetcare.fiscal.enums.TipoServicoEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests.RequestCartaCorrecaoNFe;
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
public class CartaCorrecaoNFeService {

    private static final Logger logger = LoggerFactory.getLogger(CartaCorrecaoNFeService.class);

    public static final String STR_X_MOTIVO = "xMotivo";
    public static final String STR_C_STAT = "cStat";

    public List<String> cce(RequestCartaCorrecaoNFe request, ValidaNFeService validaNFeService, AssinarNFeService assinarNFeService) throws FiscalException {

        long startTime = System.currentTimeMillis();
        logger.info("Início da Carta de Correção. Chave: {}, Sequência: {}, CNPJ: {}", request.getChaveNFe(), request.getSequencia(), request.getCnpj());

        try {
            String url = FiscalUtils.getUrlNFe(
                    TipoServicoEnum.EVENTO,
                    request.getCodigoUF(),
                    request.getTipoAmbiente().getCodigo(),
                    request.getTipoEmissao()
            );

            String xmlEvento = montaXmlEvento(request, assinarNFeService);

            boolean validada = validaNFeService.validaXml(montaRequestValidaNFe(xmlEvento));

            if (!validada) {
                logger.warn("Carta de Correção não validada. Chave: {}", request.getChaveNFe());
                throw new FiscalException("Aviso", "Carta de Correção não validada!");
            }

            String xmlFinal = montaXmlConsulta(xmlEvento);
            String retornoEnvio = FiscalUtils.consulta(
                    url,
                    xmlFinal,
                    request.getCertificado(),
                    "http://www.portalfiscal.inf.br/nfe/wsdl/NFeRecepcaoEvento4/nfeRecepcaoEvento"
            );

            String retEnvEvento = FiscalUtils.pegaTag(retornoEnvio, "retEnvEvento");
            String infEvento = FiscalUtils.pegaTag(retornoEnvio, "infEvento");

            if (!"128".equals(FiscalUtils.pegaTag(retEnvEvento, STR_C_STAT))) {
                String msg = String.format("Retorno do Evento -> cStat - %s <br> Motivo - %s; Informações do Evento -> cStat - %s <br> Motivo - %s",
                        FiscalUtils.pegaTag(retEnvEvento, STR_C_STAT),
                        FiscalUtils.pegaTag(retEnvEvento, STR_X_MOTIVO),
                        FiscalUtils.pegaTag(infEvento, STR_C_STAT),
                        FiscalUtils.pegaTag(infEvento, STR_X_MOTIVO));
                logger.error(msg);
                throw new FiscalException("Aviso", msg);
            } else if (!"135".equals(FiscalUtils.pegaTag(infEvento, STR_C_STAT))) {
                String msg = String.format("Informações do Evento -> cStat - %s <br> Motivo - %s",
                        FiscalUtils.pegaTag(infEvento, STR_C_STAT),
                        FiscalUtils.pegaTag(infEvento, STR_X_MOTIVO));
                logger.error(msg);
                throw new FiscalException("Erro", msg);
            }

            logger.info("Carta de Correção processada com sucesso. Chave: {}", request.getChaveNFe());
            return montaRetorno(infEvento, montaXmlFinal(FiscalUtils.pegaTag2(xmlEvento, "evento"), FiscalUtils.pegaTag2(retornoEnvio, "retEvento")));
        } catch (Exception e) {
            throw new FiscalException("Erro", "Erro inesperado na Carta de Correção: " + e.getMessage());
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("Fim da Carta de Correção. Tempo total: {} ms", (endTime - startTime));
        }
    }

    private RequestValidaNFe montaRequestValidaNFe(String xml) {
        return new RequestValidaNFe.Builder()
                .xmlAssinado(xml)
                .servico(ServicosNFeEnum.CCE)
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

    private String montaXmlEvento(RequestCartaCorrecaoNFe request, AssinarNFeService assinarNFeService) throws FiscalException {
        try {
            String condicaoUso = "A Carta de Correcao e disciplinada pelo paragrafo 1o-A do art. 7o do Convenio S/N, " +
                    "de 15 de dezembro de 1970 e pode ser utilizada para regularizacao de erro ocorrido na " +
                    "emissao de documento fiscal, desde que o erro nao esteja relacionado com: " +
                    "I - as variaveis que determinam o valor do imposto tais como: base de calculo, aliquota, " +
                    "diferenca de preco, quantidade, valor da operacao ou da prestacao; " +
                    "II - a correcao de dados cadastrais que implique mudanca do remetente ou do destinatario; " +
                    "III - a data de emissao ou de saida.";

            String dadosXml = "<envEvento xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"1.00\">" +
                    "<idLote>" + request.getLote() + "</idLote>";

            String id = "ID110110" + request.getChaveNFe() + String.format("%02d", request.getSequencia());
            String dhEvento = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX").format(request.getDataEvento().atOffset(ZoneOffset.ofHours(-3)));

            String dadosMsg = "<evento xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"1.00\">" +
                    "<infEvento Id=\"" + id + "\">" +
                    "<cOrgao>" + request.getCodigoUF() + "</cOrgao>" +
                    "<tpAmb>" + request.getTipoAmbiente().getCodigo() + "</tpAmb>" +
                    "<CNPJ>" + request.getCnpj() + "</CNPJ>" +
                    "<chNFe>" + request.getChaveNFe() + "</chNFe>" +
                    "<dhEvento>" + dhEvento + "</dhEvento>" +
                    "<tpEvento>110110</tpEvento>" +
                    "<nSeqEvento>" + request.getSequencia() + "</nSeqEvento>" +
                    "<verEvento>1.00</verEvento>" +
                    "<detEvento versao=\"1.00\">" +
                    "<descEvento>Carta de Correcao</descEvento>" +
                    "<xCorrecao>" + request.getCorrecao() + "</xCorrecao>" +
                    "<xCondUso>" + condicaoUso + "</xCondUso>" +
                    "</detEvento>" +
                    "</infEvento>" +
                    "</evento>";

            dadosMsg = assinarNFeService.assinaEvento(dadosMsg, request.getCertificado(), AssinaturaEnum.EVENTO);
            dadosXml += dadosMsg + "</envEvento>";

            return dadosXml;
        } catch (Exception e) {
            throw new FiscalException("Erro", "Erro ao assinar o XML - CCe: " + e.getMessage());
        }
    }

    private List<String> montaRetorno(String respostaConsulta, String xmlFinal) {
        List<String> retorno = new ArrayList<>();

        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "tpAmb"));        // Tipo ambiente
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "verAplic"));     // Versão
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "cOrgao"));       // Órgão
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, STR_C_STAT));         // Código do Status
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, STR_X_MOTIVO));       // Motivo
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "chNFe"));        // Chave NFe
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "tpEvento"));     // Tipo Evento
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "xEvento"));      // Evento
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "nSeqEvento"));   // Sequência Evento
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "dhRegEvento"));  // Data e hora do registro do evento
        retorno.add(xmlFinal);
        retorno.add(respostaConsulta);

        logger.info("Retorno CCe montado: {}", retorno);

        return retorno;
    }
}
