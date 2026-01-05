package com.lucast.vetcare.fiscal.nfce;

import com.lucast.vetcare.fiscal.certificado.Certificado;
import com.lucast.vetcare.fiscal.enums.AssinaturaEnum;
import com.lucast.vetcare.fiscal.enums.ServicosNFeEnum;
import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfce.funcionalidades.AssinarNFCe;
import com.lucast.vetcare.fiscal.nfce.funcionalidades.EnviaNFCe;
import com.lucast.vetcare.fiscal.nfce.funcionalidades.ValidaNFCe;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class NfceIssuanceService {

    private final ValidaNFCe valida = new ValidaNFCe();
    private final AssinarNFCe assinar = new AssinarNFCe();
    private final EnviaNFCe enviar = new EnviaNFCe();

    public Certificado buildCertFromBase64Pfx(String pfxBase64, String senha) throws FiscalException {
        try {
            byte[] pfxBytes = java.util.Base64.getDecoder().decode(pfxBase64);
            return new Certificado().getCertificado(pfxBytes, senha); // <-- aqui ele popula nome/alias etc.
        } catch (IllegalArgumentException e) {
            throw new FiscalException("Erro", "certBase64 inválido (não é base64): " + e.getMessage());
        } catch (FiscalException e) {
            throw e;
        } catch (Exception e) {
            throw new FiscalException("Erro", "Falha ao carregar certificado PFX: " + e.getMessage());
        }
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
        try {
            return enviar.enviaNFCe(xml, lote, codigoUF, amb, cert);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new FiscalException("Erro", "Envio NFC-e interrompido: " + e.getMessage());
        }
    }
}
