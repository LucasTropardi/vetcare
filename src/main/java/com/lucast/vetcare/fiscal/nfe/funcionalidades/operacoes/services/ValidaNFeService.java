package com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.services;

import java.io.StringReader;
import java.net.URL;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests.RequestValidaNFe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

@Service
@Scope("prototype")
public class ValidaNFeService implements ErrorHandler {

    private static final Logger logger = LoggerFactory.getLogger(ValidaNFeService.class);

    private String listaComErrosDeValidacao;

    public boolean validaXml(RequestValidaNFe request) throws FiscalException {
        logger.info("Validando XML da NFe para o serviço: {}", request.getServico().name());
        return validaXml("schemas/" + request.getServico().getXsd(), request.getXmlAssinado());
    }

    private boolean validaXml(String xsd, String xml) throws FiscalException {
        System.setProperty("jdk.xml.maxOccurLimit", "99999");

        logger.info("Iniciando validação do XML com o XSD: {}", xsd);
        String errosValidacao = validateXml(xml, xsd);

        if (verifica(errosValidacao).isPresent()) {
            logger.warn("Erros encontrados durante a validação do XML: {}", errosValidacao);
            throw new FiscalException("Aviso", "Erro: " + errosValidacao);
        }

        logger.info("Validação concluída sem erros.");
        return true;
    }

    private String validateXml(String xml, String xsd) throws FiscalException {
        try {
            logger.info("Iniciando validação do XML da NFe. Conteúdo: {}", xml);

            listaComErrosDeValidacao = "";

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setValidating(true);
            documentBuilderFactory.setNamespaceAware(true);
            documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                    "http://www.w3.org/2001/XMLSchema");

            URL xsdUrl = getClass().getClassLoader().getResource(xsd);
            if (xsdUrl == null) {
                logger.error("Schema NFe não localizado: {}", xsd);
                throw new FiscalException("Schema NFe não localizado: " + xsd);
            }

            documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", xsdUrl.toString());
            DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
            builder.setErrorHandler(this);

            logger.info("Parsing e validação do XML iniciados.");
            builder.parse(new InputSource(new StringReader(xml)));
            logger.info("Parsing do XML concluído.");

        } catch (Exception e) {
            throw new FiscalException("Erro ao validar XML: "  + e.getMessage(), e);
        }

        return this.getListaComErrosDeValidacao();
    }

    @Override
    public void error(SAXParseException exception) {
        if (isError(exception)) {
            String msgTratada = tratamentoRetorno(exception.getMessage());
            listaComErrosDeValidacao += msgTratada;
            logger.error("Erro de validação: {}", msgTratada);
        }
    }

    @Override
    public void fatalError(SAXParseException exception) {
        String msgTratada = tratamentoRetorno(exception.getMessage());
        listaComErrosDeValidacao += msgTratada;
        logger.error("Erro fatal de validação: {}", msgTratada);
    }

    @Override
    public void warning(SAXParseException exception) {
        String msgTratada = tratamentoRetorno(exception.getMessage());
        listaComErrosDeValidacao += msgTratada;
        logger.warn("Aviso de validação: {}", msgTratada);
    }

    private String tratamentoRetorno(String message) {
        if (message == null || message.isEmpty()) {
            return "";
        }

        message = removerPrefixosValidacao(message);

        message = traduzirMensagem(message);

        message = limparCaracteresEspeciais(message);

        return "<br>" + message.trim();
    }

    private String removerPrefixosValidacao(String message) {
        String[] prefixos = {
                "cvc-type.3.1.3:", "cvc-attribute.3:", "cvc-complex-type.2.4.a:",
                "cvc-complex-type.2.4.b:", "cvc-complex-type.2.4.c:",
                "cvc-complex-type.2.4.d:", "cvc-complex-type.4:",
                "cvc-elt.1.a: ", "cvc-minLength-valid:",
                "http://www.portalfiscal.inf.br/nfe:"
        };

        for (String prefixo : prefixos) {
            message = message.replace(prefixo, prefixo.endsWith(":") && !prefixo.endsWith(": ") ? "-" : "");
        }

        return message;
    }

    private String traduzirMensagem(String message) {
        Map<String, String> traducoes = new LinkedHashMap<>();
        traducoes.put("The value", "O valor");
        traducoes.put("Value", "Valor");
        traducoes.put("with length", "com tamanho");
        traducoes.put("is not facet-valid with respect to minLength", "não equivale ao tamanho mínimo");
        traducoes.put("for type", "para o tipo");
        traducoes.put("The content", "O conteúdo");
        traducoes.put("of element", "do campo");
        traducoes.put("is not complete", "não está completo");
        traducoes.put("is not valid", "não é válido");
        traducoes.put("Attribute", "Campo");
        traducoes.put("must appear on element", "precisa estar em");
        traducoes.put("Invalid content was found starting with element", "Conteúdo inválido encontrado iniciando com o campo");
        traducoes.put("One of", "Um dos Campos");
        traducoes.put("is expected", "é esperado");

        for (Map.Entry<String, String> entry : traducoes.entrySet()) {
            message = message.replace(entry.getKey(), entry.getValue());
        }

        return message;
    }

    private String limparCaracteresEspeciais(String message) {
        return message.replace("{", "")
                .replace("}", "")
                .replace("\"", "");
    }

    private String getListaComErrosDeValidacao() {
        return listaComErrosDeValidacao;
    }

    private boolean isError(SAXParseException exception) {
        return !exception.getMessage().startsWith("cvc-enumeration-valid")
                && !exception.getMessage().startsWith("cvc-pattern-valid")
                && !exception.getMessage().startsWith("cvc-maxLength-valid")
                && !exception.getMessage().startsWith("cvc-datatype");
    }

    public static <T> Optional<T> verifica(T obj) {
        if (obj == null)
            return Optional.empty();
        if (obj instanceof Collection)
            return ((Collection<?>) obj).isEmpty() ? Optional.empty() : Optional.of(obj);

        final String s = String.valueOf(obj).trim();
        return s.isEmpty() || s.equalsIgnoreCase("null") ? Optional.empty() : Optional.of(obj);
    }
}
