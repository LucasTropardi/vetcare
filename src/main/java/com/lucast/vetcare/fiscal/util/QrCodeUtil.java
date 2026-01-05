package com.lucast.vetcare.fiscal.util;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.util.Base64;
import java.util.Collection;
import java.util.Optional;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.certificado.CertificadoService;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;

public class QrCodeUtil {

	public String criaQrCode(String uf, Certificado certificado, String chaveCte, String tipoAmbiente) throws FiscalException {
		StringBuilder qrCode = new StringBuilder();
        qrCode.append(getUrlQrCodeCTe(uf, Integer.parseInt(tipoAmbiente)));
        qrCode.append("?chCTe=");
        qrCode.append(chaveCte);
        qrCode.append("&tpAmb=");
        qrCode.append(tipoAmbiente);
        if (chaveCte.charAt(34) == '2') {
            qrCode.append("&sign=");
            try {
                qrCode.append(assinaSign(chaveCte, certificado));
            } catch (Exception e) {
                throw new FiscalException("Erro ao assinar Chave contingencia: ", e.getMessage());
            }
        }

        return qrCode.toString();
	}
	
	private static String assinaSign(String id, Certificado certificado) throws Exception {

		KeyStore keyStore = CertificadoService.getKeyStore(certificado);
		KeyStore.PrivateKeyEntry pkEntry = (KeyStore.PrivateKeyEntry) keyStore.getEntry(certificado.getNome(),
		        new KeyStore.PasswordProtection(verifica(certificado.getSenha()).orElse("").toCharArray()));
		byte[] data = id.getBytes(StandardCharsets.UTF_8);
		
		Signature sig = Signature.getInstance("SHA1WithRSA");
		sig.initSign(pkEntry.getPrivateKey());
		sig.update(data);
		byte[] signatureBytes = sig.sign();
		return (Base64.getEncoder().encodeToString(signatureBytes))
		        .replace("&#13;", "")
		        .replace("\r\n", "")
		        .replace("\n", "")
		        .replace(System.lineSeparator(), "");
	}
	
	public static <T> Optional<T> verifica(T obj) {
        if (obj == null)
            return Optional.empty();
        if (obj instanceof Collection)
            return ((Collection<?>) obj).isEmpty() ? Optional.empty() : Optional.of(obj);

        final String s = String.valueOf(obj).trim();

        return s.isEmpty() || s.equalsIgnoreCase("null") ? Optional.empty() : Optional.of(obj);
    }

	private String getUrlQrCodeCTe(String uf, Integer tipoAmbiente) {
		String url = "";
		if (uf.equals("MG")) {
			url = tipoAmbiente == 1 ? "https://cte.fazenda.mg.gov.br/portalcte/sistema/qrcode.xhtml" : "https://cte.fazenda.mg.gov.br/portalcte/sistema/qrcode.xhtml";
		} else if (uf.equals("MS")) {
			url = tipoAmbiente == 1 ? "http://www.dfe.ms.gov.br/cte/qrcode" : "http://www.dfe.ms.gov.br/cte/qrcode";
		}else if (uf.equals("MT")) {
			url = tipoAmbiente == 1 ? "https://www.sefaz.mt.gov.br/cte/qrcode" : "https://homologacao.sefaz.mt.gov.br/cte/qrcode";
		}else if (uf.equals("PR")) {
			url = tipoAmbiente == 1 ? "http://www.fazenda.pr.gov.br/cte/qrcode" : "http://www.fazenda.pr.gov.br/cte/qrcode";
		}else if (uf.equals("SP")) {
			url = tipoAmbiente == 1 ? "https://nfe.fazenda.sp.gov.br/CTeConsulta/qrCode" : "https://homologacao.nfe.fazenda.sp.gov.br/CTeConsulta/qrCode";
		}else if (uf.equals("RS")) {
			url = tipoAmbiente == 1 ? "https://dfe-portal.svrs.rs.gov.br/cte/qrCode" : "https://dfe-portal.svrs.rs.gov.br/cte/qrCode";
		}else if (uf.equals("RO")) {
            url = tipoAmbiente == 1 ? "https://dfe-portal.svrs.rs.gov.br/cte/qrCode" : "https://dfe-portal.svrs.rs.gov.br/cte/qrCode";
        } else {
            throw new IllegalArgumentException("UF não suportada para consulta de QR Code: " + uf);
        }
		return url;
	}

	public String criaQrCodeMDFe(TipoAmbienteEnum tipoAmbiente, String chaveMDFe) {
		StringBuilder qrCode = new StringBuilder();
		qrCode.append(tipoAmbiente.getCodigo().equals("1") ? "https://dfe-portal.svrs.rs.gov.br/mdfe/qrCode" : "https://dfe-portal.svrs.rs.gov.br/mdfe/qrCode");
		qrCode.append("?chMDFe=");
		qrCode.append(chaveMDFe);
		qrCode.append("&tpAmb=");
		qrCode.append(tipoAmbiente.getCodigo());

		return qrCode.toString();
	}
}