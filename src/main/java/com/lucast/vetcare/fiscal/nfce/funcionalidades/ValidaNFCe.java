package com.lucast.vetcare.fiscal.nfce.funcionalidades;

import com.lucast.vetcare.fiscal.enums.ServicosNFeEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import org.springframework.stereotype.Service;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.StringReader;
import java.net.URL;
import java.util.Collection;
import java.util.Optional;

@Service
public class ValidaNFCe implements ErrorHandler {

    private final ThreadLocal<StringBuilder> listaComErrosDeValidacao = ThreadLocal.withInitial(StringBuilder::new);

    public Boolean validaXml(String xmlAssinado, ServicosNFeEnum servico) throws FiscalException {
        return validaXml("schemas-nfe/" + servico.getXsd(), xmlAssinado);
    }

    private boolean validaXml(String xsd, String xml) throws FiscalException {
        System.setProperty("jdk.xml.maxOccurLimit", "99999");

        String errosValidacao = validateXml(xml, xsd);

        if (verifica(errosValidacao).isPresent()) {
            throw new FiscalException("Aviso", "Erro: " + errosValidacao);
        }

        return true;
    }

    private String validateXml(String xml, String xsd) throws FiscalException {
        listaComErrosDeValidacao.set(new StringBuilder());

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setValidating(true);
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaLanguage",
                "http://www.w3.org/2001/XMLSchema");

        URL xsdUrl = getClass().getClassLoader().getResource(xsd);
        if (xsdUrl == null) {
            throw new FiscalException("Schema Nfe não Localizado: " + xsd);
        }

        documentBuilderFactory.setAttribute("http://java.sun.com/xml/jaxp/properties/schemaSource", xsdUrl.toString());
        DocumentBuilder builder;
        try {
            builder = documentBuilderFactory.newDocumentBuilder();
            builder.setErrorHandler(this);
            builder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            throw new FiscalException("Erro", tratamentoRetorno(e.getMessage()));
        }

        return this.getListaComErrosDeValidacao();
    }

    @Override
    public void error(SAXParseException exception) {
        if (isError(exception)) {
            listaComErrosDeValidacao.get().append(tratamentoRetorno(exception.getMessage()));
        }
    }

    @Override
    public void fatalError(SAXParseException exception) {
        listaComErrosDeValidacao.get().append(tratamentoRetorno(exception.getMessage()));
    }

    @Override
    public void warning(SAXParseException exception) {
        listaComErrosDeValidacao.get().append(tratamentoRetorno(exception.getMessage()));
    }

    private String tratamentoRetorno(String message) {
        message = message.replaceAll("cvc-type.3.1.3:", "-");
        message = message.replaceAll("cvc-attribute.3:", "-");
        message = message.replaceAll("cvc-complex-type.2.4.a:", "-");
        message = message.replaceAll("cvc-complex-type.2.4.b:", "-");
        message = message.replaceAll("cvc-complex-type.2.4.c:", "-");
        message = message.replaceAll("cvc-complex-type.2.4.d:", "-");
        message = message.replaceAll("cvc-complex-type.4:", "-");
        message = message.replaceAll("cvc-elt.1.a: ", "");
        message = message.replaceAll("cvc-minLength-valid:", "-");
        message = message.replaceAll("The value", "O valor");
        message = message.replaceAll("Value", "Valor");
        message = message.replaceAll("with length", "com tamanho");
        message = message.replaceAll("is not facet-valid with respect to minLength", "não equivale ao tamanho mínimo");
        message = message.replaceAll("for type", "para o tipo");
        message = message.replaceAll("The content", "O conteúdo");
        message = message.replaceAll("of element", "do campo");
        message = message.replaceAll("is not complete", "não está completo");
        message = message.replaceAll("is not valid", "não é válido");
        message = message.replaceAll("Attribute", "Campo");
        message = message.replaceAll("must appear on element", "precisa estar em");
        message = message.replaceAll("Invalid content was found starting with element", "Conteúdo inválido encontrado iniciando com o campo");
        message = message.replaceAll("One of", "Um dos Campos");
        message = message.replaceAll("is expected", "é esperado");
        message = message.replaceAll("\\{", "");
        message = message.replaceAll("\\}", "");
        message = message.replaceAll("\"", "");
        message = message.replaceAll("http://www.portalfiscal.inf.br/nfe:", "");
        message = message.replaceAll("The element type", "O tipo de elemento");
        message = message.replaceAll("must be terminated by the matching end-tag", "deve ser finalizado pela tag final correspondente");
        message = message.replaceAll("Cannot find the declaration do campo", "Não foi possível encontrar a declaração do campo");
        message = message.replaceAll("of attribute", "de atributo");
        message = message.replaceAll("with respect to its type", "em relação ao seu tipo");
        message = message.replaceAll("on element", "no elemento");
        return "<br>" + message.trim();
    }

    private String getListaComErrosDeValidacao() {
        String erros = listaComErrosDeValidacao.get().toString();
        listaComErrosDeValidacao.remove();
        return erros;
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
            return ((Collection<?>) obj).size() == 0 ? Optional.empty() : Optional.of(obj);

        final String s = String.valueOf(obj).trim();

        return s.length() == 0 || s.equalsIgnoreCase("null") ? Optional.empty() : Optional.of(obj);
    }
}
