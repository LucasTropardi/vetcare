package com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.requests.RequestImprimirCCeNFe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.requests.RequestImprimirNFCe;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.requests.RequestImprimirNFe;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.util.impressao.Impressao;
import com.lucast.vetcare.fiscal.util.impressao.ImpressaoService;
import com.lucast.vetcare.fiscal.util.impressao.ImpressaoUtil;
import com.lucast.vetcare.fiscal.nfe.print.NFeCCePrint;
import com.lucast.vetcare.fiscal.nfe.print.NfeProcPrint;

import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.codec.binary.Base64;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

@Service
@Scope("prototype")
public class ImprimirNFeService {

    private static final Logger logger = LoggerFactory.getLogger(ImprimirNFeService.class);

    public String imprimirNFe(RequestImprimirNFe request) throws FiscalException {
        try {
            logger.info("Iniciando impressão da NF-e");

            NfeProcPrint nfeProcPrint = new NfeProcPrint(request.getXml());

            List<NfeProcPrint> nfeProcPrints = new ArrayList<>();
            nfeProcPrints.add(nfeProcPrint);

            HashMap<String, Object> params = new HashMap<>();
            params.put("LOGO", request.getLogo());
            params.put("PRODUTOSLIST", nfeProcPrint.getProd());
            params.put("DUPLICATASLIST", nfeProcPrint.getDuplicatas());
            params.put("BACKGROUND", Objects.requireNonNull(getClass().getResourceAsStream("/images/bg_ambiente_homologacao.png")));
            params.put("IDENTIFICACAO", request.getIdentificacao());
            params.put(JRParameter.REPORT_LOCALE, Locale.forLanguageTag("pt-BR"));

            logger.info("Compilando relatório DANFE");
            JasperReport report = JasperCompileManager.compileReport(
                    getClass().getResourceAsStream("/jrxml/nfe/danfe.jrxml"));

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(nfeProcPrints);

            JasperPrint print = JasperFillManager.fillReport(report, params, dataSource);

            byte[] bytesReport = JasperExportManager.exportReportToPdf(print);
            String base64 = Base64.encodeBase64String(bytesReport);

            logger.info("Impressão da NF-e concluída com sucesso");
            return base64;
        } catch (Exception e) {
            throw new FiscalException("Erro ao imprimir a danfe <br>" + e.getMessage(), e);
        }
    }

    public String imprimirCCe(RequestImprimirCCeNFe request) throws FiscalException {
        try {
            logger.info("Iniciando impressão da CCe");

            NFeCCePrint ccePrint = new NFeCCePrint(request.getXml(), request.getXmlEventoCCe());

            List<NFeCCePrint> ccePrints = new ArrayList<>();
            ccePrints.add(ccePrint);

            HashMap<String, Object> params = new HashMap<>();
            params.put(JRParameter.REPORT_LOCALE, Locale.forLanguageTag("pt-BR"));

            logger.info("Compilando relatório CCe");
            JasperReport report = JasperCompileManager.compileReport(
                    getClass().getResourceAsStream("/jrxml/nfe/cce.jrxml"));

            JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(ccePrints);

            JasperPrint print = JasperFillManager.fillReport(report, params, dataSource);

            byte[] bytesReport = JasperExportManager.exportReportToPdf(print);
            String base64 = Base64.encodeBase64String(bytesReport);

            logger.info("Impressão da CCe concluída com sucesso");
            return base64;
        } catch (Exception e) {
            throw new FiscalException("Erro ao imprimir a carta de correção <br>" + e.getMessage(), e);
        }
    }

    public String imprimirNFCe(RequestImprimirNFCe request) throws FiscalException {
        try {
            logger.info("Iniciando impressão da NFC-e");

            Impressao impressao = ImpressaoUtil.impressaoPadraoNFCe(request.getXml(), request.getUrlConsulta(), request.getLogo());
            String base64 = ImpressaoService.impressaoPdfBase64(impressao);

            logger.info("Impressão da NFC-e concluída com sucesso");
            return base64;
        } catch (Exception e) {
            throw new FiscalException("Erro ao imprimir a danfce <br>" + e.getMessage(), e);
        }
    }
}
