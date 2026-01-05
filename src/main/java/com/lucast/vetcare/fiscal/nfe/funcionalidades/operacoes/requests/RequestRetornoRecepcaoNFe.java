package com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;

public class RequestRetornoRecepcaoNFe {

    private String numeroRecebimento;
    private String codigoUF;
    private TipoAmbienteEnum tipoAmbiente;
    private Certificado certificado;

    private RequestRetornoRecepcaoNFe(Builder builder) {
        this.numeroRecebimento = builder.numeroRecebimento;
        this.codigoUF = builder.codigoUF;
        this.tipoAmbiente = builder.tipoAmbiente;
        this.certificado = builder.certificado;
    }

    // Getters
    public String getNumeroRecebimento() {
        return numeroRecebimento;
    }

    public String getCodigoUF() {
        return codigoUF;
    }

    public TipoAmbienteEnum getTipoAmbiente() {
        return tipoAmbiente;
    }

    public Certificado getCertificado() {
        return certificado;
    }

    // Builder estático
    public static class Builder {
        private String numeroRecebimento;
        private String codigoUF;
        private TipoAmbienteEnum tipoAmbiente;
        private Certificado certificado;

        public Builder numeroRecebimento(String numeroRecebimento) {
            this.numeroRecebimento = numeroRecebimento;
            return this;
        }

        public Builder codigoUF(String codigoUF) {
            this.codigoUF = codigoUF;
            return this;
        }

        public Builder tipoAmbiente(TipoAmbienteEnum tipoAmbiente) {
            this.tipoAmbiente = tipoAmbiente;
            return this;
        }

        public Builder certificado(Certificado certificado) {
            this.certificado = certificado;
            return this;
        }

        public RequestRetornoRecepcaoNFe build() {
            return new RequestRetornoRecepcaoNFe(this);
        }
    }
}
