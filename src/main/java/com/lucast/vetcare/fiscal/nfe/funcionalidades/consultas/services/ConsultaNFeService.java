package com.lucast.vetcare.fiscal.nfe.funcionalidades.consultas.services;

import java.util.ArrayList;
import java.util.List;

import com.lucast.vetcare.fiscal.enums.TipoServicoEnum;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.consultas.requests.RequestConsultaNFe;
import com.lucast.vetcare.fiscal.util.FiscalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lucast.vetcare.fiscal.exception.FiscalException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class ConsultaNFeService {

    private static final Logger logger = LoggerFactory.getLogger(ConsultaNFeService.class);

    public List<String> consultaNFe(RequestConsultaNFe request) throws FiscalException {
        long startTime = System.currentTimeMillis();
        logger.info("Início da consulta NFe. Chave: {}, UF: {}, Tipo Emissão: {}", request.getChaveNFe(), request.getCodigoUF(), request.getTipoEmissao());

        try {
            String url = FiscalUtils.getUrlNFe(TipoServicoEnum.CONSULTA_PROTOCOLO, request.getCodigoUF(), request.getTipoAmbiente().getCodigo(), request.getTipoEmissao());

            if (url.isEmpty()) {
                logger.error("URL de consulta não encontrada para UF: {} e tipo emissão: {}", request.getCodigoUF(), request.getTipoEmissao());
                throw new FiscalException("Erro", "URL de consulta não encontrada!");
            }

            String xml = xmlConsulta(request.getTipoAmbiente().getCodigo(), request.getChaveNFe());
            String retornoConsulta = FiscalUtils.consulta(url, xml, request.getCertificado(), "http://www.portalfiscal.inf.br/nfe/wsdl/NfeConsulta2");

            List<String> retorno = montaRetorno(FiscalUtils.pegaTag(retornoConsulta, "retConsSitNFe"));

            String protocolo = FiscalUtils.pegaTag(retornoConsulta, "nProt");
            logger.info("Consulta NFe realizada com sucesso. Protocolo: {}", protocolo);

            return retorno;
        } catch (FiscalException e) {
            throw new FiscalException("Erro fiscal na consulta NFe: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new FiscalException("Erro inesperado na consulta NFe: " + e.getMessage(), e);
        } finally {
            long endTime = System.currentTimeMillis();
            logger.info("Fim da consulta NFe. Tempo total: {} ms", (endTime - startTime));
        }
    }

    private List<String> montaRetorno(String respostaConsulta) {
        List<String> retorno = new ArrayList<>();

        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "tpAmb"));          // Tipo ambiente
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "verAplic"));       // Versão
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "dhRecbto"));       // Data e Hora do Recebimento
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "nProt"));          // Número do Protocolo
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "digVal"));         // DigVal
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "cStat"));          // Código do Status
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "xMotivo"));        // Motivo
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "cUF"));            // UF
        retorno.add(FiscalUtils.pegaTag(respostaConsulta, "chNFe"));          // Chave NFe
        retorno.add(FiscalUtils.pegaTag2(respostaConsulta, "protNFe"));       // Protocolo NFe
        retorno.add(FiscalUtils.pegaTag2(respostaConsulta, "infProt"));       // Informações do protocolo
        retorno.add(FiscalUtils.pegaTag2(respostaConsulta, "retCancNFe"));    // Retorno Cancelamento NFe
        retorno.add(FiscalUtils.pegaTag2(respostaConsulta, "procEventoNFe")); // Evento NFe
        retorno.add(respostaConsulta);                                            // Retorno completo da consulta

        logger.info("Retorno NFe montado: {}", retorno);

        return retorno;
    }

    private String xmlConsulta(String tipoAmbiente, String chaveNFe) {
        String dadosMsg = "<consSitNFe versao=\"4.00\" xmlns=\"http://www.portalfiscal.inf.br/nfe\">"
                + "<tpAmb>" + tipoAmbiente + "</tpAmb>"
                + "<xServ>CONSULTAR</xServ>"
                + "<chNFe>" + chaveNFe + "</chNFe>"
                + "</consSitNFe>";

        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
                + "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
                + "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
                + "xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"
                + "<soap12:Body>"
                + "<nfeDadosMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeConsultaProtocolo4\">"
                + dadosMsg
                + "</nfeDadosMsg>"
                + "</soap12:Body>"
                + "</soap12:Envelope>";
    }
}
