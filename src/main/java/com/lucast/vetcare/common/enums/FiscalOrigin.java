package com.lucast.vetcare.common.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Arrays;

public enum FiscalOrigin {
    NATIONAL("0", "Nacional"),
    FOREIGN_DIRECT("1", "Estrangeira - importação direta"),
    FOREIGN_INTERNAL("2", "Estrangeira - adquirida no mercado interno"),
    NATIONAL_40("3", "Nacional - conteúdo importação > 40%"),
    NATIONAL_70("4", "Nacional - produção conforme processo produtivo básico"),
    NATIONAL_LESS_40("5", "Nacional - outros casos"),
    FOREIGN_DIRECT_NO_SIMILAR("6", "Estrangeira - importação direta (sem similar)"),
    FOREIGN_INTERNAL_NO_SIMILAR("7", "Estrangeira - adquirida no mercado interno (sem similar)");

    private final String code;
    private final String label;

    FiscalOrigin(String code, String label) {
        this.code = code;
        this.label = label;
    }

    @JsonValue
    public String getCode() { return code; }

    public String getLabel() { return label; }

    @JsonCreator
    public static FiscalOrigin fromCode(String code) {
        if (code == null) return null;
        return Arrays.stream(values())
                .filter(o -> o.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid fiscal origin: " + code));
    }
}
