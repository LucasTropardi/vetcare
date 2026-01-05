package com.lucast.vetcare.fiscal.nfe.xml.location;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TEndereco", namespace = "http://www.portalfiscal.inf.br/nfe", propOrder = {
    "xLgr",
    "nro",
    "xCpl",
    "xBairro",
    "cMun",
    "xMun",
    "uf",
    "cep",
    "cPais",
    "xPais",
    "fone"
})
public class Endereco {

    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
    protected String xLgr;
    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
    protected String nro;
    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
    protected String xCpl;
    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
    protected String xBairro;
    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
    protected String cMun;
    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
    protected String xMun;
    @XmlElement(name = "UF", namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
    @XmlSchemaType(name = "string")
    protected Uf uf;
    @XmlElement(name = "CEP", namespace = "http://www.portalfiscal.inf.br/nfe")
    protected String cep;
    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
    protected String cPais;
    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
    protected String xPais;
    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe")
    protected String fone;
    
    public String getXLgr() {
        return xLgr;
    }
    
    public void setXLgr(String value) {
        this.xLgr = value;
    }
    
    public String getNro() {
        return nro;
    }
    
    public void setNro(String value) {
        this.nro = value;
    }

    public String getXCpl() {
        return xCpl;
    }
    
    public void setXCpl(String value) {
        this.xCpl = value;
    }
    
    public String getXBairro() {
        return xBairro;
    }

    public void setXBairro(String value) {
        this.xBairro = value;
    }

    public String getCMun() {
        return cMun;
    }

    public void setCMun(String value) {
        this.cMun = value;
    }

    public String getXMun() {
        return xMun;
    }

    public void setXMun(String value) {
        this.xMun = value;
    }
    
    public Uf getUF() {
        return uf;
    }
    
    public void setUF(Uf value) {
        this.uf = value;
    }
    
    public String getCEP() {
        return cep;
    }

    public void setCEP(String value) {
        this.cep = value;
    }

    public String getCPais() {
        return cPais;
    }

    public void setCPais(String value) {
        this.cPais = value;
    }

    public String getXPais() {
        return xPais;
    }
    
    public void setXPais(String value) {
        this.xPais = value;
    }

    public String getFone() {
        return fone;
    }
    
    public void setFone(String value) {
        this.fone = value;
    }
}