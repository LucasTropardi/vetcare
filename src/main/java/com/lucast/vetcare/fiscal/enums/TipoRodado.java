package com.lucast.vetcare.fiscal.enums;

public enum TipoRodado {

	TRUCK("01", "Truck"),
    TOCO("02", "Toco"),
    CAVALO_MECANICO("03", "Cavalo Mecânico"),
    VAN("04", "VAN"),
    UTILITARIO("05", "Utilitário"),
    OUTROS("06", "Outros");

    private final String codigo;
    private final String descricao;

    TipoRodado(final String codigo, final String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public static TipoRodado valueOfCodigo(final String codigo) {
        for (TipoRodado tipoUnidadeTransporte : TipoRodado.values()) {
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
