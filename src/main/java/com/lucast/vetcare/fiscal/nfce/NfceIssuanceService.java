package com.lucast.vetcare.fiscal.nfce;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.certificado.CertificadoLoaderService;
import com.lucast.vetcare.fiscal.enums.AssinaturaEnum;
import com.lucast.vetcare.fiscal.enums.ServicosNFeEnum;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfce.funcionalidades.AssinarNFCe;
import com.lucast.vetcare.fiscal.nfce.funcionalidades.EnviaNFCe;
import com.lucast.vetcare.fiscal.nfce.funcionalidades.ValidaNFCe;
import com.lucast.vetcare.fiscal.nfce.result.NfceAuthorizationResult;
import org.springframework.stereotype.Service;

@Service
public class NfceIssuanceService {

    private final ValidaNFCe valida;
    private final AssinarNFCe assinar;
    private final EnviaNFCe enviar;
    private final CertificadoLoaderService certificadoLoaderService;

    public NfceIssuanceService(
            ValidaNFCe valida,
            AssinarNFCe assinar,
            EnviaNFCe enviar,
            CertificadoLoaderService certificadoLoaderService
    ) {
        this.valida = valida;
        this.assinar = assinar;
        this.enviar = enviar;
        this.certificadoLoaderService = certificadoLoaderService;
    }

    public Certificado buildCertFromBase64Pfx(String pfxBase64, String senha) throws FiscalException {
        return certificadoLoaderService.fromBase64Pfx(pfxBase64, senha);
    }

    public boolean validate(String xml, ServicosNFeEnum servico) throws FiscalException {
        return valida.validaXml(xml, servico);
    }

    public String sign(String xml, Certificado cert, AssinaturaEnum tipo) throws FiscalException {
        try {
            return assinar.assinaNfce(xml, cert, tipo);
        } catch (FiscalException e) {
            throw e;
        } catch (Exception e) {
            throw new FiscalException("Erro", "Falha ao assinar NFC-e: " + e.getMessage());
        }
    }

    public java.util.ArrayList<String> send(
            String xml, Long lote, String codigoUF,
            TipoAmbienteEnum amb, Certificado cert
    ) throws FiscalException {
        return sendResult(xml, lote, codigoUF, amb, cert).toLegacyList();
    }

    public NfceAuthorizationResult sendResult(
            String xml, Long lote, String codigoUF,
            TipoAmbienteEnum amb, Certificado cert
    ) throws FiscalException {
        try {
            return enviar.enviaNfceResult(xml, lote, codigoUF, amb, cert);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FiscalException("Erro", "Envio NFC-e interrompido: " + e.getMessage());
        }
    }
}
