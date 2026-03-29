package com.lucast.vetcare.catalog;

import com.lucast.vetcare.catalog.dto.CreateProductRequest;
import com.lucast.vetcare.catalog.dto.ProductListDTO;
import com.lucast.vetcare.catalog.dto.ProductPosLookupDTO;
import com.lucast.vetcare.catalog.dto.ProductResponse;
import com.lucast.vetcare.catalog.dto.UpdateProductRequest;
import com.lucast.vetcare.common.enums.ProductCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@Tag(
        name = "Products",
        description = "Endpoints for managing product catalog and inventory metadata"
)
public class ProductController {

    private final ProductService service;
    private final ProductQueryService queryService;

    public ProductController(ProductService service, ProductQueryService queryService) {
        this.service = service;
        this.queryService = queryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(
            summary = "Create product",
            description = "Creates a new product in the catalog"
    )
    public ProductResponse create(
            @RequestBody @Valid CreateProductRequest request
    ) {
        return service.create(request);
    }

    @GetMapping
    @Operation(
            summary = "List products",
            description = "Lists products with optional filters by name, category and active status"
    )
    public Page<ProductListDTO> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) Boolean active,
            Pageable pageable
    ) {
        return queryService.list(name, category, active, pageable);
    }

    @GetMapping("/lookup")
    @Operation(
            summary = "Lookup products for POS",
            description = "Searches products for POS flow by id, sku, GTIN/EAN or name"
    )
    public Page<ProductPosLookupDTO> lookupForPos(
            @RequestParam(required = false) String query,
            @RequestParam(defaultValue = "true") Boolean active,
            Pageable pageable
    ) {
        return queryService.lookupForPos(query, active, pageable);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by ID",
            description = "Returns product details for the given ID"
    )
    public ProductResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PatchMapping("/{id}")
    @Operation(
            summary = "Update product",
            description = "Updates product data such as name, price or category"
    )
    public ProductResponse update(
            @PathVariable Long id,
            @RequestBody @Valid UpdateProductRequest request
    ) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/active")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Set product active status",
            description = "Activates or deactivates a product"
    )
    public void setActive(
            @PathVariable Long id,
            @RequestParam boolean value
    ) {
        service.setActive(id, value);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(
            summary = "Delete product",
            description = "Soft deletes a product by deactivating it"
    )
    public void delete(@PathVariable Long id) {
        service.setActive(id, false);
    }
}
