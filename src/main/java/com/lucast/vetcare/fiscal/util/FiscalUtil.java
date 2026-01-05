package com.lucast.vetcare.fiscal.util;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.xml.NFe;
import com.lucast.vetcare.fiscal.nfe.xml.NFeProc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.zip.GZIPOutputStream;

public class FiscalUtil {

    private static final Logger logger = LoggerFactory.getLogger(FiscalUtil.class);

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

    private static final Map<String, Map<String, WsUrls>> URLS_NFE_POR_TIPO_EMISSAO;
    private static final Map<String, Map<String, WsUrls>> URLS_NFE_POR_UF;
    private static final Set<String> UFS_SVRS;

    // CTe
    private static final Map<String, Map<String, WsUrls>> URLS_CTE_POR_TIPO_EMISSAO;
    private static final Map<String, Map<String, WsUrls>> URLS_CTE_POR_UF;
    private static final Set<String> UFS_CTE_SVRS;
    private static final Set<String> UFS_CTE_SVSP;

    // NFCe
    private static final Map<String, Map<String, WsUrls>> URLS_NFCE_POR_UF;
    private static final Set<String> UFS_NFCE_SVRS;

    private static final Map<String, WsUrls> URLS_MDFE_POR_TIPO_EMISSAO;
    
    // URLs para MDFe
    static {
        URLS_MDFE_POR_TIPO_EMISSAO = new HashMap<>();
        URLS_MDFE_POR_TIPO_EMISSAO.put("mdfeRecepcaoLote", new WsUrls("https://mdfe.svrs.rs.gov.br/ws/MDFeRecepcao/MDFeRecepcao.asmx", "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeRecepcao/MDFeRecepcao.asmx"));
        URLS_MDFE_POR_TIPO_EMISSAO.put("mdfeRetRecepcao", new WsUrls("https://mdfe.svrs.rs.gov.br/ws/MDFeRetRecepcao/MDFeRetRecepcao.asmx", "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeRetRecepcao/MDFeRetRecepcao.asmx"));
        URLS_MDFE_POR_TIPO_EMISSAO.put("mdfeRecepcaoEvento", new WsUrls("https://mdfe.svrs.rs.gov.br/ws/MDFeRecepcaoEvento/MDFeRecepcaoEvento.asmx", "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeRecepcaoEvento/MDFeRecepcaoEvento.asmx"));
        URLS_MDFE_POR_TIPO_EMISSAO.put("mdfeConsultaMDF", new WsUrls("https://mdfe.svrs.rs.gov.br/ws/MDFeConsulta/MDFeConsulta.asmx", "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeConsulta/MDFeConsulta.asmx"));
        URLS_MDFE_POR_TIPO_EMISSAO.put("mdfeStatusServicoMDF", new WsUrls("https://mdfe.svrs.rs.gov.br/ws/MDFeStatusServico/MDFeStatusServico.asmx", "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeStatusServico/MDFeStatusServico.asmx"));
        URLS_MDFE_POR_TIPO_EMISSAO.put("mdfeConsNaoEnc", new WsUrls("https://mdfe.svrs.rs.gov.br/ws/MDFeConsNaoEnc/MDFeConsNaoEnc.asmx", "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeConsNaoEnc/MDFeConsNaoEnc.asmx"));
        URLS_MDFE_POR_TIPO_EMISSAO.put("MDFeDistribuicaoDFe", new WsUrls("https://mdfe.svrs.rs.gov.br/ws/MDFeDistribuicaoDFe/MDFeDistribuicaoDFe.asmx", "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeDistribuicaoDFe/MDFeDistribuicaoDFe.asmx"));
        URLS_MDFE_POR_TIPO_EMISSAO.put("MDFeRecepcaoSinc", new WsUrls("https://mdfe.svrs.rs.gov.br/ws/MDFeRecepcaoSinc/MDFeRecepcaoSinc.asmx", "https://mdfe-homologacao.svrs.rs.gov.br/ws/MDFeRecepcaoSinc/MDFeRecepcaoSinc.asmx"));
    }
    
