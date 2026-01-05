package com.lucast.vetcare.fiscal.enums;

public enum TipoConsultaCadastroEnum {

    IE("0", "Inscrição Estadual do contribuinte"),
    CNPJ("1", "CNPJ do contribuinte"),
    CPF("2", "CPF do contribuinte");

    private final String codigo;
    private final String descricao;

    TipoConsultaCadastroEnum(final String codigo, final String descricao) {
        this.codigo = codigo;
        this.descricao = descricao;
    }

    public String getCodigo() {
        return this.codigo;
    }

    public static TipoConsultaCadastroEnum getByCodigo(String codigo) {
        for (TipoConsultaCadastroEnum e : values()) {
            if (e.codigo.equals(codigo)) return e;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public String toString() {
        return codigo + " - " + descricao;
    }

}
