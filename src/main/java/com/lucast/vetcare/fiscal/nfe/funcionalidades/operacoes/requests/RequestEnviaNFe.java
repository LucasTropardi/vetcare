package com.lucast.vetcare.fiscal.nfe.funcionalidades.operacoes.requests;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;

public class RequestEnviaNFe {

    private final String xml;
    private final Long numeroLote;
    private final String tipoEmissao;
    private final String codigoUF;
    private final TipoAmbienteEnum tipoAmbiente;
    private final int indSinc;
    private final Certificado certificado;

    private RequestEnviaNFe(Builder builder) {
        this.xml = builder.xml;
        this.numeroLote = builder.numeroLote;
        this.tipoEmissao = builder.tipoEmissao;
        this.codigoUF = builder.codigoUF;
        this.tipoAmbiente = builder.tipoAmbiente;
        this.indSinc = builder.indSinc;
        this.certificado = builder.certificado;
    }

    public String getXml() { return xml; }
    public Long getNumeroLote() { return numeroLote; }
    public String getTipoEmissao() { return tipoEmissao; }
    public String getCodigoUF() { return codigoUF; }
    public TipoAmbienteEnum getTipoAmbiente() { return tipoAmbiente; }
    public int getIndSinc() { return indSinc; }
    public Certificado getCertificado() { return certificado; }

    public static class Builder {
        private String xml;
        private Long numeroLote;
        private String tipoEmissao;
        private String codigoUF;
        private TipoAmbienteEnum tipoAmbiente;
        private int indSinc;
        private Certificado certificado;

        public Builder xml(String xml) {
            this.xml = xml;
            return this;
        }

        public Builder numeroLote(Long numeroLote) {
            this.numeroLote = numeroLote;
            return this;
        }

        public Builder tipoEmissao(String tipoEmissao) {
            this.tipoEmissao = tipoEmissao;
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

        public Builder indSinc(int indSinc) {
            this.indSinc = indSinc;
            return this;
        }

        public Builder certificado(Certificado certificado) {
            this.certificado = certificado;
            return this;
        }

        public RequestEnviaNFe build() {
            return new RequestEnviaNFe(this);
        }
    }
}
