package com.lucast.vetcare.fiscal.recipient;

public record RecipientData(
        String cnpj,
        String cpf,
        String xNome,
        String xFant,
        String ie,
        String indIeDest,
        String phone,
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
