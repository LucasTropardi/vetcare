package com.lucast.vetcare.fiscal.nfe.xml.location;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TVeiculo", namespace = "http://www.portalfiscal.inf.br/nfe", propOrder = {
    "placa",
    "uf",
    "rntc"
})
public class Veiculo {

    @XmlElement(namespace = "http://www.portalfiscal.inf.br/nfe", required = true)
    protected String placa;
    @XmlElement(name = "UF", namespace = "http://www.portalfiscal.inf.br/nfe")
    @XmlSchemaType(name = "string")
    protected Uf uf;
    @XmlElement(name = "RNTC", namespace = "http://www.portalfiscal.inf.br/nfe")
    protected String rntc;

    public String getPlaca() {
        return placa;
    }
    public void setPlaca(String value) {
        this.placa = value;
    }
    
    public Uf getUF() {
        return uf;
    }
    public void setUF(Uf value) {
        this.uf = value;
    }
    
    public String getRNTC() {
        return rntc;
    }
    public void setRNTC(String value) {
        this.rntc = value;
    }
}