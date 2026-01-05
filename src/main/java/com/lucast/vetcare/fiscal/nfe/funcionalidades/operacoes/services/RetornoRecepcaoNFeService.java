package com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.services;

import java.util.ArrayList;
import java.util.List;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.enums.TipoServicoEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests.RequestRetornoRecepcaoNFe;
import com.lucast.vetcare.fiscal.util.FiscalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class RetornoRecepcaoNFeService {

	private static final Logger logger = LoggerFactory.getLogger(RetornoRecepcaoNFeService.class);

	public List<String> retornoNFe(List<String> retornoNFe, TipoAmbienteEnum tipoAmbiente, Certificado certificado, String codigoUF, String tipoEmissao) throws FiscalException {
		logger.info("Iniciando consulta de Retorno NFe - UF: {}, Ambiente: {}, Tipo Emissão: {}", codigoUF, tipoAmbiente, tipoEmissao);

		String url = FiscalUtils.getUrlNFe(TipoServicoEnum.NFE_RET_AUTORIZACAO, codigoUF, tipoAmbiente.getCodigo(), tipoEmissao);
		logger.info("URL de consulta NFe: {}", url);

		String xmlEvento = montaXmlEvento(retornoNFe.get(7), tipoAmbiente);
		logger.info("XML de Evento montado: {}", xmlEvento);

		String retornoConsulta = FiscalUtils.consulta(url, xmlEvento, certificado, "http://www.portalfiscal.inf.br/nfe/wsdl/nfeRetAutorizacaoLote");
		logger.info("Retorno da consulta: {}", retornoConsulta);

		if (retornoConsulta != null) {
			String infProt = retornoConsulta.contains("infProt") ? FiscalUtils.pegaTag(retornoConsulta, "infProt") : FiscalUtils.pegaTag(retornoConsulta, "retConsReciNFe");
			String cStat = FiscalUtils.pegaTag(infProt, "cStat");
			String xMotivo = FiscalUtils.pegaTag(infProt, "xMotivo");

			logger.info("Código Status: {}, Motivo: {}", cStat, xMotivo);

			if ("100".equals(cStat) && "Autorizado o uso da NF-e".equals(xMotivo)) {
				List<String> retorno =  montaRetorno(infProt, FiscalUtils.pegaTag2(retornoConsulta, "protNFe"));
				logger.info("NFe autorizada com sucesso. Retorno montado.");
				return retorno;
			} else if ("105".equals(cStat) && "Lote em processamento".equals(xMotivo)) {
				logger.warn("Lote em processamento. Aguarde antes de consultar novamente.");
				throw new FiscalException("Aviso", "CSTAT - " + cStat + " <br> Motivo - " + xMotivo + "<br> Aguarde um momento e realize uma consulta para atualizar os dados da NFe!");
			} else {
				logger.error("Erro na consulta NFe. CSTAT: {}, Motivo: {}", cStat, xMotivo);
				throw new FiscalException("Aviso", "CSTAT - " + cStat + " <br> Motivo - " + xMotivo);
			}
		} else {
			logger.warn("Retorno da consulta NFe é nulo.");
			return null;
		}
	}

	public List<String> retornoNFCe(ArrayList<String> retornoNFCe, TipoAmbienteEnum tipoAmbiente, Certificado certificado, String codigoUF) throws FiscalException, InterruptedException {
		logger.info("Iniciando consulta de Retorno NFCe - UF: {}, Ambiente: {}", codigoUF, tipoAmbiente);

		String url = FiscalUtils.getUrlNFCe(TipoServicoEnum.NFCE_RET_AUTORIZACAO, codigoUF, Integer.parseInt(tipoAmbiente.getCodigo()));
		logger.info("URL de consulta NFCe: {}", url);

		String xmlEvento = montaXmlEvento(retornoNFCe.get(7), tipoAmbiente);
		logger.info("XML de Evento montado: {}", xmlEvento);

		String retornoConsulta = FiscalUtils.consulta(url, xmlEvento, certificado, "http://www.portalfiscal.inf.br/nfe/wsdl/NFeRetAutorizacao4/NFeRetAutorizacaoLote");
		logger.info("Retorno da consulta: {}", retornoConsulta);

		if (retornoConsulta != null) {
			String infProt = retornoConsulta.contains("infProt") ? FiscalUtils.pegaTag(retornoConsulta, "infProt") : FiscalUtils.pegaTag(retornoConsulta, "retConsReciNFe");
			String cStat = FiscalUtils.pegaTag(infProt, "cStat");
			String xMotivo = FiscalUtils.pegaTag(infProt, "xMotivo");

			logger.info("Código Status: {}, Motivo: {}", cStat, xMotivo);

			if ("100".equals(cStat) && "Autorizado o uso da NF-e".equals(xMotivo)) {
				List<String> retorno = montaRetorno(infProt, FiscalUtils.pegaTag2(retornoConsulta, "protNFe"));
				logger.info("NFCe autorizada com sucesso. Retorno montado.");
				return retorno;
			} else if ("105".equals(cStat) && xMotivo.contains("Lote em processamento")) {
				logger.warn("Lote em processamento. Aguarde antes de consultar novamente.");
				throw new FiscalException("Aviso", "CSTAT - " + cStat + " <br> Motivo - " + xMotivo + "<br> Aguarde um momento e realize uma consulta para atualizar os dados da NFe!");
			} else {
				logger.error("Erro na consulta NFCe. CSTAT: {}, Motivo: {}", cStat, xMotivo);
				throw new FiscalException("Aviso", "CSTAT - " + cStat + " <br> Motivo - " + xMotivo);
			}
		} else {
			logger.warn("Retorno da consulta NFCe é nulo.");
			return null;
		}
	}

	public List<String> retornoNFCe2(RequestRetornoRecepcaoNFe request) throws FiscalException {
		logger.info("Iniciando consulta de Retorno NFCe2 - UF: {}, Ambiente: {}, Número Recebimento: {}", request.getCodigoUF(), request.getTipoAmbiente(), request.getNumeroRecebimento());

		String url = FiscalUtils.getUrlNFCe(TipoServicoEnum.NFCE_RET_AUTORIZACAO, request.getCodigoUF(), Integer.parseInt(request.getTipoAmbiente().getCodigo()));
		logger.info("URL de consulta NFCe2: {}", url);

		String xmlEvento = montaXmlEvento(request.getNumeroRecebimento(), request.getTipoAmbiente());
		logger.info("XML de Evento montado: {}", xmlEvento);

		String retornoConsulta = FiscalUtils.consulta(url, xmlEvento, request.getCertificado(), "http://www.portalfiscal.inf.br/nfe/wsdl/NFeRetAutorizacao4/NFeRetAutorizacaoLote");
		logger.info("Retorno da consulta: {}", retornoConsulta);

		if (retornoConsulta != null) {
			String infProt = retornoConsulta.contains("infProt") ? FiscalUtils.pegaTag(retornoConsulta, "infProt") : FiscalUtils.pegaTag(retornoConsulta, "retConsReciNFe");
			String cStat = FiscalUtils.pegaTag(infProt, "cStat");
			String xMotivo = FiscalUtils.pegaTag(infProt, "xMotivo");

			logger.info("Código Status: {}, Motivo: {}", cStat, xMotivo);

			if ("100".equals(cStat) && "Autorizado o uso da NF-e".equals(xMotivo)) {
				List<String> retorno =  montaRetorno(infProt, FiscalUtils.pegaTag2(retornoConsulta, "protNFe"));
				logger.info("NFCe2 autorizada com sucesso. Retorno montado.");
				return retorno;
			} else if ("105".equals(cStat) && xMotivo.contains("Lote em processamento")) {
				logger.warn("Lote em processamento. Aguarde antes de consultar novamente.");
				throw new FiscalException("Aviso", "CSTAT - " + cStat + " <br> Motivo - " + xMotivo + "<br> Aguarde um momento e realize uma consulta para atualizar os dados da NFe!");
			} else {
				logger.error("Erro na consulta NFCe2. CSTAT: {}, Motivo: {}", cStat, xMotivo);
				throw new FiscalException("Aviso", "CSTAT - " + cStat + " <br> Motivo - " + xMotivo);
			}
		} else {
			logger.warn("Retorno da consulta NFCe2 é nulo.");
			return null;
		}
	}

	private List<String> montaRetorno(String respostaConsulta, String protocolo) {
		List<String> retorno = new ArrayList<>();

		logger.info("Montando retorno da NF-e/NFCe...");
		retorno.add(FiscalUtils.pegaTag(respostaConsulta, "tpAmb"));            // Tipo ambiente
		retorno.add(FiscalUtils.pegaTag(respostaConsulta, "verAplic"));         // Versão
		retorno.add(FiscalUtils.pegaTag(respostaConsulta, "dhRecbto"));         // Data e Hora do Recebimento
		retorno.add(FiscalUtils.pegaTag(respostaConsulta, "nProt"));            // Número do Protocolo
		retorno.add(FiscalUtils.pegaTag(respostaConsulta, "digVal"));           // DigVal
		retorno.add(FiscalUtils.pegaTag(respostaConsulta, "cStat"));            // Código do Status
		retorno.add(FiscalUtils.pegaTag(respostaConsulta, "xMotivo"));          // Motivo
		retorno.add(FiscalUtils.pegaTag(respostaConsulta, "cUF"));              // UF
		retorno.add(FiscalUtils.pegaTag(respostaConsulta, "chNFe"));            // Chave NFe
		retorno.add(protocolo);                                                     // Salva a TAG protNFe
		logger.info("Retorno montado: {}", retorno);
		return retorno;
	}

	private String montaXmlEvento(String nRec, TipoAmbienteEnum tipoAmbiente) {
		String dadosDoXml = "<consReciNFe xmlns=\"http://www.portalfiscal.inf.br/nfe\" versao=\"4.00\">"
				+ "<tpAmb>" + tipoAmbiente.getCodigo() + "</tpAmb>"
				+ "<nRec>" + nRec + "</nRec>"
				+ "</consReciNFe>";

		String xmlCompleto = "<?xml version=\"1.0\" encoding=\"utf-8\"?>"
				+ "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">"
				+ "<soap12:Header>"
				+ "<nfeCabecMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeRetAutorizacao4\">"
				+ "</nfeCabecMsg>"
				+ "</soap12:Header>"
				+ "<soap12:Body>"
				+ "<nfeDadosMsg xmlns=\"http://www.portalfiscal.inf.br/nfe/wsdl/NFeRetAutorizacao4\">"
				+ dadosDoXml
				+ "</nfeDadosMsg>"
				+ "</soap12:Body>"
				+ "</soap12:Envelope>";

		logger.info("XML completo montado: {}", xmlCompleto);
		return xmlCompleto;
	}
}