    // URLs NFe
    static {
        URLS_NFE_POR_TIPO_EMISSAO = new HashMap<>();
        URLS_NFE_POR_UF = new HashMap<>();
        
        // URLs por Tipo de Emissão
        Map<String, WsUrls> scanUrls = new HashMap<>();
        scanUrls.put("_STATUSSERVICO", new WsUrls("https://www.scan.fazenda.gov.br/NFeStatusServico2/NFeStatusServico2.asmx", "https://hom.nfe.fazenda.gov.br/SCAN/NfeStatusServico2/NfeStatusServico2.asmx"));
        scanUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://www.scan.fazenda.gov.br/NfeConsulta2/NfeConsulta2.asmx", "https://hom.nfe.fazenda.gov.br/SCAN/NfeConsulta2/NfeConsulta2.asmx"));
        scanUrls.put("_RECEPCAO", new WsUrls("https://www.scan.fazenda.gov.br/NfeRecepcao2/NfeRecepcao2.asmx", "https://hom.nfe.fazenda.gov.br/SCAN/NfeRecepcao2/NfeRecepcao2.asmx"));
        scanUrls.put("_RETRECEPCAO", new WsUrls("https://www.scan.fazenda.gov.br/NfeRetRecepcao2/NfeRetRecepcao2.asmx", "https://hom.nfe.fazenda.gov.br/SCAN/NfeRetRecepcao2/NfeRetRecepcao2.asmx"));
        scanUrls.put("_CANCELAMENTO", new WsUrls("https://www.scan.fazenda.gov.br/NfeCancelamento2/NfeCancelamento2.asmx", "https://hom.nfe.fazenda.gov.br/SCAN/NfeCancelamento2/NfeCancelamento2.asmx"));
        scanUrls.put("_INUTILIZACAO", new WsUrls("https://www.scan.fazenda.gov.br/NfeInutilizacao2/NfeInutilizacao2.asmx", "https://hom.nfe.fazenda.gov.br/SCAN/NfeInutilizacao2/NfeInutilizacao2.asmx"));
        scanUrls.put("_CONSULTANFEDEST", new WsUrls("https://www.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx", "https://hom.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx"));
        scanUrls.put("_EVENTO", new WsUrls("https://www.scan.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx", "https://hom.nfe.fazenda.gov.br/SCAM/RecepcaoEvento/RecepcaoEvento.asmx"));
        scanUrls.put("_DOWNLOADNFE", new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx"));
        scanUrls.put("_RECPEVENTO", new WsUrls("https://www.nfe.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx", "https://hom.nfe.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx"));
        URLS_NFE_POR_TIPO_EMISSAO.put("3", scanUrls);
        URLS_NFE_POR_TIPO_EMISSAO.put("5", scanUrls);

        Map<String, WsUrls> svcAnUrls = new HashMap<>();
        svcAnUrls.put("_STATUSSERVICO", new WsUrls("https://www.svc.fazenda.gov.br/NFeStatusServico4/NFeStatusServico4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeStatusServico4"));
        svcAnUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://www.svc.fazenda.gov.br/NFeConsultaProtocolo4/NFeConsultaProtocolo4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeConsultaProtocolo4"));
        svcAnUrls.put("_RECEPCAO", new WsUrls("https://www.svc.fazenda.gov.br/NFeRecepcaoEvento4/NFeRecepcaoEvento4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4"));
        svcAnUrls.put("_NFeAutorizacao", new WsUrls("https://www.svc.fazenda.gov.br/NFeAutorizacao4/NFeAutorizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4"));
        svcAnUrls.put("_NFeRetAutorizacao", new WsUrls("https://www.svc.fazenda.gov.br/NFeRetAutorizacao4/NFeRetAutorizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4"));
        svcAnUrls.put("_RETRECEPCAO", new WsUrls("https://www.svc.fazenda.gov.br/NFeRetAutorizacao4/NFeRetAutorizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4"));
        svcAnUrls.put("_INUTILIZACAO", new WsUrls("https://www.svc.fazenda.gov.br/NFeInutilizacao4/NFeInutilizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeInutilizacao4"));
        svcAnUrls.put("_CONSULTACADASTRO", new WsUrls("https://cad.svrs.rs.gov.br/ws/cadconsultacadastro/cadconsultacadastro4.asmx", "https://homologacao.nfe.ms.gov.br/homologacao/services2/CadConsultaCadastro2"));
        svcAnUrls.put("_EVENTO", new WsUrls("https://www.svc.fazenda.gov.br/NFeRecepcaoEvento4/NFeRecepcaoEvento4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4"));
        svcAnUrls.put("_RECPEVENTO", new WsUrls("https://www.svc.fazenda.gov.br/NFeRecepcaoEvento4/NFeRecepcaoEvento4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4"));
        URLS_NFE_POR_TIPO_EMISSAO.put("6", svcAnUrls);

        Map<String, WsUrls> svcRsUrls = new HashMap<>();
        svcRsUrls.put("_STATUSSERVICO", new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeStatusServico4"));
        svcRsUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeConsultaProtocolo4"));
        svcRsUrls.put("_RECEPCAO", new WsUrls("https://nfe.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4"));
        svcRsUrls.put("_NFeAutorizacao", new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeAutorizacao4"));
        svcRsUrls.put("_NFeRetAutorizacao", new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4"));
        svcRsUrls.put("_RETRECEPCAO", new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4"));
        svcRsUrls.put("_INUTILIZACAO", new WsUrls("https://nfe.svrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeInutilizacao4"));
        svcRsUrls.put("_CONSULTACADASTRO", new WsUrls("https://cad.svrs.rs.gov.br/ws/cadconsultacadastro/cadconsultacadastro4.asmx", "https://homologacao.nfe.ms.gov.br/homologacao/services2/CadConsultaCadastro2"));
        svcRsUrls.put("_EVENTO", new WsUrls("https://nfe.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4"));
        svcRsUrls.put("_RECPEVENTO", new WsUrls("https://nfe.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4"));
        URLS_NFE_POR_TIPO_EMISSAO.put("7", svcRsUrls);
        
        // Amazonas
        Map<String, WsUrls> amUrls = new HashMap<>();
        amUrls.put("_STATUSSERVICO", new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeStatusServico4", "https://homnfe.sefaz.am.gov.br/services2/services/NfeStatusServico4"));
        amUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeConsulta4", "https://homnfe.sefaz.am.gov.br/services2/services/NfeConsulta4"));
        amUrls.put("_RECEPCAO", new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeRecepcao2", "https://homnfe.sefaz.am.gov.br/services2/services/NfeRecepcao4"));
        amUrls.put("_NFeAutorizacao", new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeAutorizacao4", "https://homnfe.sefaz.am.gov.br/services2/services/NfeAutorizacao4"));
        amUrls.put("_NFeRetAutorizacao", new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeRetAutorizacao4", "https://homnfe.sefaz.am.gov.br/services2/services/NfeRetAutorizacao4"));
        amUrls.put("_RETRECEPCAO", new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeRetAutorizacao4", "https://homnfe.sefaz.am.gov.br/services2/services/NfeRetRecepcao2"));
        amUrls.put("_CANCELAMENTO", new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeCancelamento2", "https://homnfe.sefaz.am.gov.br/services2/services/NfeCancelamento2"));
        amUrls.put("_INUTILIZACAO", new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/NfeInutilizacao4", "https://homnfe.sefaz.am.gov.br/services2/services/NfeInutilizacao2"));
        amUrls.put("_CONSULTACADASTRO", new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/cadconsultacadastro2", "https://homnfe.sefaz.am.gov.br/services2/services/cadconsultacadastro4"));
        amUrls.put("_EVENTO", new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/RecepcaoEvento4", "https://homnfe.sefaz.am.gov.br/services2/services/RecepcaoEvento4"));
        amUrls.put("_CONSULTANFEDEST", new WsUrls("https://www.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx", "https://hom.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx"));
        amUrls.put("_DOWNLOADNFE", new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx"));
        amUrls.put("_RECPEVENTO", new WsUrls("https://nfe.sefaz.am.gov.br/services2/services/RecepcaoEvento4", "https://hom.nfe.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx"));
        URLS_NFE_POR_UF.put(EstadoBrasil.AMAZONAS.getCodigo().toString(), amUrls);

        // Bahia
        Map<String, WsUrls> baUrls = new HashMap<>();
        baUrls.put("_STATUSSERVICO", new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/nfenw/NfeStatusServico2.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/NFeStatusServico4/NFeStatusServico4.asmx"));
        baUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/nfenw/NfeConsulta2.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/NFeConsultaProtocolo4/NFeConsultaProtocolo4.asmx"));
        baUrls.put("_RECEPCAO", new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/nfenw/NfeRecepcao2.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/nfenw/NfeRecepcao2.asmx"));
        baUrls.put("_RETRECEPCAO", new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/nfenw/NfeRetRecepcao2.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/nfenw/NfeRetRecepcao2.asmx"));
        baUrls.put("_CANCELAMENTO", new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/nfenw/NfeCancelamento2.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/nfenw/NfeCancelamento2.asmx"));
        baUrls.put("_INUTILIZACAO", new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/nfenw/NfeInutilizacao2.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/NFeInutilizacao4/NFeInutilizacao4.asmx"));
        baUrls.put("_CONSULTACADASTRO", new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/CadConsultaCadastro4/CadConsultaCadastro4.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/CadConsultaCadastro4/CadConsultaCadastro4.asmx"));
        baUrls.put("_EVENTO", new WsUrls("https://nfe.sefaz.ba.gov.br/webservices/sre/RecepcaoEvento.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/sre/RecepcaoEvento.asmx"));
        baUrls.put("_CONSULTANFEDEST", new WsUrls("https://www.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx", "https://hom.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx"));
        baUrls.put("_DOWNLOADNFE", new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx"));
        baUrls.put("_RECPEVENTO", new WsUrls("https://www.nfe.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx", "https://hnfe.sefaz.ba.gov.br/webservices/NFeRecepcaoEvento4/NFeRecepcaoEvento4.asmx"));
        URLS_NFE_POR_UF.put(EstadoBrasil.BAHIA.getCodigo().toString(), baUrls);

        // Mato Grosso do Sul
        Map<String, WsUrls> msUrls = new HashMap<>();
        msUrls.put("_STATUSSERVICO", new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeStatusServico4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeStatusServico4"));
        msUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeConsultaProtocolo4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeConsultaProtocolo4"));
        msUrls.put("_RECEPCAO", new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4"));
        msUrls.put("_NFeAutorizacao", new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeAutorizacao4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeAutorizacao4"));
        msUrls.put("_NFeRetAutorizacao", new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4"));
        msUrls.put("_RETRECEPCAO", new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRetAutorizacao4"));
        msUrls.put("_INUTILIZACAO", new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeInutilizacao4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeInutilizacao4"));
        msUrls.put("_CONSULTACADASTRO", new WsUrls("https://nfe.sefaz.ms.gov.br/ws/CadConsultaCadastro4", "https://hom.nfe.sefaz.ms.gov.br/ws/CadConsultaCadastro4"));
        msUrls.put("_EVENTO", new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4"));
        msUrls.put("_RECPEVENTO", new WsUrls("https://nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4", "https://hom.nfe.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4"));
        URLS_NFE_POR_UF.put(EstadoBrasil.MATO_GROSSO_DO_SUL.getCodigo().toString(), msUrls);

        // Mato Grosso
        Map<String, WsUrls> mtUrls = new HashMap<>();
        mtUrls.put("_STATUSSERVICO", new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/NfeStatusServico4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/NfeStatusServico4?wsdl"));
        mtUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/NfeConsulta4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/NfeConsulta4?wsdl"));
        mtUrls.put("_RECEPCAO", new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/RecepcaoEvento4?wsdl", "https://homologacao.nfe.ms.gov.br/homologacao/services2/NfeRecepcao2"));
        mtUrls.put("_NFeAutorizacao", new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/NfeAutorizacao4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/NfeAutorizacao4?wsdl"));
        mtUrls.put("_NFeRetAutorizacao", new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/NfeRetAutorizacao4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/NfeRetAutorizacao4?wsdl"));
        mtUrls.put("_RETRECEPCAO", new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/NfeRetAutorizacao4?wsdl", "https://homologacao.nfe.ms.gov.br/homologacao/services2/NfeRetRecepcao2"));
        mtUrls.put("_INUTILIZACAO", new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/NfeInutilizacao4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/NfeInutilizacao4?wsdl"));
        mtUrls.put("_CONSULTACADASTRO", new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/CadConsultaCadastro4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/CadConsultaCadastro4?wsdl"));
        mtUrls.put("_EVENTO", new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/RecepcaoEvento4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeRecepcaoEvento4?wsdl"));
        mtUrls.put("_RECPEVENTO", new WsUrls("https://nfe.sefaz.mt.gov.br/nfews/v2/services/RecepcaoEvento4?wsdl", "https://homologacao.sefaz.mt.gov.br/nfews/v2/services/RecepcaoEvento4?wsdl"));
        URLS_NFE_POR_UF.put(EstadoBrasil.MATO_GROSSO.getCodigo().toString(), mtUrls);

        // Goiás
        Map<String, WsUrls> goUrls = new HashMap<>();
        goUrls.put("_STATUSSERVICO", new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/v2/NfeStatusServico2?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/v2/NfeStatusServico2?wsd"));
        goUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/v2/NfeConsulta2?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeConsultaProtocolo4?wsdl"));
        goUrls.put("_RECEPCAO", new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/v2/NfeRecepcao2?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/v2/NfeRecepcao2?wsd"));
        goUrls.put("_RETRECEPCAO", new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/v2/NfeRetRecepcao2?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/v2/NfeRetRecepcao2?wsdl"));
        goUrls.put("_CANCELAMENTO", new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/v2/NfeCancelamento2?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/v2/NfeCancelamento2?wsdl"));
        goUrls.put("_INUTILIZACAO", new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/v2/NfeInutilizacao2?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeInutilizacao4?wsdl"));
        goUrls.put("_CONSULTACADASTRO", new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/CadConsultaCadastro4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/v2/CadConsultaCadastro2?wsdl"));
        goUrls.put("_EVENTO", new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/v2/NfeRecepcaoEvento?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/v2/NfeRecepcaoEvento?wsdl"));
        goUrls.put("_CONSULTANFEDEST", new WsUrls("https://www.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx", "https://hom.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx"));
        goUrls.put("_DOWNLOADNFE", new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx"));
        goUrls.put("_RECPEVENTO", new WsUrls("https://www.nfe.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx", "https://hom.nfe.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx"));
        URLS_NFE_POR_UF.put(EstadoBrasil.GOIAS.getCodigo().toString(), goUrls);

        // Pernambuco
        Map<String, WsUrls> peUrls = new HashMap<>();
        peUrls.put("_STATUSSERVICO", new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeStatusServico4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeStatusServico4?wsdl"));
        peUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeConsultaProtocolo4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeConsultaProtocolo4?wsdl"));
        peUrls.put("_RECEPCAO", new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeRecepcaoEvento4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeRecepcaoEvento4?wsd2"));
        peUrls.put("_NFeAutorizacao", new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeAutorizacao4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeAutorizacao4?wsdl"));
        peUrls.put("_NFeRetAutorizacao", new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeRetAutorizacao4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeRetAutorizacao4?wsdl"));
        peUrls.put("_RETRECEPCAO", new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeRetAutorizacao4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeRetAutorizacao4?wsdl"));
        peUrls.put("_INUTILIZACAO", new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeInutilizacao4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeInutilizacao4?wsdl"));
        peUrls.put("_CONSULTACADASTRO", new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/CadConsultaCadastro4?wsdl", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/CadConsultaCadastro4?wsdl"));
        peUrls.put("_EVENTO", new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeRecepcaoEvento4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/RecepcaoEvento"));
        peUrls.put("_CONSULTANFEDEST", new WsUrls("https://www.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx", "https://hom.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx"));
        peUrls.put("_RECPEVENTO", new WsUrls("https://nfe.sefaz.pe.gov.br/nfe-service/services/NFeRecepcaoEvento4", "https://nfehomolog.sefaz.pe.gov.br/nfe-service/services/NFeRecepcaoEvento4?wsdl"));
        URLS_NFE_POR_UF.put(EstadoBrasil.PERNAMBUCO.getCodigo().toString(), peUrls);

        // São Paulo
        Map<String, WsUrls> spUrls = new HashMap<>();
        spUrls.put("_STATUSSERVICO", new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nfestatusservico4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nfestatusservico4.asmx"));
        spUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nfeconsultaprotocolo4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nfeconsultaprotocolo4.asmx"));
        spUrls.put("_RECEPCAO", new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nferecepcaoevento4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nfeautorizacao.asmx"));
        spUrls.put("_NFeAutorizacao", new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nfeautorizacao4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nfeautorizacao4.asmx"));
        spUrls.put("_NFeRetAutorizacao", new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nferetautorizacao4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nferetautorizacao4.asmx"));
        spUrls.put("_RETRECEPCAO", new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nferetautorizacao4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nferetautorizacao.asmx"));
        spUrls.put("_INUTILIZACAO", new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nfeinutilizacao4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nfeinutilizacao4.asmx"));
        spUrls.put("_CONSULTACADASTRO", new WsUrls("https://nfe.fazenda.sp.gov.br/ws/cadconsultacadastro4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/cadconsultacadastro4.asmx"));
        spUrls.put("_EVENTO", new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nferecepcaoevento4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nferecepcaoevento4.asmx"));
        spUrls.put("_RECPEVENTO", new WsUrls("https://nfe.fazenda.sp.gov.br/ws/nferecepcaoevento4.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/ws/nferecepcaoevento4.asmx"));
        URLS_NFE_POR_UF.put(EstadoBrasil.SAO_PAULO.getCodigo().toString(), spUrls);

        // Rio Grande do Sul
        Map<String, WsUrls> rsUrls = new HashMap<>();
        rsUrls.put("_STATUSSERVICO", new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx"));
        rsUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx"));
        rsUrls.put("_RECEPCAO", new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx"));
        rsUrls.put("_NFeAutorizacao", new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx"));
        rsUrls.put("_NFeRetAutorizacao", new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx"));
        rsUrls.put("_RETRECEPCAO", new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx"));
        rsUrls.put("_INUTILIZACAO", new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx"));
        rsUrls.put("_CONSULTACADASTRO", new WsUrls("https://cad.sefazrs.rs.gov.br/ws/cadconsultacadastro/cadconsultacadastro4.asmx", "https://cad.sefazrs.rs.gov.br/ws/cadconsultacadastro/cadconsultacadastro4.asmx"));
        rsUrls.put("_EVENTO", new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asm"));
        rsUrls.put("_RECPEVENTO", new WsUrls("https://nfe.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx"));
        URLS_NFE_POR_UF.put(EstadoBrasil.RIO_GRANDE_DO_SUL.getCodigo().toString(), rsUrls);

        // Paraná
        Map<String, WsUrls> prUrls = new HashMap<>();
        prUrls.put("_STATUSSERVICO", new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeStatusServico4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeStatusServico4?wsdl"));
        prUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeConsultaProtocolo4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeConsultaProtocolo4?wsdl"));
        prUrls.put("_RECEPCAO", new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeRecepcaoEvento4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeRecepcaoEvento4?wsdl"));
        prUrls.put("_NFeAutorizacao", new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeAutorizacao4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeAutorizacao4?wsdl"));
        prUrls.put("_NFeRetAutorizacao", new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeRetAutorizacao4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeRetAutorizacao4?wsdl"));
        prUrls.put("_RETRECEPCAO", new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeRecepcaoEvento4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeRetAutorizacao4?wsdl"));
        prUrls.put("_INUTILIZACAO", new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeInutilizacao4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeInutilizacao4?wsdl"));
        prUrls.put("_CONSULTACADASTRO", new WsUrls("https://nfe.sefa.pr.gov.br/nfe/CadConsultaCadastro4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/CadConsultaCadastro4?wsdl"));
        prUrls.put("_EVENTO", new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeRecepcaoEvento4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeRecepcaoEvento4?wsdl"));
        prUrls.put("_RECPEVENTO", new WsUrls("https://nfe.sefa.pr.gov.br/nfe/NFeRecepcaoEvento4?wsdl", "https://homologacao.nfe.sefa.pr.gov.br/nfe/NFeRecepcaoEvento4?wsdl"));
        URLS_NFE_POR_UF.put(EstadoBrasil.PARANA.getCodigo().toString(), prUrls);

        // Minas Gerais
        Map<String, WsUrls> mgUrls = new HashMap<>();
        mgUrls.put("_STATUSSERVICO", new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeStatusServico4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NFeStatusServico4"));
        mgUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeConsultaProtocolo4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NFeConsultaProtocolo4"));
        mgUrls.put("_RECEPCAO", new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NfeRecepcao2", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NfeRecepcao2x"));
        mgUrls.put("_NFeAutorizacao", new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeAutorizacao4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NFeAutorizacao4"));
        mgUrls.put("_NFeRetAutorizacao", new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeRetAutorizacao4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NFeRetAutorizacao4"));
        mgUrls.put("_RETRECEPCAO", new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeRecepcaoEvento4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NfeRetRecepcao2"));
        mgUrls.put("_CANCELAMENTO", new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NfeCancelamento2", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NfeCancelamento2"));
        mgUrls.put("_INUTILIZACAO", new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeInutilizacao4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NFeInutilizacao4"));
        mgUrls.put("_CONSULTACADASTRO", new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/CadConsultaCadastro4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/CadConsultaCadastro4"));
        mgUrls.put("_EVENTO", new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeRecepcaoEvento4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/RecepcaoEvento"));
        mgUrls.put("_CONSULTANFEDEST", new WsUrls("https://www.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx", "https://hom.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx"));
        mgUrls.put("_DOWNLOADNFE", new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx"));
        mgUrls.put("_RECPEVENTO", new WsUrls("https://nfe.fazenda.mg.gov.br/nfe2/services/NFeRecepcaoEvento4", "https://hnfe.fazenda.mg.gov.br/nfe2/services/NFeRecepcaoEvento4"));
        URLS_NFE_POR_UF.put(EstadoBrasil.MINAS_GERAIS.getCodigo().toString(), mgUrls);


        Map<String, WsUrls> svanUrls = new HashMap<>();
        svanUrls.put("_STATUSSERVICO", new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeStatusServico2/NfeStatusServico2.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NFeStatusServico4/NFeStatusServico4.asmx"));
        svanUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeConsulta2/NfeConsulta2.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NFeConsultaProtocolo4/NFeConsultaProtocolo4.asmx"));
        svanUrls.put("_RECEPCAO", new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeRecepcao2/NfeRecepcao2.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeRecepcao2/NfeRecepcao2.asmx"));
        svanUrls.put("_RETRECEPCAO", new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeRetRecepcao2/NfeRetRecepcao2.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeRetRecepcao2/NfeRetRecepcao2.asmx"));
        svanUrls.put("_CANCELAMENTO", new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeCancelamento2/NfeCancelamento2.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeCancelamento2/NfeCancelamento2.asmx"));
        svanUrls.put("_INUTILIZACAO", new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeInutilizacao2/NfeInutilizacao2.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NFeInutilizacao4/NFeInutilizacao4.asmx"));
        svanUrls.put("_EVENTO", new WsUrls("https://www.sefazvirtual.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx", "https://hom.sefazvirtual.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx"));
        svanUrls.put("_CONSULTANFEDEST", new WsUrls("https://www.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx", "https://hom.nfe.fazenda.gov.br/NFeConsultaDest/NFeConsultaDest.asmx"));
        svanUrls.put("_DOWNLOADNFE", new WsUrls("https://www.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NfeDownloadNF/NfeDownloadNF.asmx"));
        svanUrls.put("_RECPEVENTO", new WsUrls("https://www.nfe.fazenda.gov.br/RecepcaoEvento/RecepcaoEvento.asmx", "https://hom.sefazvirtual.fazenda.gov.br/NFeRecepcaoEvento4/NFeRecepcaoEvento4.asmx"));
        URLS_NFE_POR_UF.put("SVAN", svanUrls);


        Map<String, WsUrls> svrsUrls = new HashMap<>();
        svrsUrls.put("_STATUSSERVICO", new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx"));
        svrsUrls.put("_CONSULTAPROTOCOLO", new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx"));
        svrsUrls.put("_RECEPCAO", new WsUrls("https://nfe.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx"));
        svrsUrls.put("_NFeAutorizacao", new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx"));
        svrsUrls.put("_NFeRetAutorizacao", new WsUrls("https://nfe.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx"));
        svrsUrls.put("_RETRECEPCAO", new WsUrls("https://nfe.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx"));
        svrsUrls.put("_INUTILIZACAO", new WsUrls("https://nfe.svrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx"));
        svrsUrls.put("_CONSULTACADASTRO", new WsUrls("https://cad.svrs.rs.gov.br/ws/cadconsultacadastro/cadconsultacadastro4.asmx", "https://cad-homologacao.svrs.rs.gov.br/ws/cadconsultacadastro/cadconsultacadastro4.asmx"));
        svrsUrls.put("_EVENTO", new WsUrls("https://nfe.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx"));
        svrsUrls.put("_RECPEVENTO", new WsUrls("https://nfe.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfe-homologacao.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx"));
        URLS_NFE_POR_UF.put("SVRS", svrsUrls);
    }
    
    // URLs CTe
    static {
        URLS_CTE_POR_TIPO_EMISSAO = new HashMap<>();
        URLS_CTE_POR_UF = new HashMap<>();

        // CTe URLs
        Map<String, WsUrls> cteSvrsUrls = new HashMap<>();
        cteSvrsUrls.put("cteRecepcaoLote", new WsUrls("https://cte.svrs.rs.gov.br/ws/CTeRecepcaoSincV4/CTeRecepcaoSincV4.asmx", "https://cte-homologacao.svrs.rs.gov.br/ws/CTeRecepcaoSincV4/CTeRecepcaoSincV4.asmx"));
        cteSvrsUrls.put("cteConsultaProtocolo", new WsUrls("https://cte.svrs.rs.gov.br/ws/CTeConsultaV4/CTeConsultaV4.asmx", "https://cte-homologacao.svrs.rs.gov.br/ws/CTeConsultaV4/CTeConsultaV4.asmx"));
        cteSvrsUrls.put("cteStatusServico", new WsUrls("https://cte.svrs.rs.gov.br/ws/CTeStatusServicoV4/CTeStatusServicoV4.asmx", "https://cte-homologacao.svrs.rs.gov.br/ws/CTeStatusServicoV4/CTeStatusServicoV4.asmx"));
        cteSvrsUrls.put("cteRecepcaoEvento", new WsUrls("https://cte.svrs.rs.gov.br/ws/CTeRecepcaoEventoV4/CTeRecepcaoEventoV4.asmx", "https://cte-homologacao.svrs.rs.gov.br/ws/CTeRecepcaoEventoV4/CTeRecepcaoEventoV4.asmx"));
        cteSvrsUrls.put("cteRecepcaoOS", new WsUrls("https://cte.svrs.rs.gov.br/ws/CTeRecepcaoOSV4/CTeRecepcaoOSV4.asmx", "https://cte-homologacao.svrs.rs.gov.br/ws/CTeRecepcaoOSV4/CTeRecepcaoOSV4.asmx\n"));
        URLS_CTE_POR_TIPO_EMISSAO.put("7", cteSvrsUrls);

        Map<String, WsUrls> cteSvspUrls = new HashMap<>();
        cteSvspUrls.put("CteRecepcao", new WsUrls("https://nfe.fazenda.sp.gov.br/cteWEB/services/cteRecepcao.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/cteWEB/services/CteRecepcao.asmx"));
        cteSvspUrls.put("CteRetRecepcao", new WsUrls("https://nfe.fazenda.sp.gov.br/cteWEB/services/CteRetRecepcao.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/cteWEB/services/CteRetRecepcao.asmx"));
        cteSvspUrls.put("CteCancelamento", new WsUrls("https://nfe.fazenda.sp.gov.br/cteWEB/services/CteCancelamento.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/cteWEB/services/CteCancelamento.asmx"));
        cteSvspUrls.put("CteConsultaProtocolo", new WsUrls("https://nfe.fazenda.sp.gov.br/cteWEB/services/CteConsulta.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/cteWEB/services/CteConsulta.asmx"));
        cteSvspUrls.put("CteStatusServico", new WsUrls("https://nfe.fazenda.sp.gov.br/cteWEB/services/CteStatusServico.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/cteWEB/services/CteStatusServico.asmx"));
        cteSvspUrls.put("cteRecepcaoEvento", new WsUrls("https://nfe.fazenda.sp.gov.br/cteWEB/services/CteRecepcaoEvento.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/cteWEB/services/CteRecepcaoEvento.asmx"));
        URLS_CTE_POR_TIPO_EMISSAO.put("8", cteSvspUrls);

        Map<String, WsUrls> cteMtUrls = new HashMap<>();
        cteMtUrls.put("cteRecepcaoLote", new WsUrls("https://cte.sefaz.mt.gov.br/ctews2/services/CTeRecepcaoSincV4", "https://homologacao.sefaz.mt.gov.br/ctews2/services/CTeRecepcaoSincV4"));
        cteMtUrls.put("cteConsultaProtocolo", new WsUrls("https://cte.sefaz.mt.gov.br/ctews2/services/CTeConsultaV4", "https://homologacao.sefaz.mt.gov.br/ctews2/services/CTeConsultaV4"));
        cteMtUrls.put("cteStatusServico", new WsUrls("https://cte.sefaz.mt.gov.br/ctews2/services/CTeStatusServicoV4", "https://homologacao.sefaz.mt.gov.br/ctews2/services/CTeStatusServicoV4?wsdl"));
        cteMtUrls.put("cteRecepcaoEvento", new WsUrls("https://cte.sefaz.mt.gov.br/ctews2/services/CTeRecepcaoEventoV4?wsdl", "https://homologacao.sefaz.mt.gov.br/ctews2/services/CTeRecepcaoEventoV4?wsdl"));
        cteMtUrls.put("cteRecepcaoOS", new WsUrls("https://cte.sefaz.mt.gov.br/ctews2/services/CTeRecepcaoEventoV4?wsdl", "https://homologacao.sefaz.mt.gov.br/ctews/services/CTeRecepcaoOSV4?wsdl"));
        URLS_CTE_POR_UF.put(EstadoBrasil.MATO_GROSSO.getUf(), cteMtUrls);

        Map<String, WsUrls> cteMsUrls = new HashMap<>();
        cteMsUrls.put("CteRecepcao", new WsUrls("https://producao.cte.ms.gov.br/cteWEB/CteRecepcao.asmx", "https://homologacao.cte.ms.gov.br/cteWEB/CteRecepcao.asmx"));
        cteMsUrls.put("CteRetRecepcao", new WsUrls("https://producao.cte.ms.gov.br/cteWEB/CteRetRecepcao.asmx", "https://homologacao.cte.ms.gov.br/cteWEB/CteRetRecepcao.asmx"));
        cteMsUrls.put("CteCancelamento", new WsUrls("https://producao.cte.ms.gov.br/cteWEB/CteCancelamento.asmx", "https://homologacao.cte.ms.gov.br/cteWEB/CteCancelamento.asmx"));
        cteMsUrls.put("CteInutilizacao", new WsUrls("https://producao.cte.ms.gov.br/cteWEB/CteInutilizacao.asmx", "https://homologacao.cte.ms.gov.br/cteWEB/CteInutilizacao.asmx"));
        cteMsUrls.put("CteConsultaProtocolo", new WsUrls("https://producao.cte.ms.gov.br/cteWEB/CteConsulta.asmx", "https://homologacao.cte.ms.gov.br/cteWEB/CteConsulta.asmx"));
        cteMsUrls.put("CteStatusServico", new WsUrls("https://producao.cte.ms.gov.br/cteWEB/CteStatusServico.asmx", "https://homologacao.cte.ms.gov.br/cteWEB/CteStatusServico.asmx"));
        cteMsUrls.put("cteRecepcaoEvento", new WsUrls("https://producao.cte.ms.gov.br/cteWEB/cteRecepcaoEvento.asmx", "https://homologacao.cte.ms.gov.br/cteWEB/cteRecepcaoEvento.asmx"));
        URLS_CTE_POR_UF.put(EstadoBrasil.MATO_GROSSO_DO_SUL.getUf(), cteMsUrls);

        Map<String, WsUrls> cteMgUrls = new HashMap<>();
        cteMgUrls.put("CteRecepcao", new WsUrls("https://cte.fazenda.mg.gov.br/cte/services/CteRecepcao", "https://hcte.fazenda.mg.gov.br/cte/services/CteRecepcao"));
        cteMgUrls.put("CteRetRecepcao", new WsUrls("https://cte.fazenda.mg.gov.br/cte/services/CteRetRecepcao", "https://hcte.fazenda.mg.gov.br/cte/services/CteRetRecepcao"));
        cteMgUrls.put("CteCancelamento", new WsUrls("https://cte.fazenda.mg.gov.br/cte/services/CteCancelamento", "https://hcte.fazenda.mg.gov.br/cte/services/CteCancelamento"));
        cteMgUrls.put("CteInutilizacao", new WsUrls("https://cte.fazenda.mg.gov.br/cte/services/CteInutilizacao", "https://hcte.fazenda.mg.gov.br/cte/services/CteInutilizacao"));
        cteMgUrls.put("CteConsultaProtocolo", new WsUrls("https://cte.fazenda.mg.gov.br/cte/services/CteConsulta", "https://hcte.fazenda.mg.gov.br/cte/services/CteConsulta"));
        cteMgUrls.put("CteStatusServico", new WsUrls("https://cte.fazenda.mg.gov.br/cte/services/CteStatusServico", "https://hcte.fazenda.mg.gov.br/cte/services/CteStatusServico"));
        cteMgUrls.put("cteRecepcaoEvento", new WsUrls("https://cte.fazenda.mg.gov.br/cte/services/RecepcaoEvento", "https://hcte.fazenda.mg.gov.br/cte/services/cteRecepcaoEvento"));
        URLS_CTE_POR_UF.put(EstadoBrasil.MINAS_GERAIS.getUf(), cteMgUrls);

        Map<String, WsUrls> ctePrUrls = new HashMap<>();
        ctePrUrls.put("cteRecepcaoLote", new WsUrls("https://cte.fazenda.pr.gov.br/cte4/CTeRecepcaoSincV4?wsdl", "https://homologacao.cte.fazenda.pr.gov.br/cte4/CTeRecepcaoSincV4"));
        ctePrUrls.put("cteConsultaProtocolo", new WsUrls("https://cte.fazenda.pr.gov.br/cte4/CTeConsultaV4?wsdl", "https://homologacao.cte.fazenda.pr.gov.br/cte4/CTeConsultaV4"));
        ctePrUrls.put("cteStatusServico", new WsUrls("https://cte.fazenda.pr.gov.br/cte4/CTeStatusServicoV4?wsdl", "https://homologacao.cte.fazenda.pr.gov.br/cte4/CTeStatusServicoV4?wsdl"));
        ctePrUrls.put("cteRecepcaoEvento", new WsUrls("https://cte.fazenda.pr.gov.br/cte4/CTeRecepcaoEventoV4?wsdl", "https://homologacao.cte.fazenda.pr.gov.br/cte4/CTeRecepcaoEventoV4?wsdl"));
        ctePrUrls.put("cteRecepcaoOS", new WsUrls("https://cte.fazenda.pr.gov.br/cte4/CTeRecepcaoOSV4?wsdl", "https://homologacao.cte.fazenda.pr.gov.br/cte4/CTeRecepcaoOSV4?wsdl"));
        ctePrUrls.put("CteInutilizacao", new WsUrls("https://cte.svrs.rs.gov.br/ws/cteinutilizacao/CteInutilizacao4.asmx", "https://homologacao.cte.svrs.rs.gov.br/ws/cteinutilizacao/CteInutilizacao4.asmx"));
        URLS_CTE_POR_UF.put(EstadoBrasil.PARANA.getUf(), ctePrUrls);

        Map<String, WsUrls> cteRsUrls = new HashMap<>();
        cteRsUrls.put("CteRecepcao", new WsUrls("https://cte.sefaz.rs.gov.br/ws/cterecepcao/CteRecepcao.asmx", "https://homologacao.cte.sefaz.rs.gov.br/ws/cterecepcao/CteRecepcao.asmx"));
        cteRsUrls.put("CteRetRecepcao", new WsUrls("https://cte.sefaz.rs.gov.br/ws/cteretrecepcao/CteRetRecepcao.asmx", "https://homologacao.cte.sefaz.rs.gov.br/ws/cteretrecepcao/CteRetRecepcao.asmx"));
        cteRsUrls.put("CteCancelamento", new WsUrls("https://cte.sefaz.rs.gov.br/ws/ctecancelamento/ctecancelamento.asmx", "https://homologacao.cte.sefaz.rs.gov.br/ws/ctecancelamento/ctecancelamento.asmx"));
        cteRsUrls.put("CteInutilizacao", new WsUrls("https://cte.sefaz.rs.gov.br/ws/cteinutilizacao/cteinutilizacao.asmx", "https://homologacao.cte.sefaz.rs.gov.br/ws/cteinutilizacao/cteinutilizacao.asmx"));
        cteRsUrls.put("CteConsultaProtocolo", new WsUrls("https://cte.sefaz.rs.gov.br/ws/cteconsulta/cteconsulta.asmx", "https://homologacao.cte.sefaz.rs.gov.br/ws/cteconsulta/cteconsulta.asmx"));
        cteRsUrls.put("CteStatusServico", new WsUrls("https://cte.sefaz.rs.gov.br/ws/ctestatusservico/ctestatusservico.asm", "https://homologacao.cte.sefaz.rs.gov.br/ws/ctestatusservico/ctestatusservico.asmx"));
        cteRsUrls.put("cteRecepcaoEvento", new WsUrls("https://cte.sefaz.rs.gov.br/ws/cteRecepcaoEvento/cteRecepcaoEvento.asmx", "https://homologacao.cte.sefaz.rs.gov.br/ws/cteRecepcaoEvento/cteRecepcaoEvento.asmx"));
        URLS_CTE_POR_UF.put(EstadoBrasil.RIO_GRANDE_DO_SUL.getUf(), cteRsUrls);

        Map<String, WsUrls> cteSpUrls = new HashMap<>();
        cteSpUrls.put("cteRecepcaoLote", new WsUrls("https://nfe.fazenda.sp.gov.br/cteWEB/services/cteRecepcao.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/CTeWS/WS/CTeRecepcaoSincV4.asmx"));
        cteSpUrls.put("cteRetRecepcao", new WsUrls("https://nfe.fazenda.sp.gov.br/cteWEB/services/cteRetRecepcao.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/cteWEB/services/cteRetRecepcao.asmx"));
        cteSpUrls.put("CteCancelamento", new WsUrls("https://nfe.fazenda.sp.gov.br/cteWEB/services/cteCancelamento.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/cteWEB/services/cteCancelamento.asmx"));
        cteSpUrls.put("CteInutilizacao", new WsUrls("https://nfe.fazenda.sp.gov.br/cteWEB/services/cteInutilizacao.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/cteWEB/services/cteInutilizacao.asmx"));
        cteSpUrls.put("cteConsultaProtocolo", new WsUrls("https://nfe.fazenda.sp.gov.br/cteWEB/services/cteConsulta.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/CTeWS/WS/CTeConsultaV4.asmx"));
        cteSpUrls.put("cteStatusServico", new WsUrls("https://nfe.fazenda.sp.gov.br/cteWEB/services/cteStatusServico.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/CTeWS/WS/CTeStatusServicoV4.asmx"));
        cteSpUrls.put("cteRecepcaoEvento", new WsUrls("https://nfe.fazenda.sp.gov.br/cteWEB/services/cteRecepcaoEvento.asmx", "https://homologacao.nfe.fazenda.sp.gov.br/CTeWS/WS/CTeRecepcaoEventoV4.asmx"));
        URLS_CTE_POR_UF.put(EstadoBrasil.SAO_PAULO.getUf(), cteSpUrls);
    }

    // URLs NFCe
    static {
        URLS_NFCE_POR_UF = new HashMap<>();
        
        UFS_SVRS = new HashSet<>(Arrays.asList(
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

        UFS_CTE_SVRS = new HashSet<>(Arrays.asList(
                EstadoBrasil.ACRE.getUf(),
                EstadoBrasil.ALAGOAS.getUf(),
                EstadoBrasil.AMAZONAS.getUf(),
                EstadoBrasil.BAHIA.getUf(),
                EstadoBrasil.CEARA.getUf(),
                EstadoBrasil.DISTRITO_FEDERAL.getUf(),
                EstadoBrasil.ESPIRITO_SANTO.getUf(),
                EstadoBrasil.GOIAS.getUf(),
                EstadoBrasil.MARANHAO.getUf(),
                EstadoBrasil.PARA.getUf(),
                EstadoBrasil.PARAIBA.getUf(),
                EstadoBrasil.PIAUI.getUf(),
                EstadoBrasil.RIO_DE_JANEIRO.getUf(),
                EstadoBrasil.RIO_GRANDE_DO_NORTE.getUf(),
                EstadoBrasil.RONDONIA.getUf(),
                EstadoBrasil.SANTA_CATARINA.getUf(),
                EstadoBrasil.SERGIPE.getUf(),
                EstadoBrasil.TOCANTINS.getUf())
        );
        
        UFS_CTE_SVSP = new HashSet<>(Arrays.asList(
                EstadoBrasil.AMAPA.getUf(),
                EstadoBrasil.PERNAMBUCO.getUf(),
                EstadoBrasil.RORAIMA.getUf())
        );

        UFS_NFCE_SVRS = new HashSet<>(Arrays.asList(
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
        
        Map<String, WsUrls> nfceSvrsUrls = new HashMap<>();
        nfceSvrsUrls.put("RecepcaoEvento", new WsUrls("https://nfce.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfce-homologacao.svrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx"));
        nfceSvrsUrls.put("Inutilizacao", new WsUrls("https://nfce.svrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx", "https://nfce-homologacao.svrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx"));
        nfceSvrsUrls.put("ConsultaProtocolo", new WsUrls("https://nfce.svrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx", "https://nfce-homologacao.svrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx"));
        nfceSvrsUrls.put("NFeStatusServico", new WsUrls("https://nfce.svrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx", "https://nfce-homologacao.svrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx"));
        nfceSvrsUrls.put("NFeAutorizacao", new WsUrls("https://nfce.svrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx", "https://nfce-homologacao.svrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx"));
        nfceSvrsUrls.put("RetAutorizacao", new WsUrls("https://nfce.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx", "https://nfce-homologacao.svrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx"));
        URLS_NFCE_POR_UF.put("SVRS", nfceSvrsUrls);

        Map<String, WsUrls> nfceAcUrls = new HashMap<>();
        nfceAcUrls.put("QRCode", new WsUrls("http://www.sefaznet.ac.gov.br/nfce/qrcode", "http://www.hml.sefaznet.ac.gov.br/nfce/qrcode"));
        nfceAcUrls.put("ConsultaNFCe", new WsUrls("www.sefaznet.ac.gov.br/nfce/consulta", "www.sefaznet.ac.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.ACRE.getUf(), nfceAcUrls);

        Map<String, WsUrls> nfceAlUrls = new HashMap<>();
        nfceAlUrls.put("QRCode", new WsUrls("http://nfce.sefaz.al.gov.br/QRCode/consultarNFCe.jsp", "http://nfce.sefaz.al.gov.br/QRCode/consultarNFCe.jsp"));
        nfceAlUrls.put("ConsultaNFCe", new WsUrls("www.sefaz.al.gov.br/nfce/consulta", "www.sefaz.al.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.ALAGOAS.getUf(), nfceAlUrls);

        Map<String, WsUrls> nfceApUrls = new HashMap<>();
        nfceApUrls.put("QRCode", new WsUrls("https://www.sefaz.ap.gov.br/nfce/nfcep.php", "https://www.sefaz.ap.gov.br/nfcehml/nfce.php"));
        nfceApUrls.put("ConsultaNFCe", new WsUrls("www.sefaz.ap.gov.br/nfce/consulta", "www.sefaz.ap.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.AMAPA.getUf(), nfceApUrls);

        Map<String, WsUrls> nfceBaUrls = new HashMap<>();
        nfceBaUrls.put("QRCode", new WsUrls("http://nfe.sefaz.ba.gov.br/servicos/nfce/modulos/geral/NFCEC_consulta_chave_acesso.aspx", "http://hnfe.sefaz.ba.gov.br/servicos/nfce/modulos/geral/NFCEC_consulta_chave_acesso.aspx"));
        nfceBaUrls.put("ConsultaNFCe", new WsUrls("www.sefaz.ba.gov.br/nfce/consulta", "http://hinternet.sefaz.ba.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.BAHIA.getUf(), nfceBaUrls);

        Map<String, WsUrls> nfceDfUrls = new HashMap<>();
        nfceDfUrls.put("QRCode", new WsUrls("https://www.fazenda.df.gov.br/nfce/qrcode", "https://www.fazenda.df.gov.br/nfce/qrcode"));
        nfceDfUrls.put("ConsultaNFCe", new WsUrls("www.fazenda.df.gov.br/nfce/consulta", "www.fazenda.df.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.DISTRITO_FEDERAL.getUf(), nfceDfUrls);

        Map<String, WsUrls> nfceEsUrls = new HashMap<>();
        nfceEsUrls.put("QRCode", new WsUrls("http://app.sefaz.es.gov.br/ConsultaNFCe/qrcode.aspx", "http://homologacao.sefaz.es.gov.br/ConsultaNFCe/qrcode.aspx"));
        nfceEsUrls.put("ConsultaNFCe", new WsUrls("www.sefaz.es.gov.br/nfce/consulta", "www.sefaz.es.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.ESPIRITO_SANTO.getUf(), nfceEsUrls);

        Map<String, WsUrls> nfceMaUrls = new HashMap<>();
        nfceMaUrls.put("QRCode", new WsUrls("http://www.nfce.sefaz.ma.gov.br/portal/consultarNFCe.jsp", "http://www.hom.nfce.sefaz.ma.gov.br/portal/consultarNFCe.jsp"));
        nfceMaUrls.put("ConsultaNFCe", new WsUrls("www.sefaz.ma.gov.br/nfce/consulta", "www.sefaz.ma.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.MARANHAO.getUf(), nfceMaUrls);

        Map<String, WsUrls> nfcePaUrls = new HashMap<>();
        nfcePaUrls.put("QRCode", new WsUrls("https://appnfc.sefa.pa.gov.br/portal/view/consultas/nfce/nfceForm.seam", "https://appnfc.sefa.pa.gov.br/portal-homologacao/view/consultas/nfce/nfceForm.seam"));
        nfcePaUrls.put("ConsultaNFCe", new WsUrls("wwww.sefa.pa.gov.br/nfce/consulta", "www.sefa.pa.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.PARA.getUf(), nfcePaUrls);

        Map<String, WsUrls> nfcePbUrls = new HashMap<>();
        nfcePbUrls.put("QRCode", new WsUrls("http://www.sefaz.pb.gov.br/nfce", "http://www.sefaz.pb.gov.br/nfcehom"));
        nfcePbUrls.put("ConsultaNFCe", new WsUrls("www.sefaz.pb.gov.br/nfce/consulta", "www.sefaz.pb.gov.br/nfcehom"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.PARAIBA.getUf(), nfcePbUrls);

        Map<String, WsUrls> nfcePeUrls = new HashMap<>();
        nfcePeUrls.put("QRCode", new WsUrls("http://nfce.sefaz.pe.gov.br/nfce-web/consultarNFCe", "http://nfcehomolog.sefaz.pe.gov.br/nfce-web/consultarNFCe"));
        nfcePeUrls.put("ConsultaNFCe", new WsUrls("nfce.sefaz.pe.gov.br/nfce/consulta", "nfce.sefaz.pe.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.PERNAMBUCO.getUf(), nfcePeUrls);

        Map<String, WsUrls> nfcePiUrls = new HashMap<>();
        nfcePiUrls.put("QRCode", new WsUrls("http://www.sefaz.pi.gov.br/nfce/qrcode", "http://www.sefaz.pi.gov.br/nfce/qrcode"));
        nfcePiUrls.put("ConsultaNFCe", new WsUrls("www.sefaz.pi.gov.br/nfce/consulta", "www.sefaz.pi.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.PIAUI.getUf(), nfcePiUrls);

        Map<String, WsUrls> nfceRjUrls = new HashMap<>();
        nfceRjUrls.put("QRCode", new WsUrls("https://consultadfe.fazenda.rj.gov.br/consultaNFCe/QRCode", "https://consultadfe.fazenda.rj.gov.br/consultaNFCe/QRCode"));
        nfceRjUrls.put("ConsultaNFCe", new WsUrls("www.fazenda.rj.gov.br/nfce/consulta", "www.fazenda.rj.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.RIO_DE_JANEIRO.getUf(), nfceRjUrls);

        Map<String, WsUrls> nfceRnUrls = new HashMap<>();
        nfceRnUrls.put("QRCode", new WsUrls("http://nfce.set.rn.gov.br/consultarNFCe.aspx", "http://hom.nfce.set.rn.gov.br/consultarNFCe.aspx"));
        nfceRnUrls.put("ConsultaNFCe", new WsUrls("www.set.rn.gov.br/nfce/consulta", "www.set.rn.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.RIO_GRANDE_DO_NORTE.getUf(), nfceRnUrls);

        Map<String, WsUrls> nfceRoUrls = new HashMap<>();
        nfceRoUrls.put("QRCode", new WsUrls("http://www.nfce.sefin.ro.gov.br/consultanfce/consulta.jsp", "http://www.nfce.sefin.ro.gov.br/consultanfce/consulta.jsp"));
        nfceRoUrls.put("ConsultaNFCe", new WsUrls("=www.sefin.ro.gov.br/nfce/consulta", "=www.sefin.ro.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.RONDONIA.getUf(), nfceRoUrls);

        Map<String, WsUrls> nfceRrUrls = new HashMap<>();
        nfceRrUrls.put("QRCode", new WsUrls("https://www.sefaz.rr.gov.br/nfce/servlet/qrcode", "http://200.174.88.103:8080/nfce/servlet/qrcode"));
        nfceRrUrls.put("ConsultaNFCe", new WsUrls("www.sefaz.rr.gov.br/nfce/consulta", "www.sefaz.rr.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.RORAIMA.getUf(), nfceRrUrls);

        Map<String, WsUrls> nfceScUrls = new HashMap<>();
        nfceScUrls.put("QRCode", new WsUrls("https://sat.sef.sc.gov.br/nfce/consulta", "https://hom.sat.sef.sc.gov.br/nfce/consulta"));
        nfceScUrls.put("ConsultaNFCe", new WsUrls("https://sat.sef.sc.gov.br/nfce/consulta", "https://hom.sat.sef.sc.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.SANTA_CATARINA.getUf(), nfceScUrls);

        Map<String, WsUrls> nfceSeUrls = new HashMap<>();
        nfceSeUrls.put("QRCode", new WsUrls("http://www.nfce.se.gov.br/portal/consultarNFCe.jsp", "http://www.hom.nfe.se.gov.br/portal/consultarNFCe.jsp"));
        nfceSeUrls.put("ConsultaNFCe", new WsUrls("http://www.nfce.se.gov.br/nfce/consulta", "http://www.hom.nfe.se.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.SERGIPE.getUf(), nfceSeUrls);

        Map<String, WsUrls> nfceToUrls = new HashMap<>();
        nfceToUrls.put("QRCode", new WsUrls("http://www.sefaz.to.gov.br/nfce/qrcode", "http://homologacao.sefaz.to.gov.br/nfce/qrcode"));
        nfceToUrls.put("ConsultaNFCe", new WsUrls("www.sefaz.to.gov.br/nfce/consulta", "http://homologacao.sefaz.to.gov.br/nfce/consulta.jsf"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.TOCANTINS.getUf(), nfceToUrls);

        Map<String, WsUrls> nfceAmUrls = new HashMap<>();
        nfceAmUrls.put("RecepcaoEvento", new WsUrls("https://nfce.sefaz.am.gov.br/nfce-services/services/RecepcaoEvento4", "https://homnfce.sefaz.am.gov.br/nfce-services-nac/services/RecepcaoEvento4"));
        nfceAmUrls.put("Inutilizacao", new WsUrls("https://nfce.sefaz.am.gov.br/nfce-services/services/NfeInutilizacao4", "https://homnfce.sefaz.am.gov.br/nfce-services-nac/services/NfeInutilizacao4"));
        nfceAmUrls.put("ConsultaProtocolo", new WsUrls("https://nfce.sefaz.am.gov.br/nfce-services/services/NfeConsulta4", "https://homnfce.sefaz.am.gov.br/nfce-services-nac/services/NfeConsulta4"));
        nfceAmUrls.put("NFeStatusServico", new WsUrls("https://nfce.sefaz.am.gov.br/nfce-services/services/NfeStatusServico4", "https://homnfce.sefaz.am.gov.br/nfce-services-nac/services/NfeStatusServico4"));
        nfceAmUrls.put("NFeAutorizacao", new WsUrls("https://nfce.sefaz.am.gov.br/nfce-services/services/NfeAutorizacao4", "https://homnfce.sefaz.am.gov.br/nfce-services-nac/services/NfeAutorizacao4"));
        nfceAmUrls.put("RetAutorizacao", new WsUrls("https://nfce.sefaz.am.gov.br/nfce-services/services/NfeRetAutorizacao4", "https://homnfce.sefaz.am.gov.br/nfce-services-nac/services/RecepcaoEvento4"));
        nfceAmUrls.put("QRCode", new WsUrls("http://sistemas.sefaz.am.gov.br/nfceweb/consultarNFCe.jsp", "https://sistemas.sefaz.am.gov.br/nfceweb-hom/consultarNFCe.jsp"));
        nfceAmUrls.put("ConsultaNFCe", new WsUrls("www.sefaz.am.gov.br/nfce/consulta", "https://sistemas.sefaz.am.gov.br/nfceweb-hom/formConsulta.do"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.AMAZONAS.getUf(), nfceAmUrls);

        Map<String, WsUrls> nfceCeUrls = new HashMap<>();
        nfceCeUrls.put("RecepcaoEvento", new WsUrls("https://nfce.sefaz.ce.gov.br/nfce4/services/NFeRecepcaoEvento4?WSDL", "https://nfceh.sefaz.ce.gov.br/nfce4/services/NFeRecepcaoEvento4?WSDL"));
        nfceCeUrls.put("Inutilizacao", new WsUrls("https://nfce.sefaz.ce.gov.br/nfce4/services/NFeInutilizacao4?WSDL", "https://nfceh.sefaz.ce.gov.br/nfce4/services/NFeInutilizacao4?WSDL"));
        nfceCeUrls.put("ConsultaProtocolo", new WsUrls("https://nfce.sefaz.ce.gov.br/nfce4/services/NFeConsultaProtocolo4?WSDL", "https://nfceh.sefaz.ce.gov.br/nfce4/services/NFeConsultaProtocolo4?WSDL"));
        nfceCeUrls.put("NFeStatusServico", new WsUrls("https://nfce.sefaz.ce.gov.br/nfce4/services/NFeStatusServico4?WSDL", "https://nfceh.sefaz.ce.gov.br/nfce4/services/NFeStatusServico4?WSDL"));
        nfceCeUrls.put("NFeAutorizacao", new WsUrls("https://nfce.sefaz.ce.gov.br/nfce4/services/NFeAutorizacao4?WSDL", "https://nfceh.sefaz.ce.gov.br/nfce4/services/NFeAutorizacao4?WSDL"));
        nfceCeUrls.put("RetAutorizacao", new WsUrls("https://nfce.sefaz.ce.gov.br/nfce4/services/NFeRetAutorizacao4?WSDL", "https://nfceh.sefaz.ce.gov.br/nfce4/services/NFeRetAutorizacao4?WSDL"));
        nfceCeUrls.put("QRCode", new WsUrls("http://nfce.sefaz.ce.gov.br/pages/ShowNFCe.html", "http://nfceh.sefaz.ce.gov.br/pages/ShowNFCe.html"));
        nfceCeUrls.put("ConsultaNFCe", new WsUrls("www.sefaz.ce.gov.br/nfce/consulta", "www.sefaz.ce.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.CEARA.getUf(), nfceCeUrls);

        Map<String, WsUrls> nfceGoUrls = new HashMap<>();
        nfceGoUrls.put("RecepcaoEvento", new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/NFeRecepcaoEvento4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeRecepcaoEvento4?wsdl"));
        nfceGoUrls.put("Inutilizacao", new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/NFeInutilizacao4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeInutilizacao4?wsdl"));
        nfceGoUrls.put("ConsultaProtocolo", new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/NFeConsultaProtocolo4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeConsultaProtocolo4?wsdl"));
        nfceGoUrls.put("NFeStatusServico", new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/NFeStatusServico4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeStatusServico4?wsdl"));
        nfceGoUrls.put("NFeAutorizacao", new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/NFeAutorizacao4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeAutorizacao4?wsdl"));
        nfceGoUrls.put("RetAutorizacao", new WsUrls("https://nfe.sefaz.go.gov.br/nfe/services/NFeRetAutorizacao4?wsdl", "https://homolog.sefaz.go.gov.br/nfe/services/NFeRetAutorizacao4?wsdl"));
        nfceGoUrls.put("QRCode", new WsUrls("http://nfe.sefaz.go.gov.br/nfeweb/sites/nfce/danfeNFCe", "http://homolog.sefaz.go.gov.br/nfeweb/sites/nfce/danfeNFCe"));
        nfceGoUrls.put("ConsultaNFCe", new WsUrls("http://www.sefaz.go.gov.br/nfce/consulta", "http://www.sefaz.go.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.GOIAS.getUf(), nfceGoUrls);

        Map<String, WsUrls> nfceMgUrls = new HashMap<>();
        nfceMgUrls.put("RecepcaoEvento", new WsUrls("https://nfce.fazenda.mg.gov.br/nfce/services/NFeRecepcaoEvento4", "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeRecepcaoEvento4"));
        nfceMgUrls.put("Inutilizacao", new WsUrls("https://nfce.fazenda.mg.gov.br/nfce/services/NFeInutilizacao4", "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeInutilizacao4"));
        nfceMgUrls.put("ConsultaProtocolo", new WsUrls("https://nfce.fazenda.mg.gov.br/nfce/services/NFeConsultaProtocolo4", "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeConsultaProtocolo4"));
        nfceMgUrls.put("NFeStatusServico", new WsUrls("https://nfce.fazenda.mg.gov.br/nfce/services/NFeStatusServico4", "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeStatusServico4"));
        nfceMgUrls.put("NFeAutorizacao", new WsUrls("https://nfce.fazenda.mg.gov.br/nfce/services/NFeAutorizacao4", "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeAutorizacao4"));
        nfceMgUrls.put("RetAutorizacao", new WsUrls("https://nfce.fazenda.mg.gov.br/nfce/services/NFeRetAutorizacao4", "https://hnfce.fazenda.mg.gov.br/nfce/services/NFeRetAutorizacao4"));
        nfceMgUrls.put("QRCode", new WsUrls("https://portalsped.fazenda.mg.gov.br/portalnfce/sistema/qrcode.xhtml", "https://portalsped.fazenda.mg.gov.br/portalnfce/sistema/qrcode.xhtml"));
        nfceMgUrls.put("ConsultaNFCe", new WsUrls("https://portalsped.fazenda.mg.gov.br/portalnfce", "https://hportalsped.fazenda.mg.gov.br/portalnfce"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.MINAS_GERAIS.getUf(), nfceMgUrls);

        Map<String, WsUrls> nfceMsUrls = new HashMap<>();
        nfceMsUrls.put("RecepcaoEvento", new WsUrls("https://nfce.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4", "https://hom.nfce.sefaz.ms.gov.br/ws/NFeRecepcaoEvento4"));
        nfceMsUrls.put("Inutilizacao", new WsUrls("https://nfce.sefaz.ms.gov.br/ws/NFeInutilizacao4", "https://hom.nfce.sefaz.ms.gov.br/ws/NFeInutilizacao4"));
        nfceMsUrls.put("ConsultaProtocolo", new WsUrls("https://nfce.sefaz.ms.gov.br/ws/NFeConsultaProtocolo44", "https://hom.nfce.sefaz.ms.gov.br/ws/NFeConsultaProtocolo4"));
        nfceMsUrls.put("NFeStatusServico", new WsUrls("https://nfce.sefaz.ms.gov.br/ws/NFeStatusServico4", "https://hom.nfce.sefaz.ms.gov.br/ws/NFeStatusServico4"));
        nfceMsUrls.put("NFeAutorizacao", new WsUrls("https://nfce.sefaz.ms.gov.br/ws/NFeAutorizacao4", "https://hom.nfce.sefaz.ms.gov.br/ws/NFeAutorizacao4"));
        nfceMsUrls.put("RetAutorizacao", new WsUrls("https://nfce.sefaz.ms.gov.br/ws/NFeRetAutorizacao4", "https://hom.nfce.sefaz.ms.gov.br/ws/NFeRetAutorizacao4"));
        nfceMsUrls.put("QRCode", new WsUrls("http://www.dfe.ms.gov.br/nfce/qrcode", "http://www.dfe.ms.gov.br/nfce/qrcode"));
        nfceMsUrls.put("ConsultaNFCe", new WsUrls("http://www.dfe.ms.gov.br/nfce/consulta", "http://www.dfe.ms.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.MATO_GROSSO_DO_SUL.getUf(), nfceMsUrls);

        Map<String, WsUrls> nfceMtUrls = new HashMap<>();
        nfceMtUrls.put("RecepcaoEvento", new WsUrls("https://nfce.sefaz.mt.gov.br/nfcews/services/RecepcaoEvento4", "https://homologacao.sefaz.mt.gov.br/nfcews/services/RecepcaoEvento4"));
        nfceMtUrls.put("Inutilizacao", new WsUrls("https://nfce.sefaz.mt.gov.br/nfcews/services/NfeInutilizacao4", "https://homologacao.sefaz.mt.gov.br/nfcews/services/NfeInutilizacao4"));
        nfceMtUrls.put("ConsultaProtocolo", new WsUrls("https://nfce.sefaz.mt.gov.br/nfcews/services/NfeConsulta4", "https://homologacao.sefaz.mt.gov.br/nfcews/services/NfeConsulta4"));
        nfceMtUrls.put("NFeStatusServico", new WsUrls("https://nfce.sefaz.mt.gov.br/nfcews/services/NfeStatusServico4", "https://homologacao.sefaz.mt.gov.br/nfcews/services/NfeStatusServico4"));
        nfceMtUrls.put("NFeAutorizacao", new WsUrls("https://nfce.sefaz.mt.gov.br/nfcews/services/NfeAutorizacao4", "https://homologacao.sefaz.mt.gov.br/nfcews/services/NfeAutorizacao4"));
        nfceMtUrls.put("RetAutorizacao", new WsUrls("https://nfce.sefaz.mt.gov.br/nfcews/services/NfeRetAutorizacao4", "https://homologacao.sefaz.mt.gov.br/nfcews/services/NfeRetAutorizacao4"));
        nfceMtUrls.put("QRCode", new WsUrls("http://www.sefaz.mt.gov.br/nfce/consultanfce", "http://homologacao.sefaz.mt.gov.br/nfce/consultanfce"));
        nfceMtUrls.put("ConsultaNFCe", new WsUrls("http://www.sefaz.mt.gov.br/nfce/consultanfce", "http://homologacao.sefaz.mt.gov.br/nfce/consultanfce"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.MATO_GROSSO.getUf(), nfceMtUrls);

        Map<String, WsUrls> nfcePrUrls = new HashMap<>();
        nfcePrUrls.put("RecepcaoEvento", new WsUrls("https://nfce.sefa.pr.gov.br/nfce/NFeRecepcaoEvento4?wsdl", "https://homologacao.nfce.sefa.pr.gov.br/nfce/NFeRecepcaoEvento4?wsdl"));
        nfcePrUrls.put("Inutilizacao", new WsUrls("https://nfce.sefa.pr.gov.br/nfce/NFeInutilizacao4?wsdl", "https://homologacao.nfce.sefa.pr.gov.br/nfce/NFeInutilizacao4?wsdl"));
        nfcePrUrls.put("ConsultaProtocolo", new WsUrls("https://nfce.sefa.pr.gov.br/nfce/NFeConsultaProtocolo4?wsdl", "https://homologacao.nfce.sefa.pr.gov.br/nfce/NFeConsultaProtocolo4?wsdl"));
        nfcePrUrls.put("NFeStatusServico", new WsUrls("https://nfce.sefa.pr.gov.br/nfce/NFeStatusServico4?wsdl", "https://homologacao.nfce.sefa.pr.gov.br/nfce/NFeStatusServico4?wsdl"));
        nfcePrUrls.put("NFeAutorizacao", new WsUrls("https://nfce.sefa.pr.gov.br/nfce/NFeAutorizacao4?wsdl", "https://homologacao.nfce.sefa.pr.gov.br/nfce/NFeAutorizacao4?wsdl"));
        nfcePrUrls.put("RetAutorizacao", new WsUrls("https://nfce.sefa.pr.gov.br/nfce/NFeRetAutorizacao4?wsdl", "https://homologacao.nfce.sefa.pr.gov.br/nfce/NFeRetAutorizacao4?wsdl"));
        nfcePrUrls.put("QRCode", new WsUrls("http://www.fazenda.pr.gov.br/nfce/qrcode", "http://www.fazenda.pr.gov.br/nfce/qrcode"));
        nfcePrUrls.put("ConsultaNFCe", new WsUrls("http://www.fazenda.pr.gov.br/nfce/consulta", "http://www.fazenda.pr.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.PARANA.getUf(), nfcePrUrls);

        Map<String, WsUrls> nfceRsUrls = new HashMap<>();
        nfceRsUrls.put("RecepcaoEvento", new WsUrls("https://nfce.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx", "https://nfce-homologacao.sefazrs.rs.gov.br/ws/recepcaoevento/recepcaoevento4.asmx"));
        nfceRsUrls.put("Inutilizacao", new WsUrls("https://nfce.sefazrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx", "https://nfce-homologacao.sefazrs.rs.gov.br/ws/nfeinutilizacao/nfeinutilizacao4.asmx"));
        nfceRsUrls.put("ConsultaProtocolo", new WsUrls("https://nfce.sefazrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx", "https://nfce-homologacao.sefazrs.rs.gov.br/ws/NfeConsulta/NfeConsulta4.asmx"));
        nfceRsUrls.put("NFeStatusServico", new WsUrls("https://nfce.sefazrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx", "https://nfce-homologacao.sefazrs.rs.gov.br/ws/NfeStatusServico/NfeStatusServico4.asmx"));
        nfceRsUrls.put("NFeAutorizacao", new WsUrls("https://nfce.sefazrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx", "https://nfce-homologacao.sefazrs.rs.gov.br/ws/NfeAutorizacao/NFeAutorizacao4.asmx"));
        nfceRsUrls.put("RetAutorizacao", new WsUrls("https://nfce.sefazrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx", "https://nfce-homologacao.sefazrs.rs.gov.br/ws/NfeRetAutorizacao/NFeRetAutorizacao4.asmx"));
        nfceRsUrls.put("QRCode", new WsUrls("https://www.sefaz.rs.gov.br/NFCE/NFCE-COM.aspx", "https://www.sefaz.rs.gov.br/NFCE/NFCE-COM.aspx"));
        nfceRsUrls.put("ConsultaNFCe", new WsUrls("www.sefaz.rs.gov.br/nfce/consulta", "www.sefaz.rs.gov.br/nfce/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.RIO_GRANDE_DO_SUL.getUf(), nfceRsUrls);

        Map<String, WsUrls> nfceSpUrls = new HashMap<>();
        nfceSpUrls.put("RecepcaoEvento", new WsUrls("https://nfce.fazenda.sp.gov.br/ws/NFeRecepcaoEvento4.asmx", "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeRecepcaoEvento4.asmx"));
        nfceSpUrls.put("Inutilizacao", new WsUrls("https://nfce.fazenda.sp.gov.br/ws/NFeInutilizacao4.asmx", "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeInutilizacao4.asmx"));
        nfceSpUrls.put("ConsultaProtocolo", new WsUrls("https://nfce.fazenda.sp.gov.br/ws/NFeConsultaProtocolo4.asmx", "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeConsultaProtocolo4.asmx"));
        nfceSpUrls.put("NFeStatusServico", new WsUrls("https://nfce.fazenda.sp.gov.br/ws/NFeStatusServico4.asmx", "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeStatusServico4.asmx"));
        nfceSpUrls.put("NFeAutorizacao", new WsUrls("https://nfce.fazenda.sp.gov.br/ws/NFeAutorizacao4.asmx", "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeAutorizacao4.asmx"));
        nfceSpUrls.put("RetAutorizacao", new WsUrls("https://nfce.fazenda.sp.gov.br/ws/NFeRetAutorizacao4.asmx", "https://homologacao.nfce.fazenda.sp.gov.br/ws/NFeRetAutorizacao4.asmx"));
        nfceSpUrls.put("QRCode", new WsUrls("https://www.nfce.fazenda.sp.gov.br/qrcode", "https://www.homologacao.nfce.fazenda.sp.gov.br/qrcode"));
        nfceSpUrls.put("ConsultaNFCe", new WsUrls("https://www.nfce.fazenda.sp.gov.br/consultaa", "https://www.homologacao.nfce.fazenda.sp.gov.br/consulta"));
        URLS_NFCE_POR_UF.put(EstadoBrasil.SAO_PAULO.getUf(), nfceSpUrls);
    }

    public static String getUrlNFe(String tipoServico, String uf, String tipoAmbiente, String tipoEmissao) {
        Map<String, WsUrls> urlsPorServico = URLS_NFE_POR_TIPO_EMISSAO.get(tipoEmissao);
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

    @SuppressWarnings({"deprecation", "unused"})
    public String consulta(String url, String xml, Certificado certificado, String soapAction) {
        logger.info("Iniciando consulta");

        HttpsURLConnection conn = null;
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            try (FileInputStream fis = new FileInputStream(certificado.getArquivo())) {
                keyStore.load(fis, certificado.getSenha().toCharArray());
            }
            logger.info("keyStore: {}", keyStore);

            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (InputStream fis = getClass().getClassLoader().getResourceAsStream("cacert")) {
                trustStore.load(fis, "changeit".toCharArray());
            }
            logger.info("trustStore: {}", trustStore);

            KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            keyManagerFactory.init(keyStore, certificado.getSenha().toCharArray());
            logger.info("keyManagerFactory: {}", keyManagerFactory);

            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(trustStore);
            logger.info("trustManagerFactory: {}", trustManagerFactory);

            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), new SecureRandom());
            logger.info("sslContext: {}", sslContext);

            URL urlConsulta = new URL(url);
            conn = (HttpsURLConnection) urlConsulta.openConnection();
            logger.info("urlConsulta: {} | conn: {}", sslContext, conn);

            conn.setSSLSocketFactory(sslContext.getSocketFactory());
            conn.setHostnameVerifier((hostname, session) -> true);
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("SOAPAction", "\"" + soapAction + "\"");
            logger.info("soapAction: {}", soapAction);
            conn.setRequestProperty("Content-Type", "application/soap+xml; charset=utf-8");
            conn.setRequestProperty("Content-length", Integer.toString(xml.length()));

            OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
            writer.write(xml);
            writer.flush();

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            writer.close();
            reader.close();
            conn.disconnect();

            String xmlResp = response.toString();
            logger.info("xmlResp de response: {}", xmlResp);

            if (conn.getResponseCode() == 200) {// OK
                int index = xmlResp.indexOf("<?xml");
                if (index != -1) {
                    xmlResp = xmlResp.substring(index);
                }
            } else {
                String responseStr = montaMotivo(conn);
                throw new FiscalException("Erro", conn.getResponseCode() + ";" + responseStr);
            }

            logger.info("xmlResp: {}", xmlResp);
            return xmlResp;
        } catch (FiscalException e) {
            logger.error("Resposta de SEFAZ com status diferente de 200: {}", e.getMessage());
            return montaRetConsStatServFiscalException(xml, e.getMessage());
        } catch (Exception e) {
            StringBuilder errorResponse = new StringBuilder();

            if (conn != null && conn.getErrorStream() != null) {
                try (BufferedReader errorReader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                    String line;
                    while ((line = errorReader.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    logger.error("Resposta de erro da SEFAZ: {}", errorResponse);
                } catch (IOException ioEx) {
                    logger.warn("Erro ao ler a resposta de erro da SEFAZ: {}", ioEx.getMessage(), ioEx);
                }
            }

            String message = !errorResponse.isEmpty() ? errorResponse.toString() : e.getMessage();
            logger.error("Exceção ao consultar SEFAZ: {}", message, e);

            return montaRetConsStatServ(xml, "500", message);
        }

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
		String respostaConsulta = FiscalUtil.pegaTag3(xml, "consStatServ");

		String tpAmb   = getTagValueOrEmpty(respostaConsulta, "tpAmb");
		String verAplic = getTagValueOrEmpty(respostaConsulta, "verAplic");
		String cUF     = getTagValueOrEmpty(respostaConsulta, "cUF");
		String tMed    = getTagValueOrEmpty(respostaConsulta, "tMed");

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

    public String removeXMLNamespaceAndVersionCTe(String xml) {
        return xml.replaceFirst("xmlns=\"http://www.portalfiscal.inf.br/cte\" versao=\"4.00\">", "");
    }

    public String pegaTag(String xml, String tag) {
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

    public String pegaTag2(String xml, String tag) {
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

    public String removeXMLTag(String conteudoXml) {
        if (conteudoXml == null) {
            return null;
        }

        if (conteudoXml.startsWith("<?xml")) {
            int endIndex = conteudoXml.indexOf("?>");
            if (endIndex != -1) {
                conteudoXml = conteudoXml.substring(endIndex + 2).trim();
            }
        }

        return conteudoXml;
    }

    @SuppressWarnings("unchecked")
    public <T> T convertXmlToObject(String conteudoXml, Class<T> clazz) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(clazz);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        StringReader reader = new StringReader(conteudoXml);
        return (T) unmarshaller.unmarshal(reader);
    }

    public String objectToXmlCTe(Object objeto) throws JAXBException {
        return objectToXmlCTe(objeto, null, null);
    }

    private <T> String objectToXmlCTe(T objeto, Class<T> clazz, String nomeElemento) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(objeto.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.encoding", "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        Result result = new StreamResult(sw);

        if (nomeElemento != null) {
            marshaller.marshal(new JAXBElement<>(
                    new QName("http://www.portalfiscal.inf.br/cte", nomeElemento),
                    clazz, objeto), result);
        } else {
            marshaller.marshal(objeto, result);
        }

        return replacesCte("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + sw);
    }

    private static String replacesCte(String xml) {
        return xml.replace("ns2:", "")
                .replace("ns3:", "")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("<Signature>", "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">")
                .replace(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "")
                .replace(" xmlns:ns3=\"http://www.portalfiscal.inf.br/cte\"", "")
                .replace(" xmlns:ns2=\"http://www.portalfiscal.inf.br/cte\"", "")
                .replace(" xmlns=\"\"", "");
    }

    public String objectToXmlMDFe(Object objeto) throws JAXBException {
        return objectToXmlMDFe(objeto, null, null);
    }

    private <T> String objectToXmlMDFe(T objeto, Class<T> clazz, String nomeElemento) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(objeto.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.encoding", "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        Result result = new StreamResult(sw);

        if (nomeElemento != null) {
            marshaller.marshal(new JAXBElement<>(
                    new QName("http://www.portalfiscal.inf.br/mdfe", nomeElemento),
                    clazz, objeto), result);
        } else {
            marshaller.marshal(objeto, result);
        }

        return replacesMDFe("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + sw);
    }

    private static String replacesMDFe(String xml) {
        return xml.replace("ns2:", "")
                .replace("ns3:", "")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("<Signature>", "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">")
                .replace(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "")
                .replace(" xmlns:ns3=\"http://www.portalfiscal.inf.br/mdfe\"", "")
                .replace(" xmlns:ns2=\"http://www.portalfiscal.inf.br/mdfe\"", "")
                .replace("xmlns:ns4=\"http://www.portalfiscal.inf.br/mdfe\"", "")
                .replace("xmlns:ns3=\"http://www.w3.org/2000/09/xmldsig#\"", "")
                .replace("xmlns:ns2=\"http://www.portalfiscal.inf.br/cte\"", "")
                .replace(" xmlns=\"\"", "");
    }

    public String objectToXml(Object object) {
        try {
            JAXBContext context = JAXBContext.newInstance(object.getClass());

            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(object, writer);

            return writer.toString().replaceAll("ns1:", "").replaceAll("standalone=\"yes\"", "").replaceAll(":ns1", "").replaceAll("ns2:", "").replaceAll("standalone=\"yes\"", "").replaceAll(":ns2", "").replaceAll("ns3:", "").replaceAll(":ns3", "");

        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }


    public String objectToXmlNFe(Object objeto) throws JAXBException {
        return objectToXmlNFe(objeto, null, null);
    }

    private <T> String objectToXmlNFe(T objeto, Class<T> clazz, String nomeElemento) throws JAXBException {

        JAXBContext context = JAXBContext.newInstance(objeto.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.encoding", "UTF-8");
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.FALSE);
        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);
        StringWriter sw = new StringWriter();
        Result result = new StreamResult(sw);

        if (nomeElemento != null) {
            marshaller.marshal(new JAXBElement<>(
                    new QName("http://www.portalfiscal.inf.br/nfe", nomeElemento),
                    clazz, objeto), result);
        } else {
            marshaller.marshal(objeto, result);
        }

        return replacesNFe("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + sw);
    }

    private static String replacesNFe(String xml) {
        return xml.replace("ns2:", "")
                .replace("ns1:", "")
                .replace("ns3:", "")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("standalone=\"yes\"", "")
                .replace("<Signature>", "<Signature xmlns=\"http://www.w3.org/2000/09/xmldsig#\">")
                .replace(" xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "")
                .replace(" xmlns:ns3=\"http://www.portalfiscal.inf.br/nfe\"", "")
                .replace(" xmlns:ns2=\"http://www.portalfiscal.inf.br/nfe\"", "")
                .replace("xmlns:ns4=\"http://www.portalfiscal.inf.br/nfe\"", "")
                .replace("xmlns:ns3=\"http://www.w3.org/2000/09/xmldsig#\"", "")
                .replace("xmlns:ns2=\"http://www.portalfiscal.inf.br/nfe\"", "")
                .replace(" xmlns=\"\"", "");
    }


    public String objectToXml(NFeProc nfeProc) {
        try {
            JAXBContext context = JAXBContext.newInstance(NFeProc.class);

            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(nfeProc, writer);

            return writer.toString().replaceAll("ns1:", "").replaceAll("standalone=\"yes\"", "").replaceAll(":ns1", "").replaceAll("ns2:", "").replaceAll("standalone=\"yes\"", "").replaceAll(":ns2", "").replaceAll("ns3:", "").replaceAll(":ns3", "");

        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String objectToXmlNFe(NFe nfe) {
        try {
            JAXBContext context = JAXBContext.newInstance(NFe.class);

            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            StringWriter writer = new StringWriter();
            marshaller.marshal(nfe, writer);

            return writer.toString().replaceAll("ns1:", "").replaceAll("standalone=\"yes\"", "").replaceAll(":ns1", "").replaceAll("ns2:", "").replaceAll("standalone=\"yes\"", "").replaceAll(":ns2", "").replaceAll("ns3:", "").replaceAll(":ns3", "");

        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String strZero(int value, int length) {
        return String.format("%0" + length + "d", value);
    }

    public String addNamespaceToXMLNFe(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));

        Element root = doc.getDocumentElement();
        root.setAttribute("xmlns", "http://www.portalfiscal.inf.br/nfe");

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        transformer.transform(source, new StreamResult(writer));

        return writer.toString();
    }

    public String addNamespaceToXMLCTe(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));

        Element root = doc.getDocumentElement();
        root.setAttribute("xmlns", "http://www.portalfiscal.inf.br/cte");

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        transformer.transform(source, new StreamResult(writer));

        return writer.toString();
    }

    public String addNamespaceToXMLMDFe(String xmlContent) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));

        Element root = doc.getDocumentElement();
        root.setAttribute("xmlns", "http://www.portalfiscal.inf.br/mdfe");

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        transformer.transform(source, new StreamResult(writer));

        return writer.toString();
    }

    public String xmlSoapCte(String xml, String servico) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap12:Body>" +
                "<cteDadosMsg xmlns=\"http://www.portalfiscal.inf.br/cte/wsdl/" + servico + "\">" +
                xml +
                "</cteDadosMsg>" +
                "</soap12:Body>" +
                "</soap12:Envelope>";
    }

    public String xmlSoapMdfe(String xml, String soapAction, String uf) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
                "<soap12:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap12=\"http://www.w3.org/2003/05/soap-envelope\">" +
                "<soap12:Header>" +
                "<mdfeCabecMsg  xmlns=\"http://www.portalfiscal.inf.br/mdfe/wsdl/" + soapAction + "\">" +
                "<cUF>" + ufToCodUf(uf) + "</cUF>" +
                "<versaoDados>3.00</versaoDados>" +
                "</mdfeCabecMsg>" +
                "</soap12:Header>" +
                "<soap12:Body>" +
                "<mdfeDadosMsg xmlns=\"http://www.portalfiscal.inf.br/mdfe/wsdl/" + soapAction + "\">" +
                xml +
                "</mdfeDadosMsg>" +
                "</soap12:Body>" +
                "</soap12:Envelope>";
    }

    public String getUrlCTe(String tipoServico, String uf, Integer tipoAmbienteInt, String tipoEmissao) {
        String tipoAmbiente = String.valueOf(tipoAmbienteInt);
        Map<String, WsUrls> urlsPorServico;

        if (tipoEmissao != null && !tipoEmissao.isEmpty()) {
            urlsPorServico = URLS_CTE_POR_TIPO_EMISSAO.get(tipoEmissao);
            if (urlsPorServico != null) {
                WsUrls urls = urlsPorServico.get(tipoServico);
                if (urls != null) {
                    return urls.get(tipoAmbiente);
                }
            }
        }

        urlsPorServico = URLS_CTE_POR_UF.get(uf);
        if (urlsPorServico != null) {
            WsUrls urls = urlsPorServico.get(tipoServico);
            if (urls != null) {
                return urls.get(tipoAmbiente);
            }
        }

        if (UFS_CTE_SVRS.contains(uf)) {
            urlsPorServico = URLS_CTE_POR_TIPO_EMISSAO.get("7"); // SVRS
            if (urlsPorServico != null) {
                WsUrls urls = urlsPorServico.get(tipoServico);
                if (urls != null) {
                    return urls.get(tipoAmbiente);
                }
            }
        }

        if (UFS_CTE_SVSP.contains(uf)) {
            urlsPorServico = URLS_CTE_POR_TIPO_EMISSAO.get("8"); // SVSP
            if (urlsPorServico != null) {
                WsUrls urls = urlsPorServico.get(tipoServico);
                if (urls != null) {
                    return urls.get(tipoAmbiente);
                }
            }
        }

        return "";
    }

    public String getUrlNFCe(String tipoServico, String uf, Integer ambiente) {
        String tipoAmbiente = String.valueOf(ambiente);
        Map<String, WsUrls> urlsPorServico = URLS_NFCE_POR_UF.get(uf);
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

    public Integer ufToCodUf(String uf) {
        return EstadoBrasil.ufToCodUf(uf);
    }

    public String xmlToGZip(final String xml) {
        if (Objects.isNull(xml) || xml.isEmpty()) {
            return null;
        }

        try (final ByteArrayOutputStream baos = new ByteArrayOutputStream();
             final GZIPOutputStream gzipOutput = new GZIPOutputStream(baos)) {
            gzipOutput.write(xml.getBytes(StandardCharsets.UTF_8));
            gzipOutput.finish();
            return Base64.getEncoder().encodeToString(baos.toByteArray());
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao compactar GZIp", e);
        }
    }

    public String getUrlMDFe(String service, Integer tipoAmbiente) {
        WsUrls wsUrls = URLS_MDFE_POR_TIPO_EMISSAO.get(service);
        
        if (wsUrls == null) {
            wsUrls = URLS_MDFE_POR_TIPO_EMISSAO.get("MDFeRecepcaoSinc");
        }
        
        return wsUrls.get(tipoAmbiente.toString());
    }

    public String formatDate(LocalDateTime data) {
        int year = data.getYear() % 100;
        int month = data.getMonthValue();
        return String.format("%02d%02d", year, month);
    }

    public String removeString(String campo, String eliminar) {
        if (campo == null || eliminar == null) {
            return "";
        }

        StringBuilder retorno = new StringBuilder();

        for (int i = 0; i < campo.length(); i++) {
            try {
                char currentChar = campo.charAt(i);
                if (!eliminar.contains(String.valueOf(currentChar))) {
                    retorno.append(currentChar);
                }
            } catch (Exception e) {
            }
        }
        return retorno.toString();
    }

    public String modulo11(String chave) {
        int total = 0;
        int peso = 2;

        for (int i = 0; i < chave.length(); i++) {
            total += (chave.charAt((chave.length() - 1) - i) - '0') * peso;
            peso++;
            if (peso == 10)
                peso = 2;
        }
        int resto = total % 11;
        int resultado = (resto == 0 || resto == 1) ? 0 : (11 - resto);
        return Integer.toString(resultado);
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

	private static String getTagValueOrEmpty(String xml, String tagName) {
		String value = FiscalUtil.pegaTag3(xml, tagName);
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
}
