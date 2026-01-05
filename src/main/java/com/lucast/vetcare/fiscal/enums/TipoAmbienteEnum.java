package com.lucast.vetcare.fiscal.enums;

public enum TipoAmbienteEnum {

    HOMOLOGACAO("2"),
    PRODUCAO("1");

    private final String codigo;

    TipoAmbienteEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static TipoAmbienteEnum getByCodigo(String codigo) {
        for (TipoAmbienteEnum e : values()) {
            if (e.codigo.equals(codigo)) return e;
        }
        throw new IllegalArgumentException();
    }
}
