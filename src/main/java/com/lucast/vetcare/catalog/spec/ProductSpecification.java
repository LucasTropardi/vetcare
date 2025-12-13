package com.lucast.vetcare.catalog.spec;

import com.lucast.vetcare.catalog.ProductEntity;
import com.lucast.vetcare.common.enums.ProductCategory;
import org.springframework.data.jpa.domain.Specification;

public class ProductSpecification {

    public static Specification<ProductEntity> nameLike(String name) {
        return (root, query, cb) ->
                name == null ? null :
                        cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<ProductEntity> categoryEquals(ProductCategory category) {
        return (root, query, cb) ->
                category == null ? null :
                        cb.equal(root.get("category"), category);
    }

    public static Specification<ProductEntity> activeEquals(Boolean active) {
        return (root, query, cb) ->
                active == null ? null :
                        cb.equal(root.get("active"), active);
    }
}
