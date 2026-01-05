package com.lucast.vetcare.fiscal.api;

import com.lucast.vetcare.fiscal.api.dto.NfceSendRequest;
import com.lucast.vetcare.fiscal.api.dto.NfceSignRequest;
import com.lucast.vetcare.fiscal.api.dto.NfceValidateRequest;
import com.lucast.vetcare.fiscal.enums.AssinaturaEnum;
import com.lucast.vetcare.fiscal.enums.ServicosNFeEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfce.NfceIssuanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/fiscal/nfce")
@RequiredArgsConstructor
public class NfceIssuanceController {

    private final NfceIssuanceService service;

    @PostMapping("/validate")
    public ResponseEntity<?> validate(@RequestBody NfceValidateRequest req) throws FiscalException {
        var servico = ServicosNFeEnum.valueOf(req.servico());
        return ResponseEntity.ok(Map.of("valid", service.validate(req.xml(), servico)));
    }

    @PostMapping("/sign")
    public ResponseEntity<?> sign(@RequestBody NfceSignRequest req) throws FiscalException {
        var cert = service.buildCertFromBase64Pfx(req.certBase64(), req.certPassword());
        var tipo = AssinaturaEnum.valueOf(req.tipoAssinatura());
        return ResponseEntity.ok(Map.of("xml", service.sign(req.xml(), cert, tipo)));
    }

    @PostMapping("/send")
    public ResponseEntity<?> send(@RequestBody NfceSendRequest req) throws FiscalException {
        var cert = service.buildCertFromBase64Pfx(req.certBase64(), req.certPassword());
        return ResponseEntity.ok(
                service.send(req.xml(), req.numeroLote(), req.codigoUF(), req.ambiente(), cert)
        );
    }
}
