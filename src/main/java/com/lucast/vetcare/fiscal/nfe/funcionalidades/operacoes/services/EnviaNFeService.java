package com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.services;

import java.util.ArrayList;
import java.util.List;

import com.lucast.vetcare.fiscal.enums.TipoServicoEnum;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests.RequestEnviaNFe;
import com.lucast.vetcare.fiscal.util.FiscalUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lucast.vetcare.fiscal.exception.FiscalException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class EnviaNFeService {

    private static final Logger logger = LoggerFactory.getLogger(EnviaNFeService.class);

    private static final String STR_X_MOTIVO = "xMotivo";
    private static final String STR_C_STAT = "cStat";

    public List<String> enviaNFe(RequestEnviaNFe request) throws FiscalException {
        long startTime = System.currentTimeMillis();
        logger.info("Início do envio da NFe. Lote: {}, Tipo Emissão: {}, UF: {}", request.getNumeroLote(), request.getTipoEmissao(), request.getCodigoUF());

        try {
            String conteudoXml = FiscalUtils.removeXMLTag(request.getXml());
            conteudoXml = FiscalUtils.pegaTag2(conteudoXml, "NFe");
            String xmlEvento = montaXmlEvento(request.getNumeroLote(), conteudoXml, request.getIndSinc());
            String url = FiscalUtils.getUrlNFe(TipoServicoEnum.NFE_AUTORIZACAO, request.getCodigoUF(), request.getTipoAmbiente().getCodigo(), request.getTipoEmissao());

            String retornoConsulta = FiscalUtils.consulta(url, xmlEvento, request.getCertificado(), "http://www.portalfiscal.inf.br/nfe/wsdl/NFeAutorizacao4/nfeAutorizacaoLote");

            if (retornoConsulta != null && !retornoConsulta.isBlank()) {
                String infProt = retornoConsulta.contains("infProt") ? FiscalUtils.pegaTag(retornoConsulta, "infProt") : FiscalUtils.pegaTag(retornoConsulta, "retEnviNFe");

                if ("100".equals(FiscalUtils.pegaTag(infProt, STR_C_STAT)) && "Autorizado o uso da NF-e".equals(FiscalUtils.pegaTag(infProt, STR_X_MOTIVO))) {
                    List<String> retorno = montaRetorno(FiscalUtils.pegaTag2(retornoConsulta, "protNFe"));
                    String nProt = FiscalUtils.pegaTag(infProt, "nProt");
                    logger.info("NFe autorizada com sucesso. Protocolo: {}", nProt);
                    return retorno;
                } else {
                    String msgErro = !StringUtils.isBlank(infProt)
                            ? "CSTAT - " + FiscalUtils.pegaTag(infProt, STR_C_STAT) + " | Motivo - " + FiscalUtils.pegaTag(infProt, STR_X_MOTIVO)
                            : "CSTAT - " + FiscalUtils.pegaTag(retornoConsulta, STR_C_STAT) + " | Motivo - " + FiscalUtils.pegaTag(retornoConsulta, STR_X_MOTIVO);
                    logger.warn("Erro na autorização da NFe: {}", msgErro);
                    throw new FiscalException("Aviso", msgErro);
                }
            } else {
                logger.error("Retorno da consulta NFe vazio");
                throw new FiscalException("Erro", "Erro ao realizar a consulta");
            }
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("Fim do envio da NFe. Tempo total: {} ms", (endTime - startTime));
        }
    }

    private String montaXmlEvento(Long numeroLote, String conteudoXml, int indSinc) {
        String xml = "<enviNFe xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">"
                + "<idLote>" + numeroLote + "</idLote>"
                + "<indSinc>" + indSinc + "</indSinc>"
                + conteudoXml
                + "</enviNFe>";

        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
                + "xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"
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

    private List<String> montaRetorno(String respostaConsulta) {
        List<String> retorno = new ArrayList<>();

        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "tpAmb"));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "verAplic"));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "dhRecbto"));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "nProt"));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "digVal"));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, STR_C_STAT));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, STR_X_MOTIVO));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "cUF"));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "chNFe"));
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "versao").isEmpty() ? "Sem Versao" : FiscalUtils.pegaTag(respostaConsulta, "versao"));
        retorno.add(respostaConsulta);

        logger.info("Retorno NFe montado: {}", retorno);

        return retorno;
    }
}
