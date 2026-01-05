package com.lucast.vetcare.fiscal.nfe.funcionalidades.consultas.services;

import com.lucast.vetcare.fiscal.enums.TipoConsultaCadastroEnum;
import com.lucast.vetcare.fiscal.enums.TipoServicoEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.consultas.requests.RequestConsultaCadastro;
import com.lucast.vetcare.fiscal.util.FiscalUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Scope("prototype")
public class ConsultaCadastroService {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaCadastroService.class);

    public List<String> consultaCadastro(RequestConsultaCadastro request) throws FiscalException {
        long startTime = System.currentTimeMillis();
        logger.info("Início da consulta cadastro. UF: {}, TipoConsulta: {}, Valor: {}",
                request.getUf(), request.getTipoConsultaCadastro(), request.getValorConsultaCadastro());

        try {
            Integer codigoUF = FiscalUtils.ufToCodUf(request.getUf());
            String url = FiscalUtils.getUrlNFe(TipoServicoEnum.CONSULTA_CADASTRO, codigoUF.toString(), request.getTipoAmbiente().getCodigo(), "");

            if (url == null || url.isEmpty()) {
                logger.error("URL de consulta cadastro não encontrada para UF: {}", request.getUf());
                throw new FiscalException("Erro", "URL de consulta não encontrada!");
            }

            String xml = xmlConsulta(request.getUf(), request.getTipoConsultaCadastro(), request.getValorConsultaCadastro());
            String retornoConsulta = FiscalUtils.consulta(url, xml, request.getCertificado(), "http://www.portalfiscal.inf.br/nfe/wsdl/NfeConsulta2");

            List<String> retorno =  montaRetorno(FiscalUtils.pegaTag(retornoConsulta, "retConsCad"));

            String cStat = FiscalUtils.pegaTag(retornoConsulta, "cStat");
            logger.info("Consulta cadastro realizada com sucesso. Status: {}", cStat);
            return retorno;
        } catch (Exception e) {
            throw new FiscalException("Erro inesperado na consulta cadastro: " + e.getMessage(), e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("Fim da consulta cadastro. Tempo total: {} ms", (endTime - startTime));
        }
    }

    private List<String> montaRetorno(String respostaConsulta) {
        List<String> retorno = new ArrayList<>();

        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "tpAmb"));     // Tipo ambiente
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "verAplic"));  // Versão
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "dhCons"));    // Data e Hora da consulta
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "cStat"));     // Código do Status
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "xMotivo"));   // Motivo
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "UF"));        // Sigla UF
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "cUF"));       // Código UF
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "infCad"));    // Informações do cadastro
        retorno.add(respostaConsulta);                                   // Retorno completo

        logger.info("Retorno consulta cadastro montado: {}", retorno);

        return retorno;
    }

    private String xmlConsulta(String uf, TipoConsultaCadastroEnum tipoConsultaCadastro, String valorConsultaCadastro) {
        StringBuilder dadosMsg = new StringBuilder();
        dadosMsg.append("<ConsCad versao=\"2.00\" xmlns=\"http://www.portalfiscal.inf.br/nfe\">")
                .append("<infCons>")
                .append("<xServ>CONS-CAD</xServ>")
                .append("<UF>").append(uf).append("</UF>");

        switch (tipoConsultaCadastro) {
            case IE -> dadosMsg.append("<IE>").append(valorConsultaCadastro).append("</IE>");
            case CNPJ -> dadosMsg.append("<CNPJ>").append(valorConsultaCadastro).append("</CNPJ>");
            case CPF -> dadosMsg.append("<CPF>").append(valorConsultaCadastro).append("</CPF>");
        }

        dadosMsg.append("</infCons>").append("</ConsCad>");

        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
                + "xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"
                + "<soap12:Header>"
                + "<nfeCabecMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/CadConsultaCadastro4\">"
                + "</nfeCabecMsg>"
                + "</soap12:Header>"
                + "<soap12:Body>"
                + "<nfeDadosMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/CadConsultaCadastro4\">"
                + dadosMsg
                + "</nfeDadosMsg>"
                + "</soap12:Body>"
                + "</soap12:Envelope>";
    }
}
