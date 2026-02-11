package com.lucast.vetcare.fiscal.certificado;

import com.lucast.vetcare.fiscal.exception.FiscalException;
import org.springframework.stereotype.Service;

@Service
public class CertificadoLoaderService {

    public Certificado fromBase64Pfx(String pfxBase64, String senha) throws FiscalException {
        try {
            byte[] pfxBytes = java.util.Base64.getDecoder().decode(pfxBase64);
            return CertificadoService.certificadoPfxBytes(pfxBytes, senha);
        } catch (IllegalArgumentException e) {
            throw new FiscalException("Erro", "certBase64 invalido (nao e base64): " + e.getMessage());
        } catch (FiscalException e) {
            throw e;
        } catch (Exception e) {
            throw new FiscalException("Erro", "Falha ao carregar certificado PFX: " + e.getMessage());
        }
    }
}
