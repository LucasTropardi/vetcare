package com.lucast.vetcare.fiscal.enums;

public enum AssinaturaEnum {

    NFE("NFe","infNFe"),
    INUTILIZACAO("infInut","infInut"),
    EVENTO("evento","infEvento"),
    CTE("CTe","infCte"),
    CTE_OS ("CTeOS","infCte"),
    MDFE("MDFe", "infMDFe");

    private final String tipo;
    private final String tag;

    AssinaturaEnum(String tipo,String tag) {
        this.tipo = tipo;
        this.tag = tag;
    }

    public String getTipo() {
        return tipo;
    }
    public String getTag() {
        return tag;
    }
}