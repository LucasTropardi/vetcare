package com.lucast.vetcare.catalog.validation;

import com.lucast.vetcare.catalog.ProductEntity;
import com.lucast.vetcare.catalog.ProductFiscalEntity;
import com.lucast.vetcare.catalog.dto.CreateProductRequest;
import com.lucast.vetcare.catalog.dto.ProductFiscalRequest;
import com.lucast.vetcare.catalog.dto.UpdateProductRequest;
import com.lucast.vetcare.common.enums.ItemType;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Component
public class FiscalRulesValidator {

    public void validateCreate(CreateProductRequest req) {
        validateItemTypeRules(req.itemType(), req.fiscal());
    }

    public void validateUpdate(ProductEntity current, UpdateProductRequest req) {
        ItemType finalItemType = req.itemType() != null ? req.itemType() : current.getItemType();
        ProductFiscalRequest finalFiscal = mergeFiscal(current.getFiscal(), req.fiscal());

        if (finalFiscal != null) {
            validateItemTypeRules(finalItemType, finalFiscal);
        }
    }

    private void validateItemTypeRules(ItemType itemType, ProductFiscalRequest fiscal) {
        if (itemType == null) return;

        if (fiscal == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Fiscal data is required");
        }

        // Regras comuns
        validateTribUnitAndFactor(fiscal);

        if (itemType == ItemType.PRODUCT) {
            // Obrigatórios para PRODUCT
            if (isBlank(fiscal.ncm())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ncm is required for PRODUCT");
            }
            if (fiscal.origin() == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "origin is required for PRODUCT");
            }

            // Proibidos para PRODUCT
            if (!isBlank(fiscal.serviceListCode())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "serviceListCode must be null for PRODUCT");
            }
        }

        if (itemType == ItemType.SERVICE) {
            // Obrigatórios para SERVICE
            if (isBlank(fiscal.serviceListCode())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "serviceListCode is required for SERVICE");
            }

            // Proibidos para SERVICE
            if (!isBlank(fiscal.ncm())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ncm must be null for SERVICE");
            }
            if (!isBlank(fiscal.cest())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cest must be null for SERVICE");
            }
            if (!isBlank(fiscal.gtinEan())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "gtinEan must be null for SERVICE");
            }
            if (!isBlank(fiscal.gtinEanTrib())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "gtinEanTrib must be null for SERVICE");
            }
            if (fiscal.origin() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "origin must be null for SERVICE");
            }
            if (!isBlank(fiscal.cbenef())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "cbenef must be null for SERVICE");
            }
        }
    }

    private void validateTribUnitAndFactor(ProductFiscalRequest fiscal) {
        // unitTrib e tribFactor devem andar juntos, ambos null ou ambos preenchidos
        boolean hasUnit = !isBlank(fiscal.unitTrib());
        boolean hasFactor = fiscal.tribFactor() != null;

        if (hasUnit ^ hasFactor) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "unitTrib and tribFactor must be provided together"
            );
        }

        if (fiscal.tribFactor() != null && fiscal.tribFactor().compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "tribFactor must be > 0");
        }
    }

    private ProductFiscalRequest mergeFiscal(ProductFiscalEntity current, ProductFiscalRequest patch) {
        if (current == null && patch == null) return null;
        if (current == null) return patch;

        // se não veio fiscal no PATCH, valida o que já existe no banco
        if (patch == null) {
            return new ProductFiscalRequest(
                    current.getNcm(),
                    current.getCest(),
                    current.getOrigin(),
                    current.getGtinEan(),
                    current.getGtinEanTrib(),
                    current.getUnitTrib(),
                    current.getTribFactor(),
                    current.getCbenef(),
                    current.getServiceListCode()
            );
        }

        // merge parcial
        return new ProductFiscalRequest(
                patch.ncm() != null ? patch.ncm() : current.getNcm(),
                patch.cest() != null ? patch.cest() : current.getCest(),
                patch.origin() != null ? patch.origin() : current.getOrigin(),
                patch.gtinEan() != null ? patch.gtinEan() : current.getGtinEan(),
                patch.gtinEanTrib() != null ? patch.gtinEanTrib() : current.getGtinEanTrib(),
                patch.unitTrib() != null ? patch.unitTrib() : current.getUnitTrib(),
                patch.tribFactor() != null ? patch.tribFactor() : current.getTribFactor(),
                patch.cbenef() != null ? patch.cbenef() : current.getCbenef(),
                patch.serviceListCode() != null ? patch.serviceListCode() : current.getServiceListCode()
        );
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
