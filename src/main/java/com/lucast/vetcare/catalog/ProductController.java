package com.lucast.vetcare.catalog;

import com.lucast.vetcare.catalog.dto.CreateProductRequest;
import com.lucast.vetcare.catalog.dto.ProductListDTO;
import com.lucast.vetcare.catalog.dto.ProductResponse;
import com.lucast.vetcare.catalog.dto.UpdateProductRequest;
import com.lucast.vetcare.common.enums.ProductCategory;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService service;
    private final ProductQueryService queryService;

    public ProductController(ProductService service, ProductQueryService queryService) {
        this.service = service;
        this.queryService = queryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@RequestBody @Valid CreateProductRequest request) {
        return service.create(request);
    }

    @GetMapping
    public Page<ProductListDTO> list(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) ProductCategory category,
            @RequestParam(required = false) Boolean active,
            Pageable pageable
    ) {
        return queryService.list(name, category, active, pageable);
    }

    @GetMapping("/{id}")
    public ProductResponse get(@PathVariable Long id) {
        return service.get(id);
    }

    @PatchMapping("/{id}")
    public ProductResponse update(@PathVariable Long id, @RequestBody @Valid UpdateProductRequest request) {
        return service.update(id, request);
    }

    @PatchMapping("/{id}/active")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void setActive(@PathVariable Long id, @RequestParam boolean value) {
        service.setActive(id, value);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        service.setActive(id, false);
    }
}
