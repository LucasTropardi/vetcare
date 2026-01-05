package com.lucast.vetcare.fiscal.enums;

public enum TipoCarroceria {
	NAO_APLICAVEL("00", "Não aplicável"),
    ABERTA("01", "Aberta"),
    FECHADA_BAU("02", "Fechada/Baú"),
    GRANELERA("03", "Granelera"),
    PORTA_CONTAINER("04", "Porta Container"),
    SIDER("05", "Sider");

    private final String codigo;
    private final String descricao;

    TipoCarroceria(final String codigo, final String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public static TipoCarroceria valueOfCodigo(final String codigo) {
        for (TipoCarroceria tipoUnidadeTransporte : TipoCarroceria.values()) {
            if (tipoUnidadeTransporte.getCodigo().equalsIgnoreCase(codigo)) {
                return tipoUnidadeTransporte;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }
}
