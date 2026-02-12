package com.lucast.vetcare.fiscal.nfce;

import com.lucast.vetcare.config.SpringContextHolder;
import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.AssinaturaEnum;
import com.lucast.vetcare.fiscal.enums.ServicosNFeEnum;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfce.funcionalidades.AssinarNFCe;
import com.lucast.vetcare.fiscal.nfce.funcionalidades.CancelarNFCe;
import com.lucast.vetcare.fiscal.nfce.funcionalidades.ConsultaNFCe;
import com.lucast.vetcare.fiscal.nfce.funcionalidades.ConsultaStatusService;
import com.lucast.vetcare.fiscal.nfce.funcionalidades.EnviaNFCe;
import com.lucast.vetcare.fiscal.nfce.funcionalidades.ValidaNFCe;
import com.lucast.vetcare.fiscal.infra.sefaz.FiscalUtilsSefazGateway;
import com.lucast.vetcare.fiscal.util.impressao.Impressao;
import com.lucast.vetcare.fiscal.util.impressao.ImpressaoService;
import com.lucast.vetcare.fiscal.util.impressao.ImpressaoUtil;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Component
public class CupomFiscal {

    private final AssinarNFCe assinarNFCe;
    private final ValidaNFCe validaNFCe;
    private final EnviaNFCe enviaNFCe;
    private final ConsultaNFCe consultaNFCe;
    private final ConsultaStatusService consultaStatusService;
    private final CancelarNFCe cancelarNFCe;
    private static volatile CupomFiscal legacyInstance;

    public CupomFiscal(
            AssinarNFCe assinarNFCe,
            ValidaNFCe validaNFCe,
            EnviaNFCe enviaNFCe,
            ConsultaNFCe consultaNFCe,
            ConsultaStatusService consultaStatusService,
            CancelarNFCe cancelarNFCe
    ) {
        this.assinarNFCe = assinarNFCe;
        this.validaNFCe = validaNFCe;
        this.enviaNFCe = enviaNFCe;
        this.consultaNFCe = consultaNFCe;
        this.consultaStatusService = consultaStatusService;
        this.cancelarNFCe = cancelarNFCe;
    }

    private static CupomFiscal bean() {
        if (SpringContextHolder.isInitialized()) {
            return SpringContextHolder.getBean(CupomFiscal.class);
        }
        return legacy();
    }

    private static CupomFiscal legacy() {
        if (legacyInstance == null) {
            synchronized (CupomFiscal.class) {
                if (legacyInstance == null) {
                    var sefazGateway = new FiscalUtilsSefazGateway();
                    AssinarNFCe assinar = new AssinarNFCe(sefazGateway);
                    ValidaNFCe valida = new ValidaNFCe();
                    EnviaNFCe envia = new EnviaNFCe(sefazGateway);
                    ConsultaNFCe consulta = new ConsultaNFCe(sefazGateway);
                    ConsultaStatusService consultaStatus = new ConsultaStatusService(sefazGateway);
                    CancelarNFCe cancelar = new CancelarNFCe(sefazGateway, valida, assinar);
                    legacyInstance = new CupomFiscal(assinar, valida, envia, consulta, consultaStatus, cancelar);
                }
            }
        }
        return legacyInstance;
    }

    public static String assinaNFCe(String xml,Certificado certificado) throws FiscalException, Exception {
        return bean().assinarNFCe.assinaNfce(xml, certificado, AssinaturaEnum.NFE);
    }

    public static Boolean validaNFCe(String xmlAssinado, ServicosNFeEnum servico) throws FiscalException, Exception {
        return bean().validaNFCe.validaXml(xmlAssinado, servico);
    }

    public static ArrayList<String> enviaNFCe(String xml, Long numeroLote, String codigoUF, TipoAmbienteEnum tipoAmbiente, Certificado certificado) throws FiscalException, Exception{
        return bean().enviaNFCe.enviaNFCe(xml, numeroLote, codigoUF, tipoAmbiente, certificado);
    }

    public static ArrayList<String> consultaNFCe(String uf, TipoAmbienteEnum tipoAmbiente, String chaveNFe, Certificado certificado) throws FiscalException {
        return bean().consultaNFCe.consultaNFCe(uf, tipoAmbiente, chaveNFe, certificado);
    }

    public static String imprimirNFCe(String xml, String urlConsulta, BufferedImage logo) throws Exception {
        Impressao imp = ImpressaoUtil.impressaoPadraoNFCe(xml, urlConsulta, logo);
        return ImpressaoService.impressaoPdfBase64(imp);
    }

    public static ArrayList<String> consultaStatus(String uf, String url, TipoAmbienteEnum tipoAmbienteEnum, Certificado certificado) throws FiscalException {
        return bean().consultaStatusService.consultaStatus(uf, url, tipoAmbienteEnum, certificado);
    }

    public static ArrayList<String> cancelarNFCe(String justificativa, String uf, String cnpj, String chaveNFCe, String protocolo, LocalDateTime dataEvento, String tipoEmissao, TipoAmbienteEnum tipoAmbiente, Certificado certificado) throws FiscalException {
        return bean().cancelarNFCe.cancelarNFCe(justificativa, uf, cnpj, chaveNFCe, protocolo, dataEvento, tipoEmissao, tipoAmbiente, certificado);
    }
}
