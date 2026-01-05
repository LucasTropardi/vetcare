package com.lucast.vetcare.fiscal.nfe.funcionalidades.eventos.requests;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;

import java.time.LocalDateTime;

public class RequestCartaCorrecaoNFe {

    private final Long lote;
    private final String chaveNFe;
    private final Long sequencia;
    private final String correcao;
    private final String codigoUF;
    private final String cnpj;
    private final String tipoEmissao;
    private final LocalDateTime dataEvento;
    private final TipoAmbienteEnum tipoAmbiente;
    private final Certificado certificado;

    private RequestCartaCorrecaoNFe(Builder builder) {
        this.lote = builder.lote;
        this.chaveNFe = builder.chaveNFe;
        this.sequencia = builder.sequencia;
        this.correcao = builder.correcao;
        this.codigoUF = builder.codigoUF;
        this.cnpj = builder.cnpj;
        this.tipoEmissao = builder.tipoEmissao;
        this.dataEvento = builder.dataEvento;
        this.tipoAmbiente = builder.tipoAmbiente;
        this.certificado = builder.certificado;
    }

    // Getters
    public Long getLote() {
        return lote;
    }

    public String getChaveNFe() {
        return chaveNFe;
    }

    public Long getSequencia() {
        return sequencia;
    }

    public String getCorrecao() {
        return correcao;
    }

    public String getCodigoUF() {
        return codigoUF;
    }

    public String getCnpj() {
        return cnpj;
    }

    public String getTipoEmissao() {
        return tipoEmissao;
    }

    public LocalDateTime getDataEvento() {
        return dataEvento;
    }

    public TipoAmbienteEnum getTipoAmbiente() {
        return tipoAmbiente;
    }

    public Certificado getCertificado() {
        return certificado;
    }

    // Builder interno
    public static class Builder {
        private Long lote;
        private String chaveNFe;
        private Long sequencia;
        private String correcao;
        private String codigoUF;
        private String cnpj;
        private String tipoEmissao;
        private LocalDateTime dataEvento;
        private TipoAmbienteEnum tipoAmbiente;
        private Certificado certificado;

        public Builder lote(Long lote) {
            this.lote = lote;
            return this;
        }

        public Builder chaveNFe(String chaveNFe) {
            this.chaveNFe = chaveNFe;
            return this;
        }

        public Builder sequencia(Long sequencia) {
            this.sequencia = sequencia;
            return this;
        }

        public Builder correcao(String correcao) {
            this.correcao = correcao;
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

        public Builder tipoEmissao(String tipoEmissao) {
            this.tipoEmissao = tipoEmissao;
            return this;
        }

        public Builder dataEvento(LocalDateTime dataEvento) {
            this.dataEvento = dataEvento;
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

        public RequestCartaCorrecaoNFe build() {
            return new RequestCartaCorrecaoNFe(this);
        }
    }
}
