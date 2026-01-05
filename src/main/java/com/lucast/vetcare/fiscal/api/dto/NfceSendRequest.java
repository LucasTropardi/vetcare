package com.lucast.vetcare.fiscal.api.dto;

import com.lucast.vetcare.fiscal.enums.TipoAmbienteEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NfceSendRequest(
        @NotBlank String xml,
        @NotNull Long numeroLote,
        @NotBlank String codigoUF,
        @NotNull TipoAmbienteEnum ambiente,
        @NotBlank String certBase64,
        @NotBlank String certPassword
) {}