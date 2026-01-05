package com.lucast.vetcare.fiscal.nfe.xml;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TInfRespTec", namespace = "http://www.portalfiscal.inf.br/nfe", propOrder = {
    "cnpj",
    "xContato",
    "email",
    "fone",
    "idCSRT",
    "hashCSRT"
})
public class InfRespTec {

    @XmlElement(name = "CNPJ", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
    protected String cnpj;
    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
    protected String xContato;
    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
    protected String email;
    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
    protected String fone;
    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
    protected String idCSRT;
    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
    protected byte[] hashCSRT;

    public String getCNPJ() {
        return cnpj;
    }

    public void setCNPJ(String value) {
        this.cnpj = value;
    }

    public String getXContato() {
        return xContato;
    }

    public void setXContato(String value) {
        this.xContato = value;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String value) {
        this.email = value;
    }

    public String getFone() {
        return fone;
    }

    public void setFone(String value) {
        this.fone = value;
    }

    public String getIdCSRT() {
        return idCSRT;
    }

    public void setIdCSRT(String value) {
        this.idCSRT = value;
    }

    public byte[] getHashCSRT() {
        return hashCSRT;
    }

    public void setHashCSRT(byte[] value) {
        this.hashCSRT = value;
    }

}

