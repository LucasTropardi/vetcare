package com.lucast.vetcare.fiscal.util;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.TipoServicoEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.xml.NFe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class FiscalUtils {

    private static final Logger logger = LoggerFactory.getLogger(FiscalUtils.class);

    // NFe
    private static final Map<String, Map<TipoServicoEnum, WsUrls>> URLS_NFE_POR_TIPO_EMISSAO;
    private static final Map<String, Map<TipoServicoEnum, WsUrls>> URLS_NFE_POR_UF;
    private static final Set<String> UFS_SVRS;
    // NFCe
    private static final Map<String, Map<TipoServicoEnum, WsUrls>> URLS_NFCE_POR_UF;
    private static final Set<String> UFS_NFCE_SVRS;
    // Bloco de inicialização estático para carregar todas as URLs
    static {
        // URLs NFe
        URLS_NFE_POR_TIPO_EMISSAO = inicializarUrlsNfePorTipoEmissao();
        URLS_NFE_POR_UF = inicializarUrlsNfePorUf();
        // URLs NFCe
        URLS_NFCE_POR_UF = inicializarUrlsNfcePorUf();

        UFS_SVRS = inicializarUfsSvrs();
        UFS_NFCE_SVRS = inicializarUfsNfceSvrs();
        logger.info("Finalizando bloco de inicialização estático para carregar todas as URLs.");
    }

    private FiscalUtils() {
        throw new AssertionError("This class cannot be instantiated.");
    }

    public static String removeXMLTag(String conteudoXml) {
        if (conteudoXml == null) {
            return null;
        }

        String conteudo = conteudoXml.trim();

        if (conteudo.startsWith("<?xml")) {
            int endIndex = conteudo.indexOf("?>");
            if (endIndex != -1) {
                conteudo = conteudo.substring(endIndex + 2).trim();
            }
        }

        return conteudo;
    }

    public static String getUrlNFe(TipoServicoEnum tipoServico, String uf, String tipoAmbiente, String tipoEmissao) {
        Map<TipoServicoEnum, WsUrls> urlsPorServico = URLS_NFE_POR_TIPO_EMISSAO.get(tipoEmissao);
        if (urlsPorServico != null) {
            WsUrls urls = urlsPorServico.get(tipoServico);
            if (urls != null) {
                return urls.get(tipoAmbiente);
            }
        }

        String ufKey = uf;
        if ("21".equals(uf) || "22".equals(uf)) {
            ufKey = "SVAN";
        }

        urlsPorServico = URLS_NFE_POR_UF.get(ufKey);
        if (urlsPorServico != null) {
            WsUrls urls = urlsPorServico.get(tipoServico);
            if (urls != null) {
                return urls.get(tipoAmbiente);
            }
        }

        if (UFS_SVRS.contains(uf)) {
            urlsPorServico = URLS_NFE_POR_UF.get("SVRS");
            if (urlsPorServico != null) {
                WsUrls urls = urlsPorServico.get(tipoServico);
                if (urls != null) {
                    return urls.get(tipoAmbiente);
                }
            }
        }

        return "";
    }

    public static String getUrlNFCe(TipoServicoEnum tipoServico, String uf, Integer ambiente) {
        String tipoAmbiente = String.valueOf(ambiente);
        Map<TipoServicoEnum, WsUrls> urlsPorServico = URLS_NFCE_POR_UF.get(uf);
        if (urlsPorServico != null) {
            WsUrls urls = urlsPorServico.get(tipoServico);
            if (urls != null) {
                return urls.get(tipoAmbiente);
            }
        }

        if (UFS_NFCE_SVRS.contains(uf)) {
            urlsPorServico = URLS_NFCE_POR_UF.get("SVRS");
            if (urlsPorServico != null) {
                WsUrls urls = urlsPorServico.get(tipoServico);
                if (urls != null) {
                    return urls.get(tipoAmbiente);
                }
            }
        }

        return "";
    }

    public static String pegaTag(String xml, String tag) {
        if (xml == null || tag == null || xml.isBlank() || tag.isBlank()) {
            return "";
        }

        String retonro = "";
        String tagInicio = "<" + tag;
        String tagFim = "</" + tag + ">";

        int startIdx = xml.indexOf(tagInicio);
        if (startIdx != -1) {
            startIdx += tagInicio.length() + 1;
            int endIdx = xml.indexOf(tagFim, startIdx);
            if (endIdx != -1) {
                retonro = xml.substring(startIdx, endIdx);
            }
        }
        return retonro;
    }

    public static String pegaTag2(String xml, String tag) {
        if (xml == null || tag == null || xml.isBlank() || tag.isBlank()) {
            return "";
        }

        String retorno = "";
        String tagInicio = "<" + tag;
        String tagFim = "</" + tag + ">";

        int startIdx = xml.indexOf(tagInicio);
        if (startIdx != -1) {
            int endIdx = xml.indexOf(tagFim, startIdx);
            if (endIdx != -1) {
                endIdx += tagFim.length();
                retorno = xml.substring(startIdx, endIdx);
            }
        }
        return retorno;
    }

    public static String pegaTag3(String xml, String tag) {
        if (xml == null || tag == null || xml.isBlank() || tag.isBlank()) {
            return "";
        }

        String retonro = "";
        String tagInicio = "<" + tag;
        String tagFim = "</" + tag + ">";

        int startIdx = xml.indexOf(tagInicio);
        if (startIdx != -1) {
            startIdx += tagInicio.length() + 1;
            int endIdx = xml.indexOf(tagFim, startIdx);
            if (endIdx != -1) {
                retonro = xml.substring(startIdx, endIdx);
            }
        }
        return retonro;
    }

    public static String consulta(String url, String xml, Certificado certificado, String soapAction) {
        logger.info("Iniciando consulta SEFAZ para URL: {}", url);

        HttpsURLConnection conn = null;

        try {
            SSLContext sslContext = criarSSLContext(certificado);
            conn = abrirConexao(url, sslContext, soapAction, xml.length());

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(xml);
            writer.flush();

            String xmlResp = lerResposta(conn.getInputStream());
            logger.info("Resposta bruta SEFAZ: {}", xmlResp);

            if (conn.getResponseCode() == 200) {
                xmlResp = removeCabecalhoXML(xmlResp);
                return xmlResp;
            } else {
                String motivo = montaMotivo(conn);
                throw new FiscalException("Erro na consulta", conn.getResponseCode() + ";" + motivo);
            }

        } catch (FiscalException e) {
            logger.error("Resposta de SEFAZ com status diferente de 200: {}", e.getMessage());
            return montaRetConsStatServFiscalException(xml, e.getMessage());
        } catch (Exception e) {
            String erro = lerErro(conn, e);
            logger.error("Exceção ao consultar SEFAZ: {}", erro, e);
            return montaRetConsStatServ(xml, "500", erro);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public static String strZero(int value, int length) {
        return String.format("%0" + length + "d", value);
    }

    public static Integer ufToCodUf(String uf) {
        return EstadoBrasil.ufToCodUf(uf);
    }

    private static SSLContext criarSSLContext(Certificado certificado) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(certificado.getArquivo())) {
            keyStore.load(fis, certificado.getSenha().toCharArray());
        }

        KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (InputStream fis = FiscalUtils.class.getClassLoader().getResourceAsStream("cacert")) {
            trustStore.load(fis, "changeit".toCharArray());
        }

        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(keyStore, certificado.getSenha().toCharArray());

        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(trustStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
        return sslContext;
    }

    private static HttpsURLConnection abrirConexao(String url, SSLContext sslContext, String soapAction, int contentLength) throws IOException {
        URL urlObj = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) urlObj.openConnection();
        conn.setSSLSocketFactory(sslContext.getSocketFactory());
        conn.setHostnameVerifier((hostname, session) -> true);
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        conn.setRequestProperty("SOAPAction", "\"" + soapAction + "\"");
        conn.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(contentLength));
        return conn;
    }

    private static String montaMotivo(HttpsURLConnection conn) throws IOException {
        int status = conn.getResponseCode();

        InputStream inputStream = (status >= 400)
                ? conn.getErrorStream()
                : conn.getInputStream();

        if (inputStream == null) {
            logger.warn("Nenhum conteúdo retornado pela conexão. Código HTTP: " + status);
            return "";
        }

        StringBuilder response = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }

        if (status >= 400) {
            logger.warn("Erro HTTP " + status + ": " + response.toString());
        } else {
            logger.info("Status: " + status);
            logger.info("Resposta: " + response);
        }

        return response.toString();
    }

    private static String montaRetConsStatServFiscalException(String xml, String mensagemErro) {
        String cStat = "500";
        String xMotivo = "Erro desconhecido";

        if (mensagemErro != null && mensagemErro.contains(";")) {
            String[] partes = mensagemErro.split(";", 2);
            if (partes.length == 2) {
                cStat = partes[0].trim();
                xMotivo = partes[1].trim();
            }
        }

        logger.warn("FiscalException. Código: {}, Motivo: {}", cStat, xMotivo);

        return montaRetConsStatServ(xml, cStat, xMotivo);
    }

    private static String montaRetConsStatServ(String xml, String cStat, String xMotivo) {
        String respostaConsulta = pegaTag3(xml, "consStatServ");

        String tpAmb = getTagValueOrEmpty(respostaConsulta, "tpAmb");
        String verAplic = getTagValueOrEmpty(respostaConsulta, "verAplic");
        String cUF = getTagValueOrEmpty(respostaConsulta, "cUF");
        String tMed = getTagValueOrEmpty(respostaConsulta, "tMed");

        StringBuilder sb = new StringBuilder();
        sb.append("<retConsStatServ>\n")
                .append("    <tpAmb>").append(tpAmb).append("</tpAmb>\n")
                .append("    <verAplic>").append(verAplic).append("</verAplic>\n")
                .append("    <cStat>").append(cStat).append("</cStat>\n")
                .append("    <xMotivo>").append(escapeXml(xMotivo)).append("</xMotivo>\n")
                .append("    <cUF>").append(cUF).append("</cUF>\n")
                .append("    <tMed>").append(tMed).append("</tMed>\n")
                .append("</retConsStatServ>");

        return sb.toString();
    }

    private static String getTagValueOrEmpty(String xml, String tagName) {
        String value = pegaTag3(xml, tagName);
        return (value != null) ? value : "";
    }

    private static String escapeXml(String input) {
        if (input == null) return "";
        return input
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }

    private static String lerResposta(InputStream inputStream) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        }
    }

    private static String lerErro(HttpsURLConnection conn, Exception e) {
        if (conn != null) {
            try {
                InputStream errorStream = conn.getErrorStream();

                if (errorStream != null) {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(errorStream))) {
                        StringBuilder sb = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                        }
                        if (!sb.isEmpty()) {
                            return sb.toString();
                        }
                    }
                } else {
                    int responseCode = 500;
                    String responseMessage = e.getMessage();

                    try {
                        responseCode = conn.getResponseCode();
                        responseMessage = conn.getResponseMessage();
                    } catch (IOException ex) {
                        logger.warn("Impossível obter conn.getResponseCode() e conn.getResponseMessage()");
                    }

                    logger.warn("ErrorStream null. HTTP {}: {}", responseCode, responseMessage);
                    return String.format("Erro HTTP %d: %s", responseCode, responseMessage);
                }
            } catch (IOException ioEx) {
                logger.warn("Erro ao ler a resposta de erro da SEFAZ: {}", ioEx.getMessage(), ioEx);
            }
        }

        return e != null ? e.getMessage() : "Erro desconhecido na comunicação com SEFAZ";
    }

    private static String removeCabecalhoXML(String xml) {
        int index = xml.indexOf("<?xml");
        return index != -1 ? xml.substring(index) : xml;
    }

    private static Map<String, Map<TipoServicoEnum, WsUrls>> inicializarUrlsNfePorTipoEmissao() {
        // URLs por Tipo de Emissão
        return Map.ofEntries(
                Map.entry("3", urlsNfePorTipoEmissaoScan()),
                Map.entry("5", urlsNfePorTipoEmissaoScan()),
                Map.entry("6", Map.ofEntries(
                        Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://www.svc.fazenda.gov.br/NFeStatusServico4/NFeStatusServico4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeStatusServico4")),
                        Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://www.svc.fazenda.gov.br/NFeConsultaProtocolo4/NFeConsultaProtocolo4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeConsultaProtocolo4")),
                        Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://www.svc.fazenda.gov.br/NFeRecepcaoEvento4/NFeRecepcaoEvento4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4")),
                        Map.entry(TipoServicoEnum.NFE_AUTORIZACAO, new WsUrls("https://www.svc.fazenda.gov.br/NFeAutorizacao4/NFeAutorizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeAutorizacao4")),
                        Map.entry(TipoServicoEnum.NFE_RET_AUTORIZACAO, new WsUrls("https://www.svc.fazenda.gov.br/NFeRetAutorizacao4/NFeRetAutorizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4")),
                        Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://www.svc.fazenda.gov.br/NFeRetAutorizacao4/NFeRetAutorizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4")),
                        Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://www.svc.fazenda.gov.br/NFeInutilizacao4/NFeInutilizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeInutilizacao4")),
                        Map.entry(TipoServicoEnum.CONSULTA_CADASTRO, new WsUrls("https://cad.svrs.rs.gov.br/ws/cadconsultacadastro/cadconsultacadastro4.asmx", "https://homologacao.nfe.ms.gov.br/homologacao/services2/CadConsultaCadastro2")),
                        Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://www.svc.fazenda.gov.br/NFeRecepcaoEvento4/NFeRecepcaoEvento4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4")),
                        Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://www.svc.fazenda.gov.br/NFeRecepcaoEvento4/NFeRecepcaoEvento4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4"))
                )),
                Map.entry("7", Map.ofEntries(
                        Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeStatusServico4")),
                        Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeConsultaProtocolo4")),
                        Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4")),
                        Map.entry(TipoServicoEnum.NFE_AUTORIZACAO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeAutorizacao4")),
                        Map.entry(TipoServicoEnum.NFE_RET_AUTORIZACAO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4")),
                        Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4")),
                        Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeInutilizacao4")),
                        Map.entry(TipoServicoEnum.CONSULTA_CADASTRO, new WsUrls("https://cad.svrs.rs.gov.br/ws/cadconsultacadastro/cadconsultacadastro4.asmx", "https://homologacao.nfe.ms.gov.br/homologacao/services2/CadConsultaCadastro2")),
                        Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4")),
                        Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4"))
                ))
        );
    }

    private static Map<TipoServicoEnum, WsUrls> urlsNfePorTipoEmissaoScan() {
        return Map.ofEntries(
                Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://www.scan.fazenda.gov.br/NFeStatusServico2/NFeStatusServico2.asmx", "https://hom.nfe.fazenda.gov.br/SCAN/NfeStatusServico2/NfeStatusServico2.asmx")),
                Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://www.scan.fazenda.gov.br/NfeConsulta2/NfeConsulta2.asmx", "https://hom.nfe.fazenda.gov.br/SCAN/NfeConsulta2/NfeConsulta2.asmx")),
                Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://www.scan.fazenda.gov.br/NfeRecepcao2/NfeRecepcao2.asmx", "https://hom.nfe.fazenda.gov.br/SCAN/NfeRecepcao2/NfeRecepcao2.asmx")),
                Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://www.scan.fazenda.gov.br/NfeRetRecepcao2/NfeRetRecepcao2.asmx", "https://hom.nfe.fazenda.gov.br/SCAN/NfeRetRecepcao2/NfeRetRecepcao2.asmx")),
                Map.entry(TipoServicoEnum.CANCELAMENTO, new WsUrls("https://www.scan.fazenda.gov.br/NfeCancelamento2/NfeCancelamento2.asmx", "https://hom.nfe.fazenda.gov.br/SCAN/NfeCancelamento2/NfeCancelamento2.asmx")),
                Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://www.scan.fazenda.gov.br/NfeInutilizacao2/NfeInutilizacao2.asmx", "https://hom.nfe.fazenda.gov.br/SCAN/NfeInutilizacao2/NfeInutilizacao2.asmx")),
                Map.entry(TipoServicoEnum.CONSULTA_NFE_DEST, new WsUrls("https://www.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx", "https://hom.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx")),
                Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://www.scan.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx", "https://hom.nfe.fazenda.gov.br/SCAM/RecepcaoEvento/RecepcaoEvento.asmx")),
                Map.entry(TipoServicoEnum.DOWNLOAD_NFE, new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx")),
                Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://www.nfe.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx", "https://hom.nfe.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx"))
        );
    }

    private static Map<String, Map<TipoServicoEnum, WsUrls>> inicializarUrlsNfePorUf() {
        return Map.ofEntries(
                // Amazonas
                Map.entry(EstadoBrasil.AMAZONAS.getCodigo().toString(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeStatusServico4", "https://homnfe.sefaz.am.gov.br/services2/services/NfeStatusServico4")),
                        Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeConsulta4", "https://homnfe.sefaz.am.gov.br/services2/services/NfeConsulta4")),
                        Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeRecepcao4", "https://homnfe.sefaz.am.gov.br/services2/services/NfeRecepcao4")),
                        Map.entry(TipoServicoEnum.NFE_AUTORIZACAO, new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeAutorizacao4", "https://homnfe.sefaz.am.gov.br/services2/services/NfeAutorizacao4")),
                        Map.entry(TipoServicoEnum.NFE_RET_AUTORIZACAO, new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeRetAutorizacao4", "https://homnfe.sefaz.am.gov.br/services2/services/NfeRetAutorizacao4")),
                        Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeRetAutorizacao4", "https://homnfe.sefaz.am.gov.br/services2/services/NfeRetAutorizacao4")),
                        Map.entry(TipoServicoEnum.CANCELAMENTO, new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeCancelamento4", "https://homnfe.sefaz.am.gov.br/services2/services/NfeCancelamento4")),
                        Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeInutilizacao4", "https://homnfe.sefaz.am.gov.br/services2/services/NfeInutilizacao4")),
                        Map.entry(TipoServicoEnum.CONSULTA_CADASTRO, new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/CadConsultaCadastro4", "https://homnfe.sefaz.am.gov.br/services2/services/CadConsultaCadastro4")),
                        Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/RecepcaoEvento4", "https://homnfe.sefaz.am.gov.br/services2/services/RecepcaoEvento4")),
                        Map.entry(TipoServicoEnum.CONSULTA_NFE_DEST, new WsUrls("https://www.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx", "https://hom.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx")),
                        Map.entry(TipoServicoEnum.DOWNLOAD_NFE, new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx")),
                        Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/RecepcaoEvento4", "https://homnfe.sefaz.am.gov.br/services2/services/RecepcaoEvento4"))
                )),
                // Bahia
                Map.entry(EstadoBrasil.BAHIA.getCodigo().toString(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/nfenw/NfeStatusServico2.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/NFeStatusServico4/NFeStatusServico4.asmx")),
                        Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/nfenw/NfeConsulta2.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/NFeConsultaProtocolo4/NFeConsultaProtocolo4.asmx")),
                        Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/nfenw/NfeRecepcao2.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/nfenw/NfeRecepcao2.asmx")),
                        Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/nfenw/NfeRetRecepcao2.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/nfenw/NfeRetRecepcao2.asmx")),
                        Map.entry(TipoServicoEnum.CANCELAMENTO, new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/nfenw/NfeCancelamento2.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/nfenw/NfeCancelamento2.asmx")),
                        Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/nfenw/NfeInutilizacao2.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/NFeInutilizacao4/NFeInutilizacao4.asmx")),
                        Map.entry(TipoServicoEnum.CONSULTA_CADASTRO, new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/CadConsultaCadastro4/CadConsultaCadastro4.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/CadConsultaCadastro4/CadConsultaCadastro4.asmx")),
                        Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/sre/RecepcaoEvento.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/sre/RecepcaoEvento.asmx")),
                        Map.entry(TipoServicoEnum.CONSULTA_NFE_DEST, new WsUrls("https://www.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx", "https://hom.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx")),
                        Map.entry(TipoServicoEnum.DOWNLOAD_NFE, new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx")),
                        Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://www.nfe.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/NFeRecepcaoEvento4/NFeRecepcaoEvento4.asmx"))
                )),
                // Mato Grosso do Sul
                Map.entry(EstadoBrasil.MATO_GROSSO_DO_SUL.getCodigo().toString(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeStatusServico4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeStatusServico4")),
                        Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeConsultaProtocolo4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeConsultaProtocolo4")),
                        Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4")),
                        Map.entry(TipoServicoEnum.NFE_AUTORIZACAO, new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeAutorizacao4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeAutorizacao4")),
                        Map.entry(TipoServicoEnum.NFE_RET_AUTORIZACAO, new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4")),
                        Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4")),
                        Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeInutilizacao4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeInutilizacao4")),
                        Map.entry(TipoServicoEnum.CONSULTA_CADASTRO, new WsUrls("https://nfe.sefaz.ms.gov.br/ws/CadConsultaCadastro4", "https://hom.nfe.sefaz.ms.gov.br/ws/CadConsultaCadastro4")),
                        Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4")),
                        Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4"))
                )),
                // Mato Grosso
                Map.entry(EstadoBrasil.MATO_GROSSO.getCodigo().toString(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/NfeStatusServico4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/NfeStatusServico4?wsdl")),
                        Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/NfeConsulta4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/NfeConsulta4?wsdl")),
                        Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/RecepcaoEvento4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/RecepcaoEvento4?wsdl")),
                        Map.entry(TipoServicoEnum.NFE_AUTORIZACAO, new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/NfeAutorizacao4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/NfeAutorizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.NFE_RET_AUTORIZACAO, new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/NfeRetAutorizacao4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/NfeRetAutorizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/NfeRetAutorizacao4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/NfeRetAutorizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/NfeInutilizacao4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/NfeInutilizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.CONSULTA_CADASTRO, new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/CadConsultaCadastro4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/CadConsultaCadastro4?wsdl")),
                        Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/RecepcaoEvento4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/RecepcaoEvento4?wsdl")),
                        Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/RecepcaoEvento4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/RecepcaoEvento4?wsdl"))
                )),
                // Goiás
                Map.entry(EstadoBrasil.GOIAS.getCodigo().toString(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/v2/NfeStatusServico2?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/v2/NfeStatusServico2?wsd")),
                        Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/v2/NfeConsulta2?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeConsultaProtocolo4?wsdl")),
                        Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/v2/NfeRecepcao2?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/v2/NfeRecepcao2?wsd")),
                        Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/v2/NfeRetRecepcao2?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/v2/NfeRetRecepcao2?wsdl")),
                        Map.entry(TipoServicoEnum.CANCELAMENTO, new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/v2/NfeCancelamento2?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/v2/NfeCancelamento2?wsdl")),
                        Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/v2/NfeInutilizacao2?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeInutilizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.CONSULTA_CADASTRO, new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/CadConsultaCadastro4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/v2/CadConsultaCadastro2?wsdl")),
                        Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/v2/NfeRecepcaoEvento?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/v2/NfeRecepcaoEvento?wsdl")),
                        Map.entry(TipoServicoEnum.CONSULTA_NFE_DEST, new WsUrls("https://www.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx", "https://hom.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx")),
                        Map.entry(TipoServicoEnum.DOWNLOAD_NFE, new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx")),
                        Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://www.nfe.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx", "https://hom.nfe.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx"))
                )),
                // Pernambuco
                Map.entry(EstadoBrasil.PERNAMBUCO.getCodigo().toString(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeStatusServico4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeStatusServico4?wsdl")),
                        Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeConsultaProtocolo4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeConsultaProtocolo4?wsdl")),
                        Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeRecepcaoEvento4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeRecepcaoEvento4?wsd2")),
                        Map.entry(TipoServicoEnum.NFE_AUTORIZACAO, new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeAutorizacao4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeAutorizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.NFE_RET_AUTORIZACAO, new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeRetAutorizacao4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeRetAutorizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeRetAutorizacao4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeRetAutorizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeInutilizacao4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeInutilizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.CONSULTA_CADASTRO, new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/CadConsultaCadastro4?wsdl", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/CadConsultaCadastro4?wsdl")),
                        Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeRecepcaoEvento4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/RecepcaoEvento")),
                        Map.entry(TipoServicoEnum.CONSULTA_NFE_DEST, new WsUrls("https://www.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx", "https://hom.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx")),
                        Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeRecepcaoEvento4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeRecepcaoEvento4?wsdl"))
                )),
                // São Paulo
                Map.entry(EstadoBrasil.SAO_PAULO.getCodigo().toString(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nfestatusservico4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nfestatusservico4.asmx")),
                        Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nfeconsultaprotocolo4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nfeconsultaprotocolo4.asmx")),
                        Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nferecepcaoevento4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nfeautorizacao.asmx")),
                        Map.entry(TipoServicoEnum.NFE_AUTORIZACAO, new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nfeautorizacao4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nfeautorizacao4.asmx")),
                        Map.entry(TipoServicoEnum.NFE_RET_AUTORIZACAO, new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nferetautorizacao4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nferetautorizacao4.asmx")),
                        Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nferetautorizacao4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nferetautorizacao.asmx")),
                        Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nfeinutilizacao4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nfeinutilizacao4.asmx")),
                        Map.entry(TipoServicoEnum.CONSULTA_CADASTRO, new WsUrls("https://nfe.fazenda.sp.gov.br/ws/cadconsultacadastro4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/cadconsultacadastro4.asmx")),
                        Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nferecepcaoevento4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nferecepcaoevento4.asmx")),
                        Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nferecepcaoevento4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nferecepcaoevento4.asmx"))
                )),
                // Rio Grande do Sul
                Map.entry(EstadoBrasil.RIO_GRANDE_DO_SUL.getCodigo().toString(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx")),
                        Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx")),
                        Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx")),
                        Map.entry(TipoServicoEnum.NFE_AUTORIZACAO, new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx")),
                        Map.entry(TipoServicoEnum.NFE_RET_AUTORIZACAO, new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx")),
                        Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx")),
                        Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx")),
                        Map.entry(TipoServicoEnum.CONSULTA_CADASTRO, new WsUrls("https://cad.sefazrs.rs.gov.br/ws/cadconsultacadastro/cadconsultacadastro4.asmx", "https://cad.sefazrs.rs.gov.br/ws/cadconsultacadastro/cadconsultacadastro4.asmx")),
                        Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx")),
                        Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx"))
                )),
                // Paraná
                Map.entry(EstadoBrasil.PARANA.getCodigo().toString(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeStatusServico4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeStatusServico4?wsdl")),
                        Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeConsultaProtocolo4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeConsultaProtocolo4?wsdl")),
                        Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeRecepcaoEvento4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeRecepcaoEvento4?wsdl")),
                        Map.entry(TipoServicoEnum.NFE_AUTORIZACAO, new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeAutorizacao4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeAutorizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.NFE_RET_AUTORIZACAO, new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeRetAutorizacao4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeRetAutorizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeRecepcaoEvento4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeRetAutorizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeInutilizacao4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeInutilizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.CONSULTA_CADASTRO, new WsUrls("https://nfe.sefa.pr.gov.br/nfe/CadConsultaCadastro4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/CadConsultaCadastro4?wsdl")),
                        Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeRecepcaoEvento4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeRecepcaoEvento4?wsdl")),
                        Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeRecepcaoEvento4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeRecepcaoEvento4?wsdl"))
                )),
                // Minas Gerais
                Map.entry(EstadoBrasil.MINAS_GERAIS.getCodigo().toString(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeStatusServico4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NFeStatusServico4")),
                        Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeConsultaProtocolo4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NFeConsultaProtocolo4")),
                        Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NfeRecepcao2", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NfeRecepcao2x")),
                        Map.entry(TipoServicoEnum.NFE_AUTORIZACAO, new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeAutorizacao4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NFeAutorizacao4")),
                        Map.entry(TipoServicoEnum.NFE_RET_AUTORIZACAO, new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeRetAutorizacao4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NFeRetAutorizacao4")),
                        Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeRecepcaoEvento4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NfeRetRecepcao2")),
                        Map.entry(TipoServicoEnum.CANCELAMENTO, new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NfeCancelamento2", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NfeCancelamento2")),
                        Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeInutilizacao4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NFeInutilizacao4")),
                        Map.entry(TipoServicoEnum.CONSULTA_CADASTRO, new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/CadConsultaCadastro4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/CadConsultaCadastro4")),
                        Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeRecepcaoEvento4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NFeRecepcaoEvento4")),
                        Map.entry(TipoServicoEnum.CONSULTA_NFE_DEST, new WsUrls("https://www.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx", "https://hom.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx")),
                        Map.entry(TipoServicoEnum.DOWNLOAD_NFE, new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx")),
                        Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeRecepcaoEvento4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NFeRecepcaoEvento4"))
                )),
                Map.entry("SVAN", Map.ofEntries(
                        Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeStatusServico2/NfeStatusServico2.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NFeStatusServico4/NFeStatusServico4.asmx")),
                        Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeConsulta2/NfeConsulta2.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NFeConsultaProtocolo4/NFeConsultaProtocolo4.asmx")),
                        Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeRecepcao2/NfeRecepcao2.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeRecepcao2/NfeRecepcao2.asmx")),
                        Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeRetRecepcao2/NfeRetRecepcao2.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeRetRecepcao2/NfeRetRecepcao2.asmx")),
                        Map.entry(TipoServicoEnum.CANCELAMENTO, new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeCancelamento2/NfeCancelamento2.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeCancelamento2/NfeCancelamento2.asmx")),
                        Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeInutilizacao2/NfeInutilizacao2.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NFeInutilizacao4/NFeInutilizacao4.asmx")),
                        Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://www.sefazvirtual.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx", "https://hom.sefazvirtual.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx")),
                        Map.entry(TipoServicoEnum.CONSULTA_NFE_DEST, new WsUrls("https://www.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx", "https://hom.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx")),
                        Map.entry(TipoServicoEnum.DOWNLOAD_NFE, new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx")),
                        Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://www.nfe.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NFeRecepcaoEvento4/NFeRecepcaoEvento4.asmx"))
                )),
                Map.entry("SVRS", Map.ofEntries(
                        Map.entry(TipoServicoEnum.STATUS_SERVICO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx")),
                        Map.entry(TipoServicoEnum.CONSULTA_PROTOCOLO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx")),
                        Map.entry(TipoServicoEnum.RECEPCAO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx")),
                        Map.entry(TipoServicoEnum.NFE_AUTORIZACAO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx")),
                        Map.entry(TipoServicoEnum.NFE_RET_AUTORIZACAO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx")),
                        Map.entry(TipoServicoEnum.RET_RECEPCAO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx")),
                        Map.entry(TipoServicoEnum.INUTILIZACAO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx")),
                        Map.entry(TipoServicoEnum.CONSULTA_CADASTRO, new WsUrls("https://cad.svrs.rs.gov.br/ws/cadconsultacadastro/cadconsultacadastro4.asmx", "https://cad-homologacao.svrs.rs.gov.br/ws/cadconsultacadastro/cadconsultacadastro4.asmx")),
                        Map.entry(TipoServicoEnum.EVENTO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx")),
                        Map.entry(TipoServicoEnum.REC_EVENTO, new WsUrls("https://nfe.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx"))
                ))
        );
    }

    private static Map<String, Map<TipoServicoEnum, WsUrls>> inicializarUrlsNfcePorUf() {
        // URLs NFCe
        return Map.ofEntries(
                Map.entry("SVRS", Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_RECEPCAO_EVENTO, new WsUrls("https://nfce.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfce-homologacao.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_INUTILIZACAO, new WsUrls("https://nfce.svrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx", "https://nfce-homologacao.svrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_PROTOCOLO, new WsUrls("https://nfce.svrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx", "https://nfce-homologacao.svrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_STATUS_SERVICO, new WsUrls("https://nfce.svrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx", "https://nfce-homologacao.svrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_AUTORIZACAO, new WsUrls("https://nfce.svrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx", "https://nfce-homologacao.svrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_RET_AUTORIZACAO, new WsUrls("https://nfce.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx", "https://nfce-homologacao.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx"))
                )),
                Map.entry(EstadoBrasil.ACRE.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://www.sefaznet.ac.gov.br/nfce/qrcode", "http://www.hml.sefaznet.ac.gov.br/nfce/qrcode")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.sefaznet.ac.gov.br/nfce/consulta", "www.sefaznet.ac.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.ALAGOAS.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://nfce.sefaz.al.gov.br/QRCode/consultarNFCe.jsp", "http://nfce.sefaz.al.gov.br/QRCode/consultarNFCe.jsp")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.sefaz.al.gov.br/nfce/consulta", "www.sefaz.al.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.AMAPA.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("https://www.sefaz.ap.gov.br/nfce/nfcep.php", "https://www.sefaz.ap.gov.br/nfcehml/nfce.php")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.sefaz.ap.gov.br/nfce/consulta", "www.sefaz.ap.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.BAHIA.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://nfe.sefaz.ba.gov.br/servicos/nfce/modulos/geral/NFCEC_consulta_chave_acesso.aspx", "http://hnfe.sefaz.ba.gov.br/servicos/nfce/modulos/geral/NFCEC_consulta_chave_acesso.aspx")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.sefaz.ba.gov.br/nfce/consulta", "http://hinternet.sefaz.ba.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.DISTRITO_FEDERAL.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("https://www.fazenda.df.gov.br/nfce/qrcode", "https://www.fazenda.df.gov.br/nfce/qrcode")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.fazenda.df.gov.br/nfce/consulta", "www.fazenda.df.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.ESPIRITO_SANTO.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://app.sefaz.es.gov.br/ConsultaNFCe/qrcode.aspx", "http://homologacao.sefaz.es.gov.br/ConsultaNFCe/qrcode.aspx")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.sefaz.es.gov.br/nfce/consulta", "www.sefaz.es.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.MARANHAO.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://www.nfce.sefaz.ma.gov.br/portal/consultarNFCe.jsp", "http://www.hom.nfce.sefaz.ma.gov.br/portal/consultarNFCe.jsp")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.sefaz.ma.gov.br/nfce/consulta", "www.sefaz.ma.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.PARA.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("https://appnfc.sefa.pa.gov.br/portal/view/consultas/nfce/nfceForm.seam", "https://appnfc.sefa.pa.gov.br/portal-homologacao/view/consultas/nfce/nfceForm.seam")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("wwww.sefa.pa.gov.br/nfce/consulta", "www.sefa.pa.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.PARAIBA.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://www.sefaz.pb.gov.br/nfce", "http://www.sefaz.pb.gov.br/nfcehom")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.sefaz.pb.gov.br/nfce/consulta", "www.sefaz.pb.gov.br/nfcehom"))
                )),
                Map.entry(EstadoBrasil.PERNAMBUCO.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://nfce.sefaz.pe.gov.br/nfce-web/consultarNFCe", "http://nfcehomolog.sefaz.pe.gov.br/nfce-web/consultarNFCe")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("nfce.sefaz.pe.gov.br/nfce/consulta", "nfce.sefaz.pe.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.PIAUI.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://www.sefaz.pi.gov.br/nfce/qrcode", "http://www.sefaz.pi.gov.br/nfce/qrcode")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.sefaz.pi.gov.br/nfce/consulta", "www.sefaz.pi.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.RIO_DE_JANEIRO.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("https://consultadfe.fazenda.rj.gov.br/consultaNFCe/QRCode", "https://consultadfe.fazenda.rj.gov.br/consultaNFCe/QRCode")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.fazenda.rj.gov.br/nfce/consulta", "www.fazenda.rj.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.RIO_GRANDE_DO_NORTE.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://nfce.set.rn.gov.br/consultarNFCe.aspx", "http://hom.nfce.set.rn.gov.br/consultarNFCe.aspx")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.set.rn.gov.br/nfce/consulta", "www.set.rn.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.RONDONIA.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://www.nfce.sefin.ro.gov.br/consultanfce/consulta.jsp", "http://www.nfce.sefin.ro.gov.br/consultanfce/consulta.jsp")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("=www.sefin.ro.gov.br/nfce/consulta", "=www.sefin.ro.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.RORAIMA.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("https://www.sefaz.rr.gov.br/nfce/servlet/qrcode", "http://200.174.88.103:8080/nfce/servlet/qrcode")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.sefaz.rr.gov.br/nfce/consulta", "www.sefaz.rr.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.SANTA_CATARINA.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("https://sat.sef.sc.gov.br/nfce/consulta", "https://hom.sat.sef.sc.gov.br/nfce/consulta")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("https://sat.sef.sc.gov.br/nfce/consulta", "https://hom.sat.sef.sc.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.SERGIPE.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://www.nfce.se.gov.br/portal/consultarNFCe.jsp", "http://www.hom.nfe.se.gov.br/portal/consultarNFCe.jsp")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("http://www.nfce.se.gov.br/nfce/consulta", "http://www.hom.nfe.se.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.TOCANTINS.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://www.sefaz.to.gov.br/nfce/qrcode", "http://homologacao.sefaz.to.gov.br/nfce/qrcode")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.sefaz.to.gov.br/nfce/consulta", "http://homologacao.sefaz.to.gov.br/nfce/consulta.jsf"))
                )),
                Map.entry(EstadoBrasil.AMAZONAS.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_RECEPCAO_EVENTO, new WsUrls("https://nfce.sefaz.am.gov.br/nfce-services/services/RecepcaoEvento4", "https://homnfce.sefaz.am.gov.br/nfce-services-nac/services/RecepcaoEvento4")),
                        Map.entry(TipoServicoEnum.NFCE_INUTILIZACAO, new WsUrls("https://nfce.sefaz.am.gov.br/nfce-services/services/NfeInutilizacao4", "https://homnfce.sefaz.am.gov.br/nfce-services-nac/services/NfeInutilizacao4")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_PROTOCOLO, new WsUrls("https://nfce.sefaz.am.gov.br/nfce-services/services/NfeConsulta4", "https://homnfce.sefaz.am.gov.br/nfce-services-nac/services/NfeConsulta4")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_STATUS_SERVICO, new WsUrls("https://nfce.sefaz.am.gov.br/nfce-services/services/NfeStatusServico4", "https://homnfce.sefaz.am.gov.br/nfce-services-nac/services/NfeStatusServico4")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_AUTORIZACAO, new WsUrls("https://nfce.sefaz.am.gov.br/nfce-services/services/NfeAutorizacao4", "https://homnfce.sefaz.am.gov.br/nfce-services-nac/services/NfeAutorizacao4")),
                        Map.entry(TipoServicoEnum.NFCE_RET_AUTORIZACAO, new WsUrls("https://nfce.sefaz.am.gov.br/nfce-services/services/NfeRetAutorizacao4", "https://homnfce.sefaz.am.gov.br/nfce-services-nac/services/RecepcaoEvento4")),
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://sistemas.sefaz.am.gov.br/nfceweb/consultarNFCe.jsp", "https://sistemas.sefaz.am.gov.br/nfceweb-hom/consultarNFCe.jsp")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.sefaz.am.gov.br/nfce/consulta", "https://sistemas.sefaz.am.gov.br/nfceweb-hom/formConsulta.do"))
                )),
                Map.entry(EstadoBrasil.CEARA.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_RECEPCAO_EVENTO, new WsUrls("https://nfce.sefaz.ce.gov.br/nfce4/services/NFeRecepcaoEvento4?WSDL", "https://nfceh.sefaz.ce.gov.br/nfce4/services/NFeRecepcaoEvento4?WSDL")),
                        Map.entry(TipoServicoEnum.NFCE_INUTILIZACAO, new WsUrls("https://nfce.sefaz.ce.gov.br/nfce4/services/NFeInutilizacao4?WSDL", "https://nfceh.sefaz.ce.gov.br/nfce4/services/NFeInutilizacao4?WSDL")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_PROTOCOLO, new WsUrls("https://nfce.sefaz.ce.gov.br/nfce4/services/NFeConsultaProtocolo4?WSDL", "https://nfceh.sefaz.ce.gov.br/nfce4/services/NFeConsultaProtocolo4?WSDL")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_STATUS_SERVICO, new WsUrls("https://nfce.sefaz.ce.gov.br/nfce4/services/NFeStatusServico4?WSDL", "https://nfceh.sefaz.ce.gov.br/nfce4/services/NFeStatusServico4?WSDL")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_AUTORIZACAO, new WsUrls("https://nfce.sefaz.ce.gov.br/nfce4/services/NFeAutorizacao4?WSDL", "https://nfceh.sefaz.ce.gov.br/nfce4/services/NFeAutorizacao4?WSDL")),
                        Map.entry(TipoServicoEnum.NFCE_RET_AUTORIZACAO, new WsUrls("https://nfce.sefaz.ce.gov.br/nfce4/services/NFeRetAutorizacao4?WSDL", "https://nfceh.sefaz.ce.gov.br/nfce4/services/NFeRetAutorizacao4?WSDL")),
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://nfce.sefaz.ce.gov.br/pages/ShowNFCe.html", "http://nfceh.sefaz.ce.gov.br/pages/ShowNFCe.html")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.sefaz.ce.gov.br/nfce/consulta", "www.sefaz.ce.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.GOIAS.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_RECEPCAO_EVENTO, new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/NFeRecepcaoEvento4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeRecepcaoEvento4?wsdl")),
                        Map.entry(TipoServicoEnum.NFCE_INUTILIZACAO, new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/NFeInutilizacao4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeInutilizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_PROTOCOLO, new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/NFeConsultaProtocolo4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeConsultaProtocolo4?wsdl")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_STATUS_SERVICO, new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/NFeStatusServico4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeStatusServico4?wsdl")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_AUTORIZACAO, new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/NFeAutorizacao4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeAutorizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.NFCE_RET_AUTORIZACAO, new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/NFeRetAutorizacao4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeRetAutorizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://nfe.sefaz.go.gov.br/nfeweb/sites/nfce/danfeNFCe", "http://homolog.sefaz.go.gov.br/nfeweb/sites/nfce/danfeNFCe")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("http://www.sefaz.go.gov.br/nfce/consulta", "http://www.sefaz.go.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.MINAS_GERAIS.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_RECEPCAO_EVENTO, new WsUrls("https://nfce.fazenda.mg.gov.br/nfce/services/NFeRecepcaoEvento4", "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeRecepcaoEvento4")),
                        Map.entry(TipoServicoEnum.NFCE_INUTILIZACAO, new WsUrls("https://nfce.fazenda.mg.gov.br/nfce/services/NFeInutilizacao4", "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeInutilizacao4")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_PROTOCOLO, new WsUrls("https://nfce.fazenda.mg.gov.br/nfce/services/NFeConsultaProtocolo4", "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeConsultaProtocolo4")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_STATUS_SERVICO, new WsUrls("https://nfce.fazenda.mg.gov.br/nfce/services/NFeStatusServico4", "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeStatusServico4")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_AUTORIZACAO, new WsUrls("https://nfce.fazenda.mg.gov.br/nfce/services/NFeAutorizacao4", "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeAutorizacao4")),
                        Map.entry(TipoServicoEnum.NFCE_RET_AUTORIZACAO, new WsUrls("https://nfce.fazenda.mg.gov.br/nfce/services/NFeRetAutorizacao4", "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeRetAutorizacao4")),
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("https://portalsped.fazenda.mg.gov.br/portalnfce/sistema/qrcode.xhtml", "https://portalsped.fazenda.mg.gov.br/portalnfce/sistema/qrcode.xhtml")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("https://portalsped.fazenda.mg.gov.br/portalnfce", "https://hportalsped.fazenda.mg.gov.br/portalnfce"))
                )),
                Map.entry(EstadoBrasil.MATO_GROSSO_DO_SUL.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_RECEPCAO_EVENTO, new WsUrls("https://nfce.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4", "https://hom.nfce.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4")),
                        Map.entry(TipoServicoEnum.NFCE_INUTILIZACAO, new WsUrls("https://nfce.sefaz.ms.gov.br/ws/NFeInutilizacao4", "https://hom.nfce.sefaz.ms.gov.br/ws/NFeInutilizacao4")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_PROTOCOLO, new WsUrls("https://nfce.sefaz.ms.gov.br/ws/NFeConsultaProtocolo44", "https://hom.nfce.sefaz.ms.gov.br/ws/NFeConsultaProtocolo4")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_STATUS_SERVICO, new WsUrls("https://nfce.sefaz.ms.gov.br/ws/NFeStatusServico4", "https://hom.nfce.sefaz.ms.gov.br/ws/NFeStatusServico4")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_AUTORIZACAO, new WsUrls("https://nfce.sefaz.ms.gov.br/ws/NFeAutorizacao4", "https://hom.nfce.sefaz.ms.gov.br/ws/NFeAutorizacao4")),
                        Map.entry(TipoServicoEnum.NFCE_RET_AUTORIZACAO, new WsUrls("https://nfce.sefaz.ms.gov.br/ws/NFeRetAutorizacao4", "https://hom.nfce.sefaz.ms.gov.br/ws/NFeRetAutorizacao4")),
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://www.dfe.ms.gov.br/nfce/qrcode", "http://www.dfe.ms.gov.br/nfce/qrcode")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("http://www.dfe.ms.gov.br/nfce/consulta", "http://www.dfe.ms.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.MATO_GROSSO.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_RECEPCAO_EVENTO, new WsUrls("https://nfce.sefaz.mt.gov.br/nfcews/services/RecepcaoEvento4", "https://homologacao.sefaz.mt.gov.br/nfcews/services/RecepcaoEvento4")),
                        Map.entry(TipoServicoEnum.NFCE_INUTILIZACAO, new WsUrls("https://nfce.sefaz.mt.gov.br/nfcews/services/NfeInutilizacao4", "https://homologacao.sefaz.mt.gov.br/nfcews/services/NfeInutilizacao4")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_PROTOCOLO, new WsUrls("https://nfce.sefaz.mt.gov.br/nfcews/services/NfeConsulta4", "https://homologacao.sefaz.mt.gov.br/nfcews/services/NfeConsulta4")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_STATUS_SERVICO, new WsUrls("https://nfce.sefaz.mt.gov.br/nfcews/services/NfeStatusServico4", "https://homologacao.sefaz.mt.gov.br/nfcews/services/NfeStatusServico4")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_AUTORIZACAO, new WsUrls("https://nfce.sefaz.mt.gov.br/nfcews/services/NfeAutorizacao4", "https://homologacao.sefaz.mt.gov.br/nfcews/services/NfeAutorizacao4")),
                        Map.entry(TipoServicoEnum.NFCE_RET_AUTORIZACAO, new WsUrls("https://nfce.sefaz.mt.gov.br/nfcews/services/NfeRetAutorizacao4", "https://homologacao.sefaz.mt.gov.br/nfcews/services/NfeRetAutorizacao4")),
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://www.sefaz.mt.gov.br/nfce/consultanfce", "http://homologacao.sefaz.mt.gov.br/nfce/consultanfce")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("http://www.sefaz.mt.gov.br/nfce/consultanfce", "http://homologacao.sefaz.mt.gov.br/nfce/consultanfce"))
                )),
                Map.entry(EstadoBrasil.PARANA.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_RECEPCAO_EVENTO, new WsUrls("https://nfce.sefa.pr.gov.br/nfce/NFeRecepcaoEvento4?wsdl", "https://homologacao.nfce.sefa.pr.gov.br/nfce/NFeRecepcaoEvento4?wsdl")),
                        Map.entry(TipoServicoEnum.NFCE_INUTILIZACAO, new WsUrls("https://nfce.sefa.pr.gov.br/nfce/NFeInutilizacao4?wsdl", "https://homologacao.nfce.sefa.pr.gov.br/nfce/NFeInutilizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_PROTOCOLO, new WsUrls("https://nfce.sefa.pr.gov.br/nfce/NFeConsultaProtocolo4?wsdl", "https://homologacao.nfce.sefa.pr.gov.br/nfce/NFeConsultaProtocolo4?wsdl")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_STATUS_SERVICO, new WsUrls("https://nfce.sefa.pr.gov.br/nfce/NFeStatusServico4?wsdl", "https://homologacao.nfce.sefa.pr.gov.br/nfce/NFeStatusServico4?wsdl")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_AUTORIZACAO, new WsUrls("https://nfce.sefa.pr.gov.br/nfce/NFeAutorizacao4?wsdl", "https://homologacao.nfce.sefa.pr.gov.br/nfce/NFeAutorizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.NFCE_RET_AUTORIZACAO, new WsUrls("https://nfce.sefa.pr.gov.br/nfce/NFeRetAutorizacao4?wsdl", "https://homologacao.nfce.sefa.pr.gov.br/nfce/NFeRetAutorizacao4?wsdl")),
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("http://www.fazenda.pr.gov.br/nfce/qrcode", "http://www.fazenda.pr.gov.br/nfce/qrcode")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("http://www.fazenda.pr.gov.br/nfce/consulta", "http://www.fazenda.pr.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.RIO_GRANDE_DO_SUL.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_RECEPCAO_EVENTO, new WsUrls("https://nfce.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfce-homologacao.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_INUTILIZACAO, new WsUrls("https://nfce.sefazrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx", "https://nfce-homologacao.sefazrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_PROTOCOLO, new WsUrls("https://nfce.sefazrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx", "https://nfce-homologacao.sefazrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_STATUS_SERVICO, new WsUrls("https://nfce.sefazrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx", "https://nfce-homologacao.sefazrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_AUTORIZACAO, new WsUrls("https://nfce.sefazrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx", "https://nfce-homologacao.sefazrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_RET_AUTORIZACAO, new WsUrls("https://nfce.sefazrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx", "https://nfce-homologacao.sefazrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("https://www.sefaz.rs.gov.br/NFCE/NFCE-COM.aspx", "https://www.sefaz.rs.gov.br/NFCE/NFCE-COM.aspx")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("www.sefaz.rs.gov.br/nfce/consulta", "www.sefaz.rs.gov.br/nfce/consulta"))
                )),
                Map.entry(EstadoBrasil.SAO_PAULO.getUf(), Map.ofEntries(
                        Map.entry(TipoServicoEnum.NFCE_RECEPCAO_EVENTO, new WsUrls("https://nfce.fazenda.sp.gov.br/ws/NFeRecepcaoEvento4.asmx", "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeRecepcaoEvento4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_INUTILIZACAO, new WsUrls("https://nfce.fazenda.sp.gov.br/ws/NFeInutilizacao4.asmx", "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeInutilizacao4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_PROTOCOLO, new WsUrls("https://nfce.fazenda.sp.gov.br/ws/NFeConsultaProtocolo4.asmx", "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeConsultaProtocolo4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_STATUS_SERVICO, new WsUrls("https://nfce.fazenda.sp.gov.br/ws/NFeStatusServico4.asmx", "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeStatusServico4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_NFE_AUTORIZACAO, new WsUrls("https://nfce.fazenda.sp.gov.br/ws/NFeAutorizacao4.asmx", "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeAutorizacao4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_RET_AUTORIZACAO, new WsUrls("https://nfce.fazenda.sp.gov.br/ws/NFeRetAutorizacao4.asmx", "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeRetAutorizacao4.asmx")),
                        Map.entry(TipoServicoEnum.NFCE_QR_CODE, new WsUrls("https://www.nfce.fazenda.sp.gov.br/qrcode", "https://www.homologacao.nfce.fazenda.sp.gov.br/qrcode")),
                        Map.entry(TipoServicoEnum.NFCE_CONSULTA_NFCE, new WsUrls("https://www.nfce.fazenda.sp.gov.br/consultaa", "https://www.homologacao.nfce.fazenda.sp.gov.br/consulta"))
                ))
        );
    }

    private static Set<String> inicializarUfsSvrs() {
        return new HashSet<>(Arrays.asList(
                EstadoBrasil.ACRE.getCodigo().toString(),
                EstadoBrasil.PARA.getCodigo().toString(),
                EstadoBrasil.ALAGOAS.getCodigo().toString(),
                EstadoBrasil.AMAZONAS.getCodigo().toString(),
                EstadoBrasil.AMAPA.getCodigo().toString(),
                EstadoBrasil.RIO_GRANDE_DO_NORTE.getCodigo().toString(),
                EstadoBrasil.DISTRITO_FEDERAL.getCodigo().toString(),
                EstadoBrasil.MATO_GROSSO_DO_SUL.getCodigo().toString(),
                EstadoBrasil.PARAIBA.getCodigo().toString(),
                EstadoBrasil.RIO_DE_JANEIRO.getCodigo().toString(),
                EstadoBrasil.RONDONIA.getCodigo().toString(),
                EstadoBrasil.RORAIMA.getCodigo().toString(),
                EstadoBrasil.SANTA_CATARINA.getCodigo().toString(),
                EstadoBrasil.SERGIPE.getCodigo().toString(),
                EstadoBrasil.TOCANTINS.getCodigo().toString(),
                EstadoBrasil.ESPIRITO_SANTO.getCodigo().toString(),
                EstadoBrasil.CEARA.getCodigo().toString())
        );
    }

    private static Set<String> inicializarUfsNfceSvrs() {
        return new HashSet<>(Arrays.asList(
                EstadoBrasil.ACRE.getUf(),
                EstadoBrasil.ALAGOAS.getUf(),
                EstadoBrasil.AMAPA.getUf(),
                EstadoBrasil.BAHIA.getUf(),
                EstadoBrasil.DISTRITO_FEDERAL.getUf(),
                EstadoBrasil.ESPIRITO_SANTO.getUf(),
                EstadoBrasil.MARANHAO.getUf(),
                EstadoBrasil.PARA.getUf(),
                EstadoBrasil.PARAIBA.getUf(),
                EstadoBrasil.PERNAMBUCO.getUf(),
                EstadoBrasil.PIAUI.getUf(),
                EstadoBrasil.RIO_DE_JANEIRO.getUf(),
                EstadoBrasil.RIO_GRANDE_DO_NORTE.getUf(),
                EstadoBrasil.RONDONIA.getUf(),
                EstadoBrasil.RORAIMA.getUf(),
                EstadoBrasil.SANTA_CATARINA.getUf(),
                EstadoBrasil.SERGIPE.getUf(),
                EstadoBrasil.TOCANTINS.getUf())
        );
    }

    private static class WsUrls {
        final String producao;
        final String homologacao;

        WsUrls(String producao, String homologacao) {
            this.producao = producao;
            this.homologacao = homologacao;
        }

        String get(String tipoAmbiente) {
            return "1".equals(tipoAmbiente) ? producao : homologacao;
        }
    }

    public static <T> T convertXmlToObject(String conteudoXml, Class<T> clazz) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader reader = new StringReader(conteudoXml);
        return (T) unmarshaller.unmarshal(reader);
    }

    public static String objectToXmlNFe(NFe nfe) {
        try {
            JAXBContext context = JAXBContext.newInstance(new Class[]{NFe.class});
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty("jaxb.formatted.output", true);
            StringWriter writer = new StringWriter();
            marshaller.marshal(nfe, writer);
            return writer.toString().replaceAll("ns1:", "").replaceAll("standalone=\"yes\"", "").replaceAll(":ns1", "").replaceAll("ns2:", "").replaceAll("standalone=\"yes\"", "").replaceAll(":ns2", "").replaceAll("ns3:", "").replaceAll(":ns3", "");
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }


}
