package com.lucast.vetcare.fiscal.enums;

public enum TipoProprietario {

    TAC_AGREGADO("0", "TAC – Agregado"),
    TAC_INDEPENDENTE("1", "TAC – Independente"),
    OUTROS("2", "Outros");

    private final String codigo;
    private final String descricao;

    TipoProprietario(final String codigo, final String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public static TipoProprietario valueOfCodigo(final String codigo) {
        for (TipoProprietario tipoUnidadeCarga : TipoProprietario.values()) {
            if (tipoUnidadeCarga.getCodigo().equalsIgnoreCase(codigo)) {
                return tipoUnidadeCarga;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
