package com.lucast.vetcare.fiscal.nfe.xml.location;
import jakarta.xml.bind.annotation.*;

@XmlType(name = "TUf", namespace = "http://www.portalfiscal.inf.br/nfe")
@XmlEnum
public enum Uf {
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
    TO,
    EX;

    public String value() {
        return name();
    }

    public static Uf fromValue(String v) {
        return valueOf(v);
    }
}