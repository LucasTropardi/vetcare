package com.lucast.vetcare.fiscal.nfe.xml.signature;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransformType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {
    "xPath"
})
public class TransformType {

    @XmlElement(name = "XPath", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected List<String> xPath;
    @XmlAttribute(name = "Algorithm", required = true)
    protected String algorithm;

    public List<String> getXPath() {
        if (xPath == null) {
            xPath = new ArrayList<String>();
        }
        return this.xPath;
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String value) {
        this.algorithm = value;
    }
}