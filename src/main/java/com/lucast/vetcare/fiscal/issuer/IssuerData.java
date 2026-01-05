package com.lucast.vetcare.fiscal.issuer;

public record IssuerData(
        String cnpj,
        String xNome,
        String xFant,
        String ie,
        String crt,
        Endereco endereco
) {
    public record Endereco(
            String xLgr,
            String nro,
            String xCpl,
            String xBairro,
            String cMun,
            String xMun,
            String uf,
            String cep
    ) {}
}
