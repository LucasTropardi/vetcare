package com.lucast.vetcare.fiscal.nfe.xml.signature;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SignedInfoType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {
    "canonicalizationMethod",
    "signatureMethod",
    "reference"
})
public class SignedInfoType {

    @XmlElement(name = "CanonicalizationMethod", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected SignedInfoType.CanonicalizationMethod canonicalizationMethod;
    @XmlElement(name = "SignatureMethod", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected SignedInfoType.SignatureMethod signatureMethod;
    @XmlElement(name = "Reference", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected ReferenceType reference;
    @XmlAttribute(name = "Id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;

    public SignedInfoType.CanonicalizationMethod getCanonicalizationMethod() {
        return canonicalizationMethod;
    }

    public void setCanonicalizationMethod(SignedInfoType.CanonicalizationMethod value) {
        this.canonicalizationMethod = value;
    }

    public SignedInfoType.SignatureMethod getSignatureMethod() {
        return signatureMethod;
    }

    public void setSignatureMethod(SignedInfoType.SignatureMethod value) {
        this.signatureMethod = value;
    }

    public ReferenceType getReference() {
        return reference;
    }

    public void setReference(ReferenceType value) {
        this.reference = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class CanonicalizationMethod {

        @XmlAttribute(name = "Algorithm", required = true)
        @XmlSchemaType(name = "anyURI")
        protected String algorithm;

        public String getAlgorithm() {
            if (algorithm == null) {
                return "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
            } else {
                return algorithm;
            }
        }

        public void setAlgorithm(String value) {
            this.algorithm = value;
        }

    }


    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class SignatureMethod {

        @XmlAttribute(name = "Algorithm", required = true)
        @XmlSchemaType(name = "anyURI")
        protected String algorithm;

        public String getAlgorithm() {
            if (algorithm == null) {
                return "http://www.w3.org/2000/09/xmldsig#rsa-sha1";
            } else {
                return algorithm;
            }
        }

        public void setAlgorithm(String value) {
            this.algorithm = value;
        }

    }
}