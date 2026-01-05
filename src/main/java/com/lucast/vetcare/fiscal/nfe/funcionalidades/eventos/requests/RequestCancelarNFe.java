package com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;

import java.time.LocalDateTime;

public class RequestCancelarNFe {
    private final String justificativa;
    private final String codigoUF;
    private final String cnpj;
    private final String chaveNFe;
    private final String protocolo;
    private final LocalDateTime dataEvento;
    private final String tipoEmissao;
    private final TipoAmbienteEnum tipoAmbiente;
    private final Certificado certificado;

    private RequestCancelarNFe(Builder builder) {
        this.justificativa = builder.justificativa;
        this.codigoUF = builder.codigoUF;
        this.cnpj = builder.cnpj;
        this.chaveNFe = builder.chaveNFe;
        this.protocolo = builder.protocolo;
        this.dataEvento = builder.dataEvento;
        this.tipoEmissao = builder.tipoEmissao;
        this.tipoAmbiente = builder.tipoAmbiente;
        this.certificado = builder.certificado;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public String getCodigoUF() {
        return codigoUF;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getChaveNFe() {
        return chaveNFe;
    }

    public String getProtocolo() {
        return protocolo;
    }

    public LocalDateTime getDataEvento() {
        return dataEvento;
    }

    public String getTipoEmissao() {
        return tipoEmissao;
    }

    public TipoAmbienteEnum getTipoAmbiente() {
        return tipoAmbiente;
    }

    public Certificado getCertificado() {
        return certificado;
    }

    // Builder interno
    public static class Builder {
        private String justificativa;
        private String codigoUF;
        private String cnpj;
        private String chaveNFe;
        private String protocolo;
        private LocalDateTime dataEvento;
        private String tipoEmissao;
        private TipoAmbienteEnum tipoAmbiente;
        private Certificado certificado;

        public Builder justificativa(String justificativa) {
            this.justificativa = justificativa;
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

        public Builder chaveNFe(String chaveNFe) {
            this.chaveNFe = chaveNFe;
            return this;
        }

        public Builder protocolo(String protocolo) {
            this.protocolo = protocolo;
            return this;
        }

        public Builder dataEvento(LocalDateTime dataEvento) {
            this.dataEvento = dataEvento;
            return this;
        }

        public Builder tipoEmissao(String tipoEmissao) {
            this.tipoEmissao = tipoEmissao;
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

        public RequestCancelarNFe build() {
            return new RequestCancelarNFe(this);
        }
    }
}
