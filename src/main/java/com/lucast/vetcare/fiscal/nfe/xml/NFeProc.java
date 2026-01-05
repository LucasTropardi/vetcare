package com.lucast.vetcare.fiscal.nfe.xml;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TNfeProc", namespace = "http://www.portalfiscal.inf.br/nfe", propOrder = {
    "nFe",
    "protNFe"
})
public class NFeProc {

    @XmlElement(name = "NFe", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
    protected NFe nFe;
    @XmlElement(name = "protNFe", namespace = "http://www.portalfiscal.inf.br/nfe")
    protected ProtNFe protNFe;
    @XmlAttribute(name = "versao", required = true)
    protected String versao;

    public NFe getNFe() {
        return nFe;
    }

    public void setNFe(NFe value) {
        this.nFe = value;
    }

    public ProtNFe getProtNFe() {
        return protNFe;
    }

    public void setProtNFe(ProtNFe value) {
        this.protNFe = value;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String value) {
        this.versao = value;
    }

}