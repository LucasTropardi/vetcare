package com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.requests;


public class RequestImprimirCCeNFe {

    private String xml;
    private String xmlEventoCCe;

    // Construtor privado
    private RequestImprimirCCeNFe(Builder builder) {
        this.xml = builder.xml;
        this.xmlEventoCCe = builder.xmlEventoCCe;
    }

    // Getters
    public String getXml() {
        return xml;
    }

    public String getXmlEventoCCe() {
        return xmlEventoCCe;
    }

    // Builder
    public static class Builder {
        private String xml;
        private String xmlEventoCCe;

        public Builder xml(String xml) {
            this.xml = xml;
            return this;
        }

        public Builder xmlEventoCCe(String xmlEventoCCe) {
            this.xmlEventoCCe = xmlEventoCCe;
            return this;
        }

        public RequestImprimirCCeNFe build() {
            return new RequestImprimirCCeNFe(this);
        }
    }
}
