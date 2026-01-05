package com.lucast.vetcare.fiscal.nfce.funcionalidades;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.certificado.CertificadoService;
import com.lucast.vetcare.fiscal.enums.AssinaturaEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.util.FiscalUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.*;
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
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AssinarNFCe {

    private static PrivateKey privateKey;

    private static KeyInfo keyInfo;

    public String assinaNfce(String stringXml, Certificado certificado, AssinaturaEnum tipoAssinatura) throws FiscalException, Exception {
        stringXml = stringXml.replaceAll("\r\n", "").replaceAll("\n", "").replaceAll(System.lineSeparator(), "");
        stringXml = stringXml.replaceAll("\\s+<", "<");
        stringXml = assinaDocNFe(certificado, stringXml, tipoAssinatura);
        stringXml = stringXml.replaceAll("&#13;", "");

        return stringXml;
    }

    public String assinaDocNFe(Certificado certificado, String xmlString, AssinaturaEnum tipoAssinatura) throws Exception {
        Document document = documentFactory(xmlString);
        XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");
        ArrayList<Transform> transformList = signatureFactory(signatureFactory);

        KeyStore keyStore = CertificadoService.getKeyStore(certificado);
        KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(
                certificado.getNome(), new KeyStore.PasswordProtection(certificado.getSenha().toCharArray()));
        privateKey = pkEntry.getPrivateKey();

        KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
        X509Certificate cert = CertificadoService.getCertificate(certificado, keyStore);
        X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(cert));
        keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));

        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();
        NodeList infNFeNodes = (NodeList) xpath.evaluate("//*[local-name()='infNFe']", document, XPathConstants.NODESET);

        for (int i = 0; i < infNFeNodes.getLength(); i++) {
            Element element = (Element) infNFeNodes.item(i);
            String id = element.getAttribute("Id");
            element.setIdAttribute("Id", true);

            DOMSignContext dsc = new DOMSignContext(privateKey, element);
            dsc.setIdAttributeNS((Element) infNFeNodes.item(i), null, "Id");

            Reference ref = signatureFactory.newReference("#" + id,
                    signatureFactory.newDigestMethod(DigestMethod.SHA1, null),
                    transformList, null, null);

            SignedInfo si = signatureFactory.newSignedInfo(
                    signatureFactory.newCanonicalizationMethod(
                            CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                    signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                    Collections.singletonList(ref));

            XMLSignature signature = signatureFactory.newXMLSignature(si, keyInfo);
            signature.sign(dsc);
        }

        return new FiscalUtil().removeXMLTag(formataXML(outputXML(document), "infNFe"));
    }

    public String assinaEvento(String xml, Certificado certificado, AssinaturaEnum evento) {
        try {
            Document document = documentFactory(xml);
            XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");
            ArrayList<Transform> transformList = signatureFactory(signatureFactory);

            KeyStore key = CertificadoService.getKeyStore(certificado);

            KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) key.getEntry(certificado.getNome(),
                    new KeyStore.PasswordProtection(certificado.getSenha().toCharArray()));
            privateKey = pkEntry.getPrivateKey();

            KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
            List<X509Certificate> certs = new ArrayList<>();

            certs.add(CertificadoService.getCertificate(certificado, key));
            X509Data x509Data = keyInfoFactory.newX509Data(certs);

            keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));

            XPathFactory xpf = XPathFactory.newInstance();
            XPath xPath = xpf.newXPath();

            String expression = "//*[local-name()='evento']";
            NodeList eventos = (NodeList) xPath.evaluate(expression, document, XPathConstants.NODESET);

            for (int i = 0; i < eventos.getLength(); i++) {
                assinarEvento(evento, signatureFactory, transformList, privateKey, keyInfo, document, i);
            }

            return new FiscalUtil().removeXMLTag(formataXML(outputXML(document), "infEvento"));
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private void assinarEvento(AssinaturaEnum tipoAssinatura, XMLSignatureFactory signatureFactory, ArrayList<Transform> transformList,
                               PrivateKey privateKey, KeyInfo keyInfo, Document document, int index) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException,
            MarshalException, XMLSignatureException, XPathExpressionException {
        XPathFactory xpf = XPathFactory.newInstance();
        XPath xpath = xpf.newXPath();

        String expression = "//*[local-name()='" + tipoAssinatura.getTag() + "']";
        NodeList elements = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

        Element el = (Element) elements.item(index);

        String id = el.getAttribute("Id");
        el.setIdAttribute("Id", true);
        Reference ref = signatureFactory.newReference("#" + id, signatureFactory.newDigestMethod(DigestMethod.SHA1, null), transformList, null, null);

        SignedInfo si = signatureFactory.newSignedInfo(
                signatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                Collections.singletonList(ref));

        XMLSignature signature = signatureFactory.newXMLSignature(si, keyInfo);

        String typeExpression = "//*[local-name()='" + tipoAssinatura.getTipo() + "']";
        NodeList typeElements = (NodeList) xpath.evaluate(typeExpression, document, XPathConstants.NODESET);
        DOMSignContext dsc = new DOMSignContext(privateKey, typeElements.item(index));

        dsc.setBaseURI("ok");

        signature.sign(dsc);
    }

    private static String outputXML(Document doc) throws FiscalException {
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()){
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer trans = tf.newTransformer();
            trans.transform(new DOMSource(doc), new StreamResult(os));
            String xml = os.toString();
            xml = xml.replaceAll("\\r\\n", "");
            xml = xml.replaceAll(" standalone=\"no\"", "");
            return xml;
        } catch (TransformerException | IOException e) {
            e.printStackTrace();
            throw new FiscalException("Erro ao Transformar Documento:" + e.getMessage());
        }
    }

    private String formataXML(String xml, String tag) {
        try {
            // Verifica se existe a tag <infNFeSupl>
            if (xml.contains("<infNFeSupl>")) {
                tag = "infNFeSupl";
            }

            // Extrair a tag <Signature>
            String signatureRegex = "<Signature[\\s\\S]*?</Signature>";
            Pattern pattern = Pattern.compile(signatureRegex);
            Matcher matcher = pattern.matcher(xml);
            String signatureTag = "";

            if (matcher.find()) {
                signatureTag = matcher.group();
            }

            // Remover a tag <Signature> do XML original
            xml = xml.replace(signatureTag, "");

            // Adicionar a tag <Signature> após a tag informada
            String closeTag = "</" + tag + ">";
            int closeIndex = xml.indexOf(closeTag);

            if (closeIndex != -1) {
                closeIndex += closeTag.length();
                StringBuilder sb = new StringBuilder(xml);
                sb.insert(closeIndex, signatureTag);
                return sb.toString();
            }

            return xml;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static Document documentFactory(String xml) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilderFactory.setNamespaceAware(true);
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
        return docBuilder.parse(new InputSource(new StringReader(xml)));
    }

    private static ArrayList<Transform> signatureFactory(XMLSignatureFactory signatureFactory) throws NoSuchAlgorithmException, InvalidAlgorithmParameterException {
        ArrayList<Transform> transformList = new ArrayList<Transform>();
        Transform envelopedTransform = signatureFactory.newTransform(Transform.ENVELOPED,
                (TransformParameterSpec) null);
        Transform c14NTransform = signatureFactory.newTransform("http://www.w3.org/TR/2001/REC-xml-c14n-20010315",
                (TransformParameterSpec) null);

        transformList.add(envelopedTransform);
        transformList.add(c14NTransform);
        return transformList;
    }
}