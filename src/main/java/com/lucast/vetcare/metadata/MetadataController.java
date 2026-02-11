package com.lucast.vetcare.metadata;

import com.lucast.vetcare.common.dto.EnumOptionDTO;
import com.lucast.vetcare.common.enums.FiscalOrigin;
import com.lucast.vetcare.common.enums.ProductCategory;
import com.lucast.vetcare.common.enums.ItemType;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

    @GetMapping("/product-categories/paged")
    @Operation(
            summary = "List product categories (paged)",
            description = "Returns available product categories using pagination"
    )
    public Page<EnumOptionDTO> productCategoriesPaged(
            @ParameterObject
            @PageableDefault(size = 20)
            Pageable pageable
    ) {
        return toPage(productCategories(), pageable);
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

    @GetMapping("/item-types/paged")
    @Operation(
            summary = "List item types (paged)",
            description = "Returns available item types using pagination"
    )
    public Page<EnumOptionDTO> itemTypesPaged(
            @ParameterObject
            @PageableDefault(size = 20)
            Pageable pageable
    ) {
        return toPage(itemTypes(), pageable);
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

    @GetMapping("/fiscal-origins/paged")
    @Operation(
            summary = "List fiscal origins (paged)",
            description = "Returns available fiscal origins using pagination"
    )
    public Page<EnumOptionDTO> fiscalOriginsPaged(
            @ParameterObject
            @PageableDefault(size = 20)
            Pageable pageable
    ) {
        return toPage(fiscalOrigins(), pageable);
    }

    private Page<EnumOptionDTO> toPage(List<EnumOptionDTO> values, Pageable pageable) {
        int start = Math.toIntExact(pageable.getOffset());
        if (start >= values.size()) {
            return new PageImpl<>(List.of(), pageable, values.size());
        }

        int end = Math.min(start + pageable.getPageSize(), values.size());
        return new PageImpl<>(values.subList(start, end), pageable, values.size());
    }
}
