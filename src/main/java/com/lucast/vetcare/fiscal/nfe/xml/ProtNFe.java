package com.lucast.vetcare.fiscal.nfe.xml;

import jakarta.xml.bind.annotation.*;

import com.lucast.vetcare.fiscal.nfe.xml.signature.SignatureType;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TProtNFe", namespace = "http://www.portalfiscal.inf.br/nfe", propOrder = {
    "infProt",
    "signature"
})
@XmlRootElement(name = "protNFe", namespace = "http://www.portalfiscal.inf.br/nfe")
public class ProtNFe {

    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
    protected ProtNFe.InfProt infProt;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected SignatureType signature;
    @XmlAttribute(name = "versao", required = true)
    protected String versao;

    public ProtNFe.InfProt getInfProt() {
        return infProt;
    }

    public void setInfProt(ProtNFe.InfProt value) {
        this.infProt = value;
    }

    public SignatureType getSignature() {
        return signature;
    }

    public void setSignature(SignatureType value) {
        this.signature = value;
    }

    public String getVersao() {
        return versao;
    }

    public void setVersao(String value) {
        this.versao = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "tpAmb",
        "verAplic",
        "chNFe",
        "dhRecbto",
        "nProt",
        "digVal",
        "cStat",
        "xMotivo",
        "cMsg",
        "xMsg"
    })
    public static class InfProt {

        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
        protected String tpAmb;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
        protected String verAplic;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
        protected String chNFe;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
        protected String dhRecbto;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected String nProt;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected byte[] digVal;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
        protected String cStat;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
        protected String xMotivo;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected String cMsg;
        @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
        protected String xMsg;
        @XmlAttribute(name = "Id")
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;

        public String getTpAmb() {
            return tpAmb;
        }

        public void setTpAmb(String value) {
            this.tpAmb = value;
        }

        public String getVerAplic() {
            return verAplic;
        }

        public void setVerAplic(String value) {
            this.verAplic = value;
        }

        public String getChNFe() {
            return chNFe;
        }

        public void setChNFe(String value) {
            this.chNFe = value;
        }

        public String getDhRecbto() {
            return dhRecbto;
        }

        public void setDhRecbto(String value) {
            this.dhRecbto = value;
        }

        public String getNProt() {
            return nProt;
        }

        public void setNProt(String value) {
            this.nProt = value;
        }

        public byte[] getDigVal() {
            return digVal;
        }

        public void setDigVal(byte[] value) {
            this.digVal = value;
        }

        public String getCStat() {
            return cStat;
        }

        public void setCStat(String value) {
            this.cStat = value;
        }

        public String getXMotivo() {
            return xMotivo;
        }

        public void setXMotivo(String value) {
            this.xMotivo = value;
        }

        public String getCMsg() {
            return cMsg;
        }

        public void setCMsg(String value) {
            this.cMsg = value;
        }

        public String getXMsg() {
            return xMsg;
        }

        public void setXMsg(String value) {
            this.xMsg = value;
        }

        public String getId() {
            return id;
        }

        public void setId(String value) {
            this.id = value;
        }

    }
}