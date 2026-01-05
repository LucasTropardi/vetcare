package com.lucast.vetcare.fiscal.nfe.funcionalidades.consultas.requests;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.enums.TipoConsultaCadastroEnum;

public class RequestConsultaCadastro {

    private final String uf;
    private final TipoAmbienteEnum tipoAmbiente;
    private final TipoConsultaCadastroEnum tipoConsultaCadastro;
    private final String valorConsultaCadastro;
    private final Certificado certificado;

    private RequestConsultaCadastro(Builder builder) {
        this.uf = builder.uf;
        this.tipoAmbiente = builder.tipoAmbiente;
        this.tipoConsultaCadastro = builder.tipoConsultaCadastro;
        this.valorConsultaCadastro = builder.valorConsultaCadastro;
        this.certificado = builder.certificado;
    }

    public String getUf() {
        return uf;
    }

    public TipoAmbienteEnum getTipoAmbiente() {
        return tipoAmbiente;
    }

    public TipoConsultaCadastroEnum getTipoConsultaCadastro() {
        return tipoConsultaCadastro;
    }

    public String getValorConsultaCadastro() {
        return valorConsultaCadastro;
    }

    public Certificado getCertificado() {
        return certificado;
    }

    public static class Builder {
        private String uf;
        private TipoAmbienteEnum tipoAmbiente;
        private TipoConsultaCadastroEnum tipoConsultaCadastro;
        private String valorConsultaCadastro;
        private Certificado certificado;

        public Builder uf(String uf) {
            this.uf = uf;
            return this;
        }

        public Builder tipoAmbiente(TipoAmbienteEnum tipoAmbiente) {
            this.tipoAmbiente = tipoAmbiente;
            return this;
        }

        public Builder tipoConsultaCadastro(TipoConsultaCadastroEnum tipoConsultaCadastro) {
            this.tipoConsultaCadastro = tipoConsultaCadastro;
            return this;
        }

        public Builder valorConsultaCadastro(String valorConsultaCadastro) {
            this.valorConsultaCadastro = valorConsultaCadastro;
            return this;
        }

        public Builder certificado(Certificado certificado) {
            this.certificado = certificado;
            return this;
        }

        public RequestConsultaCadastro build() {
            return new RequestConsultaCadastro(this);
        }
    }
}
