package com.lucast.vetcare.fiscal.nfce;

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
import com.lucast.vetcare.fiscal.util.impressao.Impressao;
import com.lucast.vetcare.fiscal.util.impressao.ImpressaoService;
import com.lucast.vetcare.fiscal.util.impressao.ImpressaoUtil;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class CupomFiscal {

    public CupomFiscal() {}

    public static String assinaNFCe(String xml,Certificado certificado) throws FiscalException, Exception {
        return new AssinarNFCe().assinaNfce(xml, certificado, AssinaturaEnum.NFE);
    }

    public static Boolean validaNFCe(String xmlAssinado, ServicosNFeEnum servico) throws FiscalException, Exception {
        return new ValidaNFCe().validaXml(xmlAssinado, servico);
    }

    public static ArrayList<String> enviaNFCe(String xml, Long numeroLote, String codigoUF, TipoAmbienteEnum tipoAmbiente, Certificado certificado) throws FiscalException, Exception{
        return new EnviaNFCe().enviaNFCe(xml, numeroLote, codigoUF, tipoAmbiente, certificado);
    }

    public static ArrayList<String> consultaNFCe(String uf, TipoAmbienteEnum tipoAmbiente, String chaveNFe, Certificado certificado) throws FiscalException {
        return new ConsultaNFCe().consultaNFCe(uf, tipoAmbiente, chaveNFe, certificado);
    }

    public static String imprimirNFCe(String xml, String urlConsulta, BufferedImage logo) throws Exception {
        Impressao imp = ImpressaoUtil.impressaoPadraoNFCe(xml, urlConsulta, logo);
        return ImpressaoService.impressaoPdfBase64(imp);
    }

    public static ArrayList<String> consultaStatus(String uf, String url, TipoAmbienteEnum tipoAmbienteEnum, Certificado certificado) throws FiscalException {
        return new ConsultaStatusService().consultaStatus(uf, url, tipoAmbienteEnum, certificado);
    }

    public static ArrayList<String> cancelarNFCe(String justificativa, String uf, String cnpj, String chaveNFCe, String protocolo, LocalDateTime dataEvento, String tipoEmissao, TipoAmbienteEnum tipoAmbiente, Certificado certificado) throws FiscalException {
        return new CancelarNFCe().cancelarNFCe(justificativa, uf, cnpj, chaveNFCe, protocolo, dataEvento, tipoEmissao, tipoAmbiente, certificado);
    }
}
