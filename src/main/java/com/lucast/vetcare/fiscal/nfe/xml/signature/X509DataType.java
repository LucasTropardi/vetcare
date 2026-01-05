package com.lucast.vetcare.fiscal.nfe.xml.signature;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "X509DataType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {
    "x509Certificate"
})
public class X509DataType {

    @XmlElement(name = "X509Certificate", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected byte[] x509Certificate;

    public byte[] getX509Certificate() {
        return x509Certificate;
    }

    public void setX509Certificate(byte[] value) {
        this.x509Certificate = value;
    }
}
