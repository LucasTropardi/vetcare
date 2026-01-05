package com.lucast.vetcare.fiscal.enums;

public enum TipoServicoEnum {

    // NFe
    STATUS_SERVICO("_STATUSSERVICO"),
    CONSULTA_PROTOCOLO("_CONSULTAPROTOCOLO"),
    RECEPCAO("_RECEPCAO"),
    RET_RECEPCAO("_RETRECEPCAO"),
    CANCELAMENTO("_CANCELAMENTO"),
    INUTILIZACAO("_INUTILIZACAO"),
    CONSULTA_NFE_DEST("_CONSULTANFEDEST"),
    EVENTO("_EVENTO"),
    DOWNLOAD_NFE("_DOWNLOADNFE"),
    REC_EVENTO("_RECPEVENTO"),
    NFE_AUTORIZACAO("_NFeAutorizacao"),
    NFE_RET_AUTORIZACAO("_NFeRetAutorizacao"),
    CONSULTA_CADASTRO("_CONSULTACADASTRO"),

    // CTe
    CTE_RECEPCAO_LOTE("cteRecepcaoLote"),
    CTE_CONSULTA_PROTOCOLO("cteConsultaProtocolo"),
    CTE_STATUS_SERVICO("cteStatusServico"),
    CTE_RECEPCAO_EVENTO("cteRecepcaoEvento"),
    CTE_RECEPCAO_OS("cteRecepcaoOS"),
    CTE_RECEPCAO("CteRecepcao"),
    CTE_RET_RECEPCAO("CteRetRecepcao"),
    CTE_INUTILIZACAO("CteInutilizacao"),
    CTE_CANCELAMENTO("CteCancelamento"),

    // NFCe
    NFCE_RECEPCAO_EVENTO("RecepcaoEvento"),
    NFCE_INUTILIZACAO("Inutilizacao"),
    NFCE_CONSULTA_PROTOCOLO("ConsultaProtocolo"),
    NFCE_NFE_STATUS_SERVICO("NFeStatusServico"),
    NFCE_NFE_AUTORIZACAO("NFeAutorizacao"),
    NFCE_RET_AUTORIZACAO("RetAutorizacao"),
    NFCE_QR_CODE("QRCode"),
    NFCE_CONSULTA_NFCE("ConsultaNFCe"),

    // MDFe
    MDFE_RECEPCAO_LOTE("mdfeRecepcaoLote"),
    MDFE_RET_RECEPCAO("mdfeRetRecepcao"),
    MDFE_RECEPCAO_EVENTO("mdfeRecepcaoEvento"),
    MDFE_CONSULTA_MDF("mdfeConsultaMDF"),
    MDFE_STATUS_SERVICO_MDF("mdfeStatusServicoMDF"),
    MDFE_CONS_NAO_ENC("mdfeConsNaoEnc"),
    MDFE_DISTRIBUICAO_DFE("MDFeDistribuicaoDFe"),
    MDFE_RECEPCAO_SINC("MDFeRecepcaoSinc");

    private final String codigo;

    TipoServicoEnum(String codigo) {
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }

    public static TipoServicoEnum fromCodigo(String codigo) {
        if (codigo == null || codigo.isEmpty()) {
            throw new IllegalArgumentException("O código do serviço não pode ser nulo ou vazio");
        }

        for (TipoServicoEnum servico : TipoServicoEnum.values()) {
            if (servico.getCodigo().equalsIgnoreCase(codigo)) {
                return servico;
            }
        }

        throw new IllegalArgumentException("Serviço desconhecido para o código: " + codigo);
    }

}
