package com.lucast.vetcare.fiscal.nfe.funcionalidades.consultas.requests;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;

public class RequestConsultaNFe {

    private final String codigoUF;
    private final TipoAmbienteEnum tipoAmbiente;
    private final String tipoEmissao;
    private final String chaveNFe;
    private final Certificado certificado;

    private RequestConsultaNFe(Builder builder) {
        this.codigoUF = builder.codigoUF;
        this.tipoAmbiente = builder.tipoAmbiente;
        this.tipoEmissao = builder.tipoEmissao;
        this.chaveNFe = builder.chaveNFe;
        this.certificado = builder.certificado;
    }

    // Getters
    public String getCodigoUF() {
        return codigoUF;
    }

    public TipoAmbienteEnum getTipoAmbiente() {
        return tipoAmbiente;
    }

    public String getTipoEmissao() {
        return tipoEmissao;
    }

    public String getChaveNFe() {
        return chaveNFe;
    }

    public Certificado getCertificado() {
        return certificado;
    }

    // Builder interno
    public static class Builder {
        private String codigoUF;
        private TipoAmbienteEnum tipoAmbiente;
        private String tipoEmissao;
        private String chaveNFe;
        private Certificado certificado;

        public Builder codigoUF(String codigoUF) {
            this.codigoUF = codigoUF;
            return this;
        }

        public Builder tipoAmbiente(TipoAmbienteEnum tipoAmbiente) {
            this.tipoAmbiente = tipoAmbiente;
            return this;
        }

        public Builder tipoEmissao(String tipoEmissao) {
            this.tipoEmissao = tipoEmissao;
            return this;
        }

        public Builder chaveNFe(String chaveNFe) {
            this.chaveNFe = chaveNFe;
            return this;
        }

        public Builder certificado(Certificado certificado) {
            this.certificado = certificado;
            return this;
        }

        public RequestConsultaNFe build() {
            return new RequestConsultaNFe(this);
        }
    }
}
