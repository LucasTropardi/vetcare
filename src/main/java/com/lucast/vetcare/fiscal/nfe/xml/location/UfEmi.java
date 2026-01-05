package com.lucast.vetcare.fiscal.nfe.xml.location;

import jakarta.xml.bind.annotation.*;

@XmlType(name = "TUfEmi", namespace = "http://www.portalfiscal.inf.br/nfe")
@XmlEnum
public enum UfEmi {

    AC,
    AL,
    AM,
    AP,
    BA,
    CE,
    DF,
    ES,
    GO,
    MA,
    MG,
    MS,
    MT,
    PA,
    PB,
    PE,
    PI,
    PR,
    RJ,
    RN,
    RO,
    RR,
    RS,
    SC,
    SE,
    SP,
    TO;

    public String value() {
        return name();
    }

    public static UfEmi fromValue(String v) {
        return valueOf(v);
    }
}