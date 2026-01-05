package com.lucast.vetcare.fiscal.util;

public enum EstadoBrasil {
    RONDONIA("RO", 11, "Rondônia"),
    ACRE("AC", 12, "Acre"),
    AMAZONAS("AM", 13, "Amazonas"),
    RORAIMA("RR", 14, "Roraima"),
    PARA("PA", 15, "Pará"),
    AMAPA("AP", 16, "Amapá"),
    TOCANTINS("TO", 17, "Tocantins"),
    MARANHAO("MA", 21, "Maranhão"),
    PIAUI("PI", 22, "Piauí"),
    CEARA("CE", 23, "Ceará"),
    RIO_GRANDE_DO_NORTE("RN", 24, "Rio Grande do Norte"),
    PARAIBA("PB", 25, "Paraíba"),
    PERNAMBUCO("PE", 26, "Pernambuco"),
    ALAGOAS("AL", 27, "Alagoas"),
    SERGIPE("SE", 28, "Sergipe"),
    BAHIA("BA", 29, "Bahia"),
    MINAS_GERAIS("MG", 31, "Minas Gerais"),
    ESPIRITO_SANTO("ES", 32, "Espírito Santo"),
    RIO_DE_JANEIRO("RJ", 33, "Rio de Janeiro"),
    SAO_PAULO("SP", 35, "São Paulo"),
    PARANA("PR", 41, "Paraná"),
    SANTA_CATARINA("SC", 42, "Santa Catarina"),
    RIO_GRANDE_DO_SUL("RS", 43, "Rio Grande do Sul"),
    MATO_GROSSO_DO_SUL("MS", 50, "Mato Grosso do Sul"),
    MATO_GROSSO("MT", 51, "Mato Grosso"),
    GOIAS("GO", 52, "Goiás"),
    DISTRITO_FEDERAL("DF", 53, "Distrito Federal");

    private final String uf;
    private final Integer codigo;
    private final String nome;

    EstadoBrasil(String uf, Integer codigo, String nome) {
        this.uf = uf;
        this.codigo = codigo;
        this.nome = nome;
    }

    public String getUf() {
        return uf;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public String getNome() {
        return nome;
    }

    public static EstadoBrasil fromUf(String uf) {
        for (EstadoBrasil estado : values()) {
            if (estado.uf.equals(uf)) {
                return estado;
            }
        }
        return null;
    }

    public static EstadoBrasil fromCodigo(Integer codigo) {
        for (EstadoBrasil estado : values()) {
            if (estado.codigo.equals(codigo)) {
                return estado;
            }
        }
        return null;
    }

    public static Integer ufToCodUf(String uf) {
        EstadoBrasil estado = fromUf(uf);
        return estado != null ? estado.getCodigo() : null;
    }
}
