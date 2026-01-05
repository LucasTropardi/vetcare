package com.lucast.vetcare.fiscal.nfe.xml.signature;

import jakarta.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransformsType", namespace = "http://www.w3.org/2000/09/xmldsig#", propOrder = {
    "transform"
})
public class TransformsType {

    @XmlElement(name = "Transform", namespace = "http://www.w3.org/2000/09/xmldsig#", required = true)
    protected List<TransformType> transform;

    public List<TransformType> getTransform() {
        if (transform == null) {
            transform = new ArrayList<TransformType>();
        }
        return this.transform;
    }
}