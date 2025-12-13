package com.lucast.vetcare.catalog.dto;

import com.lucast.vetcare.common.enums.ItemType;
import com.lucast.vetcare.common.enums.ProductCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank @Size(max = 60) String sku,
        @NotBlank @Size(max = 200) String name,

        @NotNull ItemType itemType,
        @NotNull ProductCategory category,

        @NotBlank @Size(max = 10) String unit,

        @NotNull @DecimalMin("0.00") BigDecimal salePrice,
        @NotNull @DecimalMin("0.00") BigDecimal costPrice,

        @NotNull @DecimalMin("0.000") BigDecimal minStock,

        @Valid @NotNull ProductFiscalRequest fiscal
) {}
