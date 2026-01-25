package com.lucast.vetcare.fiscal.api;

import com.lucast.vetcare.fiscal.api.dto.NfceSendRequest;
import com.lucast.vetcare.fiscal.api.dto.NfceSignRequest;
import com.lucast.vetcare.fiscal.api.dto.NfceValidateRequest;
import com.lucast.vetcare.fiscal.enums.AssinaturaEnum;
import com.lucast.vetcare.fiscal.enums.ServicosNFeEnum;
import com.lucast.vetcare.fiscal.exception.FiscalException;
import com.lucast.vetcare.fiscal.nfce.NfceIssuanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/fiscal/nfce")
@RequiredArgsConstructor
@Tag(
        name = "NFC-e",
        description = "Endpoints for validating, signing and issuing NFC-e (Nota Fiscal do Consumidor Eletrônica)"
)
public class NfceIssuanceController {

    private final NfceIssuanceService service;

    @PostMapping("/validate")
    @Operation(
            summary = "Validate NFC-e XML",
            description = "Validates an NFC-e XML according to the selected SEFAZ service and schema"
    )
    public ResponseEntity<Map<String, Boolean>> validate(
            @RequestBody NfceValidateRequest req
    ) throws FiscalException {

        var servico = ServicosNFeEnum.valueOf(req.servico());
        boolean valid = service.validate(req.xml(), servico);

        return ResponseEntity.ok(Map.of("valid", valid));
    }

    @PostMapping("/sign")
    @Operation(
            summary = "Sign NFC-e XML",
            description = "Digitally signs an NFC-e XML using a Base64-encoded PFX certificate"
    )
    public ResponseEntity<Map<String, String>> sign(
            @RequestBody NfceSignRequest req
    ) throws FiscalException {

        var cert = service.buildCertFromBase64Pfx(
                req.certBase64(),
                req.certPassword()
        );

        var tipo = AssinaturaEnum.valueOf(req.tipoAssinatura());
        String signedXml = service.sign(req.xml(), cert, tipo);

        return ResponseEntity.ok(Map.of("xml", signedXml));
    }

    @PostMapping("/send")
    @Operation(
            summary = "Send NFC-e to SEFAZ",
            description = "Sends a signed NFC-e XML to SEFAZ and returns the processing result"
    )
    public ResponseEntity<?> send(
            @RequestBody NfceSendRequest req
    ) throws FiscalException {

        var cert = service.buildCertFromBase64Pfx(
                req.certBase64(),
                req.certPassword()
        );

        return ResponseEntity.ok(
                service.send(
                        req.xml(),
                        req.numeroLote(),
                        req.codigoUF(),
                        req.ambiente(),
                        cert
                )
        );
    }
}
