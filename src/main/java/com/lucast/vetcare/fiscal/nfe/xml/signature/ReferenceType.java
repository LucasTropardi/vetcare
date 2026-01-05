package com.lucast.vetcare.fiscal.nfe.xml.signature;

import jakarta.xml.bind.annotation.*;
import jakarta.xml.bind.annotation.adapters.CollapsedStringAdapter;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReferenceType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {
    "transforms",
    "digestMethod",
    "digestValue"
})
public class ReferenceType {

    @XmlElement(name = "Transforms", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected TransformsType transforms;
    @XmlElement(name = "DigestMethod", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected ReferenceType.DigestMethod digestMethod;
    @XmlElement(name = "DigestValue", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected byte[] digestValue;
    @XmlAttribute(name = "Id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    @XmlSchemaType(name = "ID")
    protected String id;
    @XmlAttribute(name = "URI", required = true)
    protected String uri;
    @XmlAttribute(name = "Type")
    @XmlSchemaType(name = "anyURI")
    protected String type;

    public TransformsType getTransforms() {
        return transforms;
    }

    public void setTransforms(TransformsType value) {
        this.transforms = value;
    }

    public ReferenceType.DigestMethod getDigestMethod() {
        return digestMethod;
    }

    public void setDigestMethod(ReferenceType.DigestMethod value) {
        this.digestMethod = value;
    }

    public byte[] getDigestValue() {
        return digestValue;
    }

    public void setDigestValue(byte[] value) {
        this.digestValue = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

    public String getURI() {
        return uri;
    }

    public void setURI(String value) {
        this.uri = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String value) {
        this.type = value;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class DigestMethod {

        @XmlAttribute(name = "Algorithm", required = true)
        @XmlSchemaType(name = "anyURI")
        protected String algorithm;

        public String getAlgorithm() {
            if (algorithm == null) {
                return "http://www.w3.org/2000/09/xmldsig#sha1";
            } else {
                return algorithm;
            }
        }

        public void setAlgorithm(String value) {
            this.algorithm = value;
        }

    }
}