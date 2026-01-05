package com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests;

import com.lucast.vetcare.fiscal.enums.ServicosNFeEnum;

public class RequestValidaNFe {

    private final String xmlAssinado;
    private final ServicosNFeEnum servico;

    private RequestValidaNFe(Builder builder) {
        this.xmlAssinado = builder.xmlAssinado;
        this.servico = builder.servico;
    }

    public String getXmlAssinado() {
        return xmlAssinado;
    }

    public ServicosNFeEnum getServico() {
        return servico;
    }

    // Builder interno
    public static class Builder {
        private String xmlAssinado;
        private ServicosNFeEnum servico;

        public Builder xmlAssinado(String xmlAssinado) {
            this.xmlAssinado = xmlAssinado;
            return this;
        }

        public Builder servico(ServicosNFeEnum servico) {
            this.servico = servico;
            return this;
        }

        public RequestValidaNFe build() {
            return new RequestValidaNFe(this);
        }
    }
}
