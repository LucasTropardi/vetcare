package com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.services;

import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests.RequestAssinarNFe;
import com.lucast.vetcare.fiscal.util.FiscalUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.certificado.CertificadoService;
import com.lucast.vetcare.fiscal.enums.AssinaturaEnum;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.security.*;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Scope("prototype")
public class AssinarNFeService {

    private static final Logger logger = LoggerFactory.getLogger(AssinarNFeService.class);

    // Constantes para configuração da assinatura digital
    private static final String XML_SIGNATURE_FACTORY = "DOM";
    private static final String DIGEST_METHOD = DigestMethod.SHA1;
    private static final String SIGNATURE_METHOD = SignatureMethod.RSA_SHA1;
    private static final String CANONICALIZATION_METHOD = CanonicalizationMethod.INCLUSIVE;
    private static final String C14N_TRANSFORM_URI = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";

    // Padrões de regex para limpeza e formatação de XML
    private static final Pattern LINE_BREAKS_PATTERN = Pattern.compile("[\r\n]");
    private static final Pattern SPACES_BEFORE_TAG_PATTERN = Pattern.compile("\\s+<");
    private static final Pattern SIGNATURE_TAG_PATTERN = Pattern.compile("<Signature[\\s\\S]*?</Signature>");
    private static final Pattern CARRIAGE_RETURN_PATTERN = Pattern.compile("&#13;");
    private static final Pattern STANDALONE_ATTR_PATTERN = Pattern.compile(" standalone=\"no\"");
    public static final String LOCAL_NAME = "//*[local-name()=";

    private static class SignatureContext {
        final PrivateKey privateKey;
        final KeyInfo keyInfo;
        final XMLSignatureFactory signatureFactory;
        final List<Transform> transforms;

        SignatureContext(PrivateKey privateKey, KeyInfo keyInfo,
                         XMLSignatureFactory signatureFactory, List<Transform> transforms) {
            this.privateKey = privateKey;
            this.keyInfo = keyInfo;
            this.signatureFactory = signatureFactory;
            this.transforms = transforms;
        }
    }

    public String assinaNfe(RequestAssinarNFe request) throws FiscalException {
        try {
            logger.info("Iniciando assinatura da NFe...");

            String xmlNormalizado = normalizarXml(request.getXml());
            String xmlAssinado = assinaDocNFe(request.getCertificado(), xmlNormalizado);
            xmlAssinado = removerCarriageReturn(xmlAssinado);

            logger.info("Assinatura da NFe concluída com sucesso.");
            return xmlAssinado;
        } catch (Exception e) {
            throw new FiscalException("Erro ao assinar NFe: " + e.getMessage(), e);
        }
    }

    public String assinaEvento(String xml, Certificado certificado, AssinaturaEnum evento)
            throws FiscalException {
        try {
            logger.info("Iniciando assinatura de Evento: {}", evento.name());

            Document document = criarDocumento(xml);
            SignatureContext context = criarContextoAssinatura(certificado);

            NodeList eventos = buscarElementosPorTag(document, "evento");
            assinarMultiplosElementos(document, eventos, evento, context, this::assinarElementoEvento);

            String xmlAssinado = documentoParaString(document);
            String xmlFormatado = formatarXmlComAssinatura(xmlAssinado, "infEvento");

            logger.info("Assinatura de Evento concluída com sucesso.");
            return FiscalUtils.removeXMLTag(xmlFormatado);
        } catch (Exception e) {
            throw new FiscalException("Erro ao assinar Evento: " + e.getMessage(), e);
        }
    }

    public String assinaInut(String xml, Certificado certificado, AssinaturaEnum evento) throws FiscalException {
        try {
            logger.info("Iniciando assinatura de Inutilização: {}", evento.name());

            Document document = criarDocumento(xml);
            SignatureContext context = criarContextoAssinatura(certificado);

            NodeList elementos = document.getDocumentElement().getElementsByTagName(evento.getTipo());
            assinarMultiplosElementos(document, elementos, evento, context, this::assinarElementoInut);

            String xmlAssinado = documentoParaString(document);
            String xmlFormatado = formatarXmlComAssinatura(xmlAssinado, "infInut");

            logger.info("Assinatura de Inutilização concluída com sucesso.");
            return FiscalUtils.removeXMLTag(xmlFormatado);
        } catch (Exception e) {
            throw new FiscalException("Erro ao assinar Inutilização: " + e.getMessage(), e);
        }
    }

