package com.lucast.vetcare.fiscal.nfe.xml.signature;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignatureType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {
    "signedInfo",
    "signatureValue",
    "keyInfo"
})
public class SignatureType {

    @XmlElement(name = "SignedInfo", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected SignedInfoType signedInfo;
    @XmlElement(name = "SignatureValue", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected SignatureValueType signatureValue;
    @XmlElement(name = "KeyInfo", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected KeyInfoType keyInfo;
    @XmlAttribute(name = "Id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;


    public SignedInfoType getSignedInfo() {
        return signedInfo;
    }

    public void setSignedInfo(SignedInfoType value) {
        this.signedInfo = value;
    }

    public SignatureValueType getSignatureValue() {
        return signatureValue;
    }

    public void setSignatureValue(SignatureValueType value) {
        this.signatureValue = value;
    }

    public KeyInfoType getKeyInfo() {
        return keyInfo;
    }

    public void setKeyInfo(KeyInfoType value) {
        this.keyInfo = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

}