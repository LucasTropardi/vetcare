package com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.services;

import java.util.ArrayList;
import java.util.List;

import com.lucast.vetcare.fiscal.enums.TipoServicoEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests.RequestInutilizaNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests.RequestValidaNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.services.AssinarNFeService;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.services.ValidaNFeService;
import com.lucast.vetcare.fiscal.util.FiscalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lucast.vetcare.fiscal.enums.AssinaturaEnum;
import com.lucast.vetcare.fiscal.enums.ServicosNFeEnum;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class InutilizaNFeService {

    private static final Logger logger = LoggerFactory.getLogger(InutilizaNFeService.class);

    public String inutilizar(RequestInutilizaNFe request, ValidaNFeService validaNFeService, AssinarNFeService assinarNFeService) throws FiscalException {

        logger.info("Iniciando inutilização da NFe: modelo={}, serie={}, ano={}, nfIni={}, nfFin={}, cnpj={}",
                request.getModelo(), request.getSerie(), request.getAno(), request.getNfIni(), request.getNfFin(), request.getCnpj());

        String url = FiscalUtils.getUrlNFe(TipoServicoEnum.INUTILIZACAO, request.getCodigoUF(), request.getTipoAmbiente().getCodigo(), "1");
        logger.info("URL para inutilização: {}", url);

        String xmlEvento = montaXmlEvento(request, assinarNFeService);
        logger.info("XML de evento gerado:\n{}", xmlEvento);

        boolean validada = validaNFeService.validaXml(montaRequestValidaNFe(xmlEvento));
        logger.info("Validação do XML concluída com sucesso? {}", validada);

        if (validada) {
            String xmlConsulta = montaXmlConsulta(xmlEvento);
            logger.info("XML de consulta gerado:\n{}", xmlConsulta);

            String retornoConsulta = FiscalUtils.consulta(url, xmlConsulta, request.getCertificado(),
                    "http://www.portalfiscal.inf.br/nfe/wsdl/NFeInutilizacao4/nfeInutilizacaoNF");

            logger.info("Retorno da consulta:\n{}", retornoConsulta);

            List<String> retorno = montaRetorno(FiscalUtils.pegaTag2(retornoConsulta, "retInutNFe"));

            if (retorno.size() > 2) {
                String codigoStatus = retorno.get(2);
                logger.info("Código de status da inutilização: {}", codigoStatus);
            } else {
                logger.warn("Código de status da inutilização não disponível, retorno incompleto: {}", retorno);
            }

            if (!retorno.get(2).equals("103")) {
                throw new FiscalException("Erro na inutilização: cStat - " + retorno.get(2) + " <br> Motivo - " + retorno.get(3));
            }

            logger.info("Inutilização realizada com sucesso.");
            return xmlEvento + FiscalUtils.pegaTag2(retornoConsulta, "retInutNFe");
        } else {
            logger.warn("Inutilização não validada!");
            throw new FiscalException("Aviso", "Inutilização não validada!");
        }
    }

    private RequestValidaNFe montaRequestValidaNFe(String xml) {

        return new RequestValidaNFe.Builder()
                .xmlAssinado(xml)
                .servico(ServicosNFeEnum.INUTILIZACAO)
                .build();
    }

    private List<String> montaRetorno(String respostaConsulta) {
        List<String> retorno = new ArrayList<>();

        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "tpAmb"));      // Tipo ambiente
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "verAplic"));   // Versão
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "cStat"));      // Código do Status
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "xMotivo"));    // Motivo
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "cUF"));        // UF
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "dhRecbto"));   // Data e Hora do Recebimento

        logger.info("Retorno inutilizar NFe montado: {}", retorno);

        return retorno;
    }

    private String montaXmlConsulta(String xmlEvento) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
                "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" " +
                "xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap12:Header>" +
                "<nfeCabecMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeInutilizacao4\">" +
                "</nfeCabecMsg>" +
                "</soap12:Header>" +
                "<soap12:Body>" +
                "<nfeDadosMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeInutilizacao4\">" +
                xmlEvento +
                "</nfeDadosMsg>" +
                "</soap12:Body>" +
                "</soap12:Envelope>";
    }

    private String montaXmlEvento(RequestInutilizaNFe request, AssinarNFeService assinarNFeService) throws FiscalException {

        String idInutilizacao = "ID" + request.getCodigoUF() + request.getAno() % 100 + request.getCnpj() + request.getModelo() +
                FiscalUtils.strZero(request.getSerie(), 3) +
                FiscalUtils.strZero(Integer.parseInt(request.getNfIni()), 9) +
                FiscalUtils.strZero(Integer.parseInt(request.getNfFin()), 9);

        String dadosMensagem = "<inutNFe xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">" +
                "<infInut Id=\"" + idInutilizacao + "\">" +
                "<tpAmb>" + request.getTipoAmbiente().getCodigo() + "</tpAmb>" +
                "<xServ>INUTILIZAR</xServ>" +
                "<cUF>" + request.getCodigoUF() + "</cUF>" +
                "<ano>" + request.getAno() % 100 + "</ano>" +
                "<CNPJ>" + request.getCnpj() + "</CNPJ>" +
                "<mod>" + request.getModelo() + "</mod>" +
                "<serie>" + request.getSerie() + "</serie>" +
                "<nNFIni>" + request.getNfIni() + "</nNFIni>" +
                "<nNFFin>" + request.getNfFin() + "</nNFFin>" +
                "<xJust>problema na geracao da sequencia</xJust>" +
                "</infInut>" +
                "</inutNFe>";

        String xmlAssinado = assinarNFeService.assinaInut(dadosMensagem, request.getCertificado(), AssinaturaEnum.INUTILIZACAO);
        logger.info("XML de inutilização assinado:\n{}", xmlAssinado);

        return xmlAssinado;
    }
}
