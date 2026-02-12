package com.lucast.vetcare.fiscal.application.port.out;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.TipoServicoEnum;

public interface SefazGateway {

    String getUrlNFe(TipoServicoEnum tipoServico, String uf, String tipoAmbiente, String tipoEmissao);

    String getUrlNFCe(TipoServicoEnum tipoServico, String uf, Integer ambiente);

    String consulta(String url, String xml, Certificado certificado, String soapAction);

    String pegaTag(String xml, String tag);

    String pegaTag2(String xml, String tag);

    String removeXmlTag(String conteudoXml);

    String strZero(int value, int length);

    Integer ufToCodUf(String uf);
}
