package com.lucast.vetcare.fiscal.util.impressao;

import com.lucast.vetcare.fiscal.exception.FiscalException;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.export.JRPrintServiceExporter;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimplePrintServiceExporterConfiguration;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.commons.codec.binary.Base64;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.print.attribute.HashPrintRequestAttributeSet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.IOException;
import java.io.StringReader;

public class ImpressaoService {

    private ImpressaoService(){}

    public static void impressaoPdfArquivo(Impressao impressao, String destinoPdf) throws JRException, ParserConfigurationException, IOException, SAXException {
        JasperPrint jasperPrint = geraImpressao(impressao);
        JasperExportManager.exportReportToPdfFile(jasperPrint, destinoPdf);
    }

    public static byte[] impressaoPdfByte(Impressao impressao) throws JRException, ParserConfigurationException, IOException, SAXException {
        JasperPrint jasperPrint = geraImpressao(impressao);
        return JasperExportManager.exportReportToPdf(jasperPrint);
    }

    public static String impressaoPdfBase64(Impressao impressao) throws JRException, ParserConfigurationException, IOException, SAXException {
        return Base64.encodeBase64String(impressaoPdfByte(impressao));
    }

    public static void impressaoHtml(Impressao impressao, String destinoHtml) throws JRException, ParserConfigurationException, IOException, SAXException {
        JasperPrint jasperPrint = geraImpressao(impressao);
        JasperExportManager.exportReportToHtmlFile(jasperPrint, destinoHtml);
    }

    public static JasperViewer impressaoPreview(Impressao impressao) throws JRException, ParserConfigurationException, IOException, SAXException {
        JasperPrint jasperPrint = geraImpressao(impressao);
        return new JasperViewer(jasperPrint, true);
    }

    public static void impressaoDireta(Impressao impressao) throws JRException, ParserConfigurationException, IOException, SAXException, PrinterException, FiscalException {
        PrintService impressoraPadrao = PrintServiceLookup.lookupDefaultPrintService();
        impressaoDireta(impressao, impressoraPadrao, null);
    }

    public static void impressaoDireta(Impressao impressao, SimplePrintServiceExporterConfiguration configuration) throws JRException, ParserConfigurationException, IOException, SAXException, PrinterException, FiscalException {
        PrintService impressoraPadrao = PrintServiceLookup.lookupDefaultPrintService();
        impressaoDireta(impressao, impressoraPadrao, configuration);
    }

    public static void impressaoDireta(Impressao impressao, PrintService impressora) throws JRException, ParserConfigurationException, IOException, SAXException, PrinterException, FiscalException {
        SimplePrintServiceExporterConfiguration configuration = new SimplePrintServiceExporterConfiguration();
        configuration.setPrintRequestAttributeSet(new HashPrintRequestAttributeSet());
        configuration.setDisplayPageDialog(false);
        configuration.setDisplayPrintDialog(false);

        impressaoDireta(impressao, impressora, configuration);
    }

    public static void impressaoDireta(Impressao impressao, PrintService impressora, SimplePrintServiceExporterConfiguration configuration) throws JRException, ParserConfigurationException, IOException, SAXException, PrinterException, FiscalException {
        JasperPrint jasperPrint = geraImpressao(impressao);
        if (impressora == null) {
            throw new FiscalException("Impressora não encontrada");
        }

        if (configuration == null) {
            configuration = new SimplePrintServiceExporterConfiguration();
            configuration.setPrintRequestAttributeSet(new HashPrintRequestAttributeSet());
            configuration.setDisplayPageDialog(false);
            configuration.setDisplayPrintDialog(false);
        }

        PrinterJob printerJob = PrinterJob.getPrinterJob();
        printerJob.setPrintService(impressora);

        JRPrintServiceExporter exporter = new JRPrintServiceExporter();
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));
        configuration.setPrintService(impressora);
        exporter.setConfiguration(configuration);

        exporter.exportReport();
    }

    public static JasperPrint geraImpressao(Impressao impressao) throws IOException, SAXException, ParserConfigurationException, JRException {
        DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = docBuilder.parse(new InputSource(new StringReader(impressao.getXml())));
        JRDataSource xmlDataSource = new JRXmlDataSource(document, impressao.getPathExpression());
        return JasperFillManager.fillReport(impressao.getJasper(), impressao.getParametros(), xmlDataSource);
    }
}
