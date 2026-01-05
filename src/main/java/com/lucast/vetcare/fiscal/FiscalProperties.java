package com.lucast.vetcare.fiscal;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.fiscal")
public class FiscalProperties {

    private String ambiente = "HOMOLOGACAO";
    private String uf = "SP";

    private Certificado certificado = new Certificado();
    private Nfce nfce = new Nfce();
    private Emitente emitente = new Emitente();

    @Data
    public static class Certificado {
        private String tipo = "A1";
        private String caminho = "";
        private String senha = "";
    }

    @Data
    public static class Nfce {
        private String idTokenCsc = "";
        private String csc = "";
    }

    @Data
    public static class Emitente {
        private String cnpj = "";
        private String ie = "";
        private String razaoSocial = "";
        private String fantasia = "";
        private String logradouro = "";
        private String numero = "";
        private String bairro = "";
        private String cMun = "";
        private String xMun = "";
        private String cep = "";
    }
}
