package com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;

public class RequestInutilizaNFe {

    private final Integer modelo;
    private final Integer serie;
    private final Integer ano;
    private final String nfIni;
    private final String nfFin;
    private final String numeroNota;
    private final String codigoUF;
    private final String cnpj;
    private final TipoAmbienteEnum tipoAmbiente;
    private final Certificado certificado;

    private RequestInutilizaNFe(Builder builder) {
        this.modelo = builder.modelo;
        this.serie = builder.serie;
        this.ano = builder.ano;
        this.nfIni = builder.nfIni;
        this.nfFin = builder.nfFin;
        this.numeroNota = builder.numeroNota;
        this.codigoUF = builder.codigoUF;
        this.cnpj = builder.cnpj;
        this.tipoAmbiente = builder.tipoAmbiente;
        this.certificado = builder.certificado;
    }

    // Getters
    public Integer getModelo() {
        return modelo;
    }

    public Integer getSerie() {
        return serie;
    }

    public Integer getAno() {
        return ano;
    }

    public String getNfIni() {
        return nfIni;
    }

    public String getNfFin() {
        return nfFin;
    }

    public String getNumeroNota() {
        return numeroNota;
    }

    public String getCodigoUF() {
        return codigoUF;
    }

    public String getCnpj() {
        return cnpj;
    }

    public TipoAmbienteEnum getTipoAmbiente() {
        return tipoAmbiente;
    }

    public Certificado getCertificado() {
        return certificado;
    }

    // Builder
    public static class Builder {
        private Integer modelo;
        private Integer serie;
        private Integer ano;
        private String nfIni;
        private String nfFin;
        private String numeroNota;
        private String codigoUF;
        private String cnpj;
        private TipoAmbienteEnum tipoAmbiente;
        private Certificado certificado;

        public Builder modelo(Integer modelo) {
            this.modelo = modelo;
            return this;
        }

        public Builder serie(Integer serie) {
            this.serie = serie;
            return this;
        }

        public Builder ano(Integer ano) {
            this.ano = ano;
            return this;
        }

        public Builder nfIni(String nfIni) {
            this.nfIni = nfIni;
            return this;
        }

        public Builder nfFin(String nfFin) {
            this.nfFin = nfFin;
            return this;
        }

        public Builder numeroNota(String numeroNota) {
            this.numeroNota = numeroNota;
            return this;
        }

        public Builder codigoUF(String codigoUF) {
            this.codigoUF = codigoUF;
            return this;
        }

        public Builder cnpj(String cnpj) {
            this.cnpj = cnpj;
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

        public RequestInutilizaNFe build() {
            return new RequestInutilizaNFe(this);
        }
    }
}
