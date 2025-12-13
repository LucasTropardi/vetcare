package com.lucast.vetcare.catalog;

import com.lucast.vetcare.catalog.dto.ProductListDTO;
import com.lucast.vetcare.catalog.spec.ProductSpecification;
import com.lucast.vetcare.common.enums.ProductCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

@Service
public class ProductQueryService {

    private final ProductRepository repository;

    public ProductQueryService(ProductRepository repository) {
        this.repository = repository;
    }

    public Page<ProductListDTO> list(
            String name,
            ProductCategory category,
            Boolean active,
            Pageable pageable
    ) {

        Specification<ProductEntity> spec =
                Specification.where(ProductSpecification.nameLike(name))
                        .and(ProductSpecification.categoryEquals(category))
                        .and(ProductSpecification.activeEquals(active));

        return repository.findAll(spec, pageable)
                .map(p -> new ProductListDTO(
                        p.getId(),
                        p.getSku(),
                        p.getName(),
                        p.getCategory().name(),
                        p.getUnit(),
                        p.isActive(),
                        p.getSalePrice(),
                        p.getCostPrice(),
                        p.getMinStock()
                ));
    }
}