    private String normalizarXml(String xml) {
        xml = LINE_BREAKS_PATTERN.matcher(xml).replaceAll("");
        xml = SPACES_BEFORE_TAG_PATTERN.matcher(xml).replaceAll("<");
        return xml;
    }

    private String assinaDocNFe(Certificado certificado, String xmlString)
            throws FiscalException {
        try {
            Document document = criarDocumento(xmlString);
            SignatureContext context = criarContextoAssinatura(certificado);

            NodeList infNFeNodes = buscarElementosPorTag(document, "infNFe");

            for (int i = 0; i < infNFeNodes.getLength(); i++) {
                assinarElementoNFe((Element) infNFeNodes.item(i), context);
            }

            String xmlAssinado = documentoParaString(document);
            return FiscalUtils.removeXMLTag(formatarXmlComAssinatura(xmlAssinado, "infNFe"));
        } catch (Exception e) {
            throw new FiscalException("Erro ao assinar documento NFe: " + e.getMessage(), e);
        }
    }

    private Document criarDocumento(String xml)
            throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);

        // Configurações de segurança para prevenir XXE attacks
        factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
        factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        factory.setXIncludeAware(false);
        factory.setExpandEntityReferences(false);

        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(new InputSource(new StringReader(xml)));
    }

    private SignatureContext criarContextoAssinatura(Certificado certificado)
            throws UnrecoverableEntryException, NoSuchAlgorithmException, KeyStoreException,
            InvalidAlgorithmParameterException, FiscalException {

        KeyStore keyStore = CertificadoService.getKeyStore(certificado);

        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(
                certificado.getNome(),
                new KeyStore.PasswordProtection(certificado.getSenha().toCharArray()));

        PrivateKey privateKey = pkEntry.getPrivateKey();

        XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance(XML_SIGNATURE_FACTORY);
        List<Transform> transforms = criarTransformacoes(signatureFactory);

        X509Certificate cert = CertificadoService.getCertificate(certificado, keyStore);
        KeyInfo keyInfo = criarKeyInfo(signatureFactory, cert);

        return new SignatureContext(privateKey, keyInfo, signatureFactory, transforms);
    }

    private NodeList buscarElementosPorTag(Document document, String tagName)
            throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        String expression = LOCAL_NAME + tagName + "']";
        return (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
    }

    private void assinarElementoNFe(Element element, SignatureContext context)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            MarshalException, XMLSignatureException {

        String id = element.getAttribute("Id");
        element.setIdAttribute("Id", true);

        DOMSignContext dsc = new DOMSignContext(context.privateKey, element);
        dsc.setIdAttributeNS(element, null, "Id");

        XMLSignature signature = criarAssinatura(context, id);
        signature.sign(dsc);
    }

    private String documentoParaString(Document document) throws FiscalException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(new DOMSource(document), new StreamResult(os));

            String xml = os.toString();
            xml = LINE_BREAKS_PATTERN.matcher(xml).replaceAll("");
            xml = STANDALONE_ATTR_PATTERN.matcher(xml).replaceAll("");
            return xml;
        } catch (TransformerException | IOException e) {
            throw new FiscalException("Erro ao transformar documento XML: " + e.getMessage(), e);
        }
    }

    private String formatarXmlComAssinatura(String xml, String tagReferencia) {
        try {
            Matcher matcher = SIGNATURE_TAG_PATTERN.matcher(xml);
            if (!matcher.find()) {
                logger.warn("Tag Signature não encontrada no XML");
                return xml;
            }

            String signatureTag = matcher.group();
            xml = xml.replace(signatureTag, "");

            String closeTag = "</" + tagReferencia + ">";
            int closeIndex = xml.indexOf(closeTag);

            if (closeIndex == -1) {
                logger.warn("Tag de fechamento '{}' não encontrada", closeTag);
                return xml + signatureTag;
            }

            closeIndex += closeTag.length();
            return xml.substring(0, closeIndex) + signatureTag + xml.substring(closeIndex);
        } catch (Exception e) {
            logger.error("Erro ao formatar XML com assinatura", e);
            return xml;
        }
    }

    private XMLSignature criarAssinatura(SignatureContext context, String id)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        Reference ref = context.signatureFactory.newReference(
                "#" + id,
                context.signatureFactory.newDigestMethod(DIGEST_METHOD, null),
                context.transforms,
                null,
                null);

        SignedInfo signedInfo = context.signatureFactory.newSignedInfo(
                context.signatureFactory.newCanonicalizationMethod(
                        CANONICALIZATION_METHOD,
                        (C14NMethodParameterSpec) null),
                context.signatureFactory.newSignatureMethod(SIGNATURE_METHOD, null),
                Collections.singletonList(ref));

        return context.signatureFactory.newXMLSignature(signedInfo, context.keyInfo);
    }

    private List<Transform> criarTransformacoes(XMLSignatureFactory signatureFactory)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {

        List<Transform> transforms = new ArrayList<>();
        transforms.add(signatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null));
        transforms.add(signatureFactory.newTransform(C14N_TRANSFORM_URI, (TransformParameterSpec) null));
        return transforms;
    }

    private KeyInfo criarKeyInfo(XMLSignatureFactory signatureFactory, X509Certificate certificate) {
        KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
        X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(certificate));
        return keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));
    }

    private String removerCarriageReturn(String xml) {
        return CARRIAGE_RETURN_PATTERN.matcher(xml).replaceAll("");
    }

    @FunctionalInterface
    private interface ElementSigner {
        void sign(Document doc, Element el, AssinaturaEnum tipo, SignatureContext ctx, int idx)
                throws FiscalException, MarshalException, InvalidAlgorithmParameterException, XPathExpressionException, NoSuchAlgorithmException, XMLSignatureException;
    }

    private void assinarMultiplosElementos(Document document, NodeList elementos,
                                           AssinaturaEnum tipoAssinatura, SignatureContext context,
                                           ElementSigner signer) throws Exception {
        for (int i = 0; i < elementos.getLength(); i++) {
            Element element = obterElementoPorTag(document, tipoAssinatura.getTag(), i);
            signer.sign(document, element, tipoAssinatura, context, i);
        }
    }

    private Element obterElementoPorTag(Document document, String tagName, int index)
            throws XPathExpressionException {
        XPath xpath = XPathFactory.newInstance().newXPath();
        String expression = LOCAL_NAME + tagName + "']";
        NodeList elements = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);
        return (Element) elements.item(index);
    }

    private void assinarElementoEvento(Document document, Element element, AssinaturaEnum tipoAssinatura,
                                       SignatureContext context, int index)
            throws XPathExpressionException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            MarshalException, XMLSignatureException {

        String id = element.getAttribute("Id");
        element.setIdAttribute("Id", true);

        XPath xpath = XPathFactory.newInstance().newXPath();
        String expression = LOCAL_NAME + tipoAssinatura.getTipo() + "']";
        NodeList typeElements = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

        DOMSignContext dsc = new DOMSignContext(context.privateKey, typeElements.item(index));
        dsc.setBaseURI("ok");

        XMLSignature signature = criarAssinatura(context, id);
        signature.sign(dsc);
    }

    private void assinarElementoInut(Document document, Element element, AssinaturaEnum tipoAssinatura,
                                     SignatureContext context, int index)
            throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            MarshalException, XMLSignatureException {

        String id = element.getAttribute("Id");
        element.setIdAttribute("Id", true);

        DOMSignContext dsc = new DOMSignContext(context.privateKey, document.getFirstChild());
        dsc.setBaseURI("ok");

        XMLSignature signature = criarAssinatura(context, id);
        signature.sign(dsc);
    }

}