package com.lucast.vetcare.fiscal.enums;

public enum TipoInfPag {

	A_VISTA("0", "Pagamento à Vista"),
    A_PRAZO("1", "Pagamento a Prazo");

    private final String codigo;
    private final String descricao;

    TipoInfPag(final String codigo, final String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public static TipoInfPag valueOfCodigo(final String codigo) {
        for (TipoInfPag tipoEmitente : TipoInfPag.values()) {
            if (tipoEmitente.getCodigo().equalsIgnoreCase(codigo)) {
                return tipoEmitente;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}