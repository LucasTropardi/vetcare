package com.lucast.vetcare.fiscal.nfe.funcionalidades.impressao.requests;

import java.awt.image.BufferedImage;

public class RequestImprimirNFCe {

    private String xml;
    private String urlConsulta;
    private BufferedImage logo;

    // Construtor privado
    private RequestImprimirNFCe(Builder builder) {
        this.xml = builder.xml;
        this.urlConsulta = builder.urlConsulta;
        this.logo = builder.logo;
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

    // Builder
    public static class Builder {
        private String xml;
        private String urlConsulta;
        private BufferedImage logo;

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

        public RequestImprimirNFCe build() {
            return new RequestImprimirNFCe(this);
        }
    }
}
