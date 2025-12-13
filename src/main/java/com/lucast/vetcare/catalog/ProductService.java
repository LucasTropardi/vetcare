package com.lucast.vetcare.catalog;

import com.lucast.vetcare.catalog.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import static org.springframework.http.HttpStatus.NOT_FOUND;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest req) {
        var p = new ProductEntity();
        p.setSku(req.sku());
        p.setName(req.name());
        p.setItemType(req.itemType());
        p.setCategory(req.category());
        p.setUnit(req.unit());
        p.setSalePrice(req.salePrice());
        p.setCostPrice(req.costPrice());
        p.setMinStock(req.minStock());
        p.setActive(true);

        if (req.fiscal() != null) {
            var f = new ProductFiscalEntity();
            f.setProduct(p);
            f.setNcm(req.fiscal().ncm());
            f.setCest(req.fiscal().cest());
            f.setOrigin(req.fiscal().origin());
            f.setGtinEan(req.fiscal().gtinEan());
            f.setGtinEanTrib(req.fiscal().gtinEanTrib());
            f.setUnitTrib(req.fiscal().unitTrib());
            f.setTribFactor(req.fiscal().tribFactor());
            f.setCbenef(req.fiscal().cbenef());
            f.setServiceListCode(req.fiscal().serviceListCode());
            p.setFiscal(f);
        }

        var saved = productRepository.save(p);
        return map(saved);
    }

    @Transactional(readOnly = true)
    public Page<ProductResponse> list(Pageable pageable) {
        return productRepository.findAll(pageable).map(this::map);
    }

    @Transactional(readOnly = true)
    public ProductResponse get(Long id) {
        var p = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));
        return map(p);
    }

    @Transactional
    public void setActive(Long id, boolean active) {
        var p = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));
        p.setActive(active);
        // sem save() aqui; dirty checking resolve
    }

    @Transactional
    public ProductResponse update(Long id, UpdateProductRequest req) {
        var p = productRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Product not found"));

        if (req.sku() != null) p.setSku(req.sku());
        if (req.name() != null) p.setName(req.name());
        if (req.itemType() != null) p.setItemType(req.itemType());
        if (req.category() != null) p.setCategory(req.category());
        if (req.unit() != null) p.setUnit(req.unit());
        if (req.salePrice() != null) p.setSalePrice(req.salePrice());
        if (req.costPrice() != null) p.setCostPrice(req.costPrice());
        if (req.minStock() != null) p.setMinStock(req.minStock());

        if (req.fiscal() != null) {
            var fiscal = p.getFiscal();
            if (fiscal == null) {
                fiscal = new ProductFiscalEntity();
                fiscal.setProduct(p);
                p.setFiscal(fiscal);
            }

            var f = req.fiscal();
            if (f.ncm() != null) fiscal.setNcm(f.ncm());
            if (f.cest() != null) fiscal.setCest(f.cest());
            if (f.origin() != null) fiscal.setOrigin(f.origin());
            if (f.gtinEan() != null) fiscal.setGtinEan(f.gtinEan());
            if (f.gtinEanTrib() != null) fiscal.setGtinEanTrib(f.gtinEanTrib());
            if (f.unitTrib() != null) fiscal.setUnitTrib(f.unitTrib());
            if (f.tribFactor() != null) fiscal.setTribFactor(f.tribFactor());
            if (f.cbenef() != null) fiscal.setCbenef(f.cbenef());
            if (f.serviceListCode() != null) fiscal.setServiceListCode(f.serviceListCode());
        }

        return map(p);
    }

    private ProductResponse map(ProductEntity p) {
        var f = p.getFiscal();
        ProductFiscalResponse fiscal = null;

        if (f != null) {
            fiscal = new ProductFiscalResponse(
                    f.getNcm(),
                    f.getCest(),
                    f.getOrigin(),
                    f.getGtinEan(),
                    f.getGtinEanTrib(),
                    f.getUnitTrib(),
                    f.getTribFactor(),
                    f.getCbenef(),
                    f.getServiceListCode()
            );
        }

        return new ProductResponse(
                p.getId(),
                p.getSku(),
                p.getName(),
                p.getItemType(),
                p.getCategory(),
                p.getUnit(),
                p.isActive(),
                p.getSalePrice(),
                p.getCostPrice(),
                p.getMinStock(),
                fiscal
        );
    }
}
