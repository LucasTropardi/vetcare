package com.lucast.vetcare.common.enums;

public enum FiscalOrigin {
    NATIONAL("0", "Nacional"),
    FOREIGN_DIRECT("1", "Estrangeira - importação direta"),
    FOREIGN_INTERNAL("2", "Estrangeira - adquirida no mercado interno"),
    NATIONAL_40("3", "Nacional - conteúdo importação > 40%"),
    NATIONAL_70("4", "Nacional - produção conforme processo produtivo básico"),
    NATIONAL_LESS_40("5", "5 (outros casos nacionais)"),
    FOREIGN_DIRECT_NO_SIMILAR("6", "Estrangeira - importação direta (sem similar)"),
    FOREIGN_INTERNAL_NO_SIMILAR("7", "Estrangeira - adquirida no mercado interno (sem similar)");

    private final String code;
    private final String label;

    FiscalOrigin(String code, String label) {
        this.code = code;
        this.label = label;
    }

    public String getCode() { return code; }
    public String getLabel() { return label; }

}
