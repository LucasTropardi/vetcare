package com.lucast.vetcare.fiscal.infra.sefaz;

import com.lucast.vetcare.fiscal.application.port.out.SefazGateway;
import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.TipoServicoEnum;
import com.lucast.vetcare.fiscal.util.FiscalUtils;
import org.springframework.stereotype.Component;

@Component
public class FiscalUtilsSefazGateway implements SefazGateway {

    @Override
    public String getUrlNFe(TipoServicoEnum tipoServico, String uf, String tipoAmbiente, String tipoEmissao) {
        return FiscalUtils.getUrlNFe(tipoServico, uf, tipoAmbiente, tipoEmissao);
    }

    @Override
    public String getUrlNFCe(TipoServicoEnum tipoServico, String uf, Integer ambiente) {
        return FiscalUtils.getUrlNFCe(tipoServico, uf, ambiente);
    }

    @Override
    public String consulta(String url, String xml, Certificado certificado, String soapAction) {
        return FiscalUtils.consulta(url, xml, certificado, soapAction);
    }

    @Override
    public String pegaTag(String xml, String tag) {
        return FiscalUtils.pegaTag(xml, tag);
    }

    @Override
    public String pegaTag2(String xml, String tag) {
        return FiscalUtils.pegaTag2(xml, tag);
    }

    @Override
    public String removeXmlTag(String conteudoXml) {
        return FiscalUtils.removeXMLTag(conteudoXml);
    }

    @Override
    public String strZero(int value, int length) {
        return FiscalUtils.strZero(value, length);
    }

    @Override
    public Integer ufToCodUf(String uf) {
        return FiscalUtils.ufToCodUf(uf);
    }
}
