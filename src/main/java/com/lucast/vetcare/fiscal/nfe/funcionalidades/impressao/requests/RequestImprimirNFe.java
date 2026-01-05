package com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.requests;

import java.awt.image.BufferedImage;

public class RequestImprimirNFe {

    private String xml;
    private String urlConsulta;
    private BufferedImage logo;
    private String identificacao;

    // Construtor privado
    private RequestImprimirNFe(Builder builder) {
        this.xml = builder.xml;
        this.urlConsulta = builder.urlConsulta;
        this.logo = builder.logo;
        this.identificacao = builder.identificacao;
    }

    // Getters
    public String getXml() {
        return xml;
    }

    public String getUrlConsulta() {
        return urlConsulta;
    }

    public BufferedImage getLogo() {
        return logo;
    }

    public String getIdentificacao() {
        return identificacao;
    }

    // Builder
    public static class Builder {
        private String xml;
        private String urlConsulta;
        private BufferedImage logo;
        private String identificacao;

        public Builder xml(String xml) {
            this.xml = xml;
            return this;
        }

        public Builder urlConsulta(String urlConsulta) {
            this.urlConsulta = urlConsulta;
            return this;
        }

        public Builder logo(BufferedImage logo) {
            this.logo = logo;
            return this;
        }

        public Builder identificacao(String identificacao) {
            this.identificacao = identificacao;
            return this;
        }

        public RequestImprimirNFe build() {
            return new RequestImprimirNFe(this);
        }
    }
}
