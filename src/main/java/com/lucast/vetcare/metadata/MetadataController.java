package com.lucast.vetcare.metadata;

import com.lucast.vetcare.common.dto.EnumOptionDTO;
import com.lucast.vetcare.common.enums.FiscalOrigin;
import com.lucast.vetcare.common.enums.ProductCategory;
import com.lucast.vetcare.common.enums.ItemType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/metadata")
@Tag(
        name = "Metadata",
        description = "Endpoints for loading static metadata and enum options"
)
public class MetadataController {

    @GetMapping("/product-categories")
    @Operation(
            summary = "List product categories",
            description = "Returns all available product categories"
    )
    public List<EnumOptionDTO> productCategories() {
        return Arrays.stream(ProductCategory.values())
                .map(e -> new EnumOptionDTO(e.name(), e.getLabel()))
                .toList();
    }

    @GetMapping("/item-types")
    @Operation(
            summary = "List item types",
            description = "Returns all available item types"
    )
    public List<EnumOptionDTO> itemTypes() {
        return Arrays.stream(ItemType.values())
                .map(e -> new EnumOptionDTO(e.name(), e.getLabel()))
                .toList();
    }

    @GetMapping("/fiscal-origins")
    @Operation(
            summary = "List fiscal origins",
            description = "Returns all available fiscal origins"
    )
    public List<EnumOptionDTO> fiscalOrigins() {
        return Arrays.stream(FiscalOrigin.values())
                .map(o -> new EnumOptionDTO(o.name(), o.getLabel()))
                .toList();
    }
}
