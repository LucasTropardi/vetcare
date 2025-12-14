package com.lucast.vetcare.stock.dto;

import com.lucast.vetcare.common.enums.StockMovementType;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateStockMovementRequest(
        @NotNull Long productId,
        @NotNull StockMovementType movementType,

        @NotNull BigDecimal quantity, // com sinal
        BigDecimal unitCost, // obrigatório em ENTRY_PURCHASE

        @Size(max = 300) String notes,

        @Size(max = 40) String referenceType,  // PURCHASE/SALE/VISIT/MANUAL/IMPORT
        Long referenceId
) {}
