package com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests;

import com.lucast.vetcare.fiscal.certificado.Certificado;

public class RequestAssinarNFe {

    private final String xml;
    private final Certificado certificado;

    private RequestAssinarNFe(Builder builder) {
        this.xml = builder.xml;
        this.certificado = builder.certificado;
    }

    public String getXml() {
        return xml;
    }

    public Certificado getCertificado() {
        return certificado;
    }

    public static class Builder {
        private String xml;
        private Certificado certificado;

        public Builder xml(String xml) {
            this.xml = xml;
            return this;
        }

        public Builder certificado(Certificado certificado) {
            this.certificado = certificado;
            return this;
        }

        public RequestAssinarNFe build() {
            return new RequestAssinarNFe(this);
        }
    }
}
