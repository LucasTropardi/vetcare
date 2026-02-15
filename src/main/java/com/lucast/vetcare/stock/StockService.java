package com.lucast.vetcare.stock;

import com.lucast.vetcare.catalog.ProductRepository;
import com.lucast.vetcare.common.enums.ItemType;
import com.lucast.vetcare.common.enums.StockMovementType;
import com.lucast.vetcare.stock.dto.CreateStockMovementRequest;
import com.lucast.vetcare.stock.dto.ProductStockBalanceListDTO;
import com.lucast.vetcare.stock.dto.ProductStockBalanceResponse;
import com.lucast.vetcare.stock.dto.StockMovementResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Set;

@Service
public class StockService {

    private final ProductRepository productRepository;
    private final StockMovementRepository movementRepository;
    private final ProductStockBalanceRepository balanceRepository;

    public StockService(
            ProductRepository productRepository,
            StockMovementRepository movementRepository,
            ProductStockBalanceRepository balanceRepository
    ) {
        this.productRepository = productRepository;
        this.movementRepository = movementRepository;
        this.balanceRepository = balanceRepository;
    }

    @Transactional
    public StockMovementResponse createMovement(CreateStockMovementRequest req, Long currentUserId) {

        var product = productRepository.findById(req.productId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        if (!product.isActive()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Inactive product cannot be moved");
        }
        if (product.getItemType() != ItemType.PRODUCT) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "Only PRODUCT can have stock movements");
        }

        validateMovementRules(req);

        // lock do saldo
        var balance = balanceRepository.findByProductIdForUpdate(product.getId())
                .orElseGet(() -> {
                    var b = new ProductStockBalanceEntity();
                    b.setProduct(product);
                    b.setProductId(product.getId());
                    b.setOnHand(BigDecimal.ZERO);
                    b.setAvgCost(BigDecimal.ZERO);
                    return b;
                });

        BigDecimal onHand = nvl(balance.getOnHand());
        BigDecimal avgCost = nvl(balance.getAvgCost());

        BigDecimal newOnHand = onHand.add(req.quantity());

        if (newOnHand.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Insufficient stock for this movement");
        }

        BigDecimal newAvgCost = avgCost;

        if (req.movementType() == StockMovementType.ENTRY_PURCHASE) {
            // custo médio ponderado
            BigDecimal qty = req.quantity(); // positiva
            BigDecimal totalCost = onHand.multiply(avgCost).add(qty.multiply(req.unitCost()));
            newAvgCost = totalCost.divide(newOnHand, 2, RoundingMode.HALF_UP);
        }

        balance.setOnHand(newOnHand);
        balance.setAvgCost(newAvgCost);
        balanceRepository.save(balance);

        var m = new StockMovementEntity();
        m.setProduct(product);
        m.setMovementType(req.movementType());
        m.setQuantity(req.quantity());
        m.setUnitCost(req.unitCost());
        m.setNotes(req.notes());
        m.setReferenceType(req.referenceType());
        m.setReferenceId(req.referenceId());
        m.setCreatedBy(currentUserId);

        var saved = movementRepository.save(m);

        return new StockMovementResponse(
                saved.getId(),
                saved.getProduct().getId(),
                saved.getMovementType(),
                saved.getQuantity(),
                saved.getUnitCost(),
                saved.getNotes(),
                saved.getReferenceType(),
                saved.getReferenceId(),
                saved.getCreatedBy(),
                saved.getCreatedAt()
        );
    }

    @Transactional(readOnly = true)
    public ProductStockBalanceResponse getBalance(Long productId) {
        var b = balanceRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Balance not found"));
        return new ProductStockBalanceResponse(b.getProductId(), b.getOnHand(), b.getAvgCost());
    }

    private void validateMovementRules(CreateStockMovementRequest req) {
        if (req.quantity() == null || req.quantity().compareTo(BigDecimal.ZERO) == 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "quantity must be non-zero"
            );
        }

        switch (req.movementType()) {

            case ENTRY_PURCHASE -> {
                if (req.quantity().compareTo(BigDecimal.ZERO) <= 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "ENTRY_PURCHASE quantity must be > 0"
                    );
                }
                if (req.unitCost() == null || req.unitCost().compareTo(BigDecimal.ZERO) < 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "ENTRY_PURCHASE unitCost must be >= 0"
                    );
                }
            }

            case EXIT_SALE, EXIT_VISIT_CONSUMPTION -> {
                if (req.quantity().compareTo(BigDecimal.ZERO) >= 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "EXIT quantity must be < 0"
                    );
                }
                if (req.unitCost() != null) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "EXIT movements must not receive unitCost"
                    );
                }
            }

            case ADJUSTMENT -> {
                if (req.unitCost() != null && req.unitCost().compareTo(BigDecimal.ZERO) < 0) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "unitCost must be >= 0"
                    );
                }
                if (req.notes() == null || req.notes().trim().isEmpty()) {
                    throw new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "notes is required for ADJUSTMENT"
                    );
                }
            }
        }

        String refType = req.referenceType() == null
                ? null
                : req.referenceType().trim().toUpperCase();

        if (refType == null && req.referenceId() != null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "referenceType is required when referenceId is provided"
            );
        }

        if (refType != null) {

            var allowedTypes = Set.of(
                    "PURCHASE",
                    "SALE",
                    "VISIT",
                    "MANUAL",
                    "IMPORT",
                    "REVERSAL"
            );

            if (!allowedTypes.contains(refType)) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Invalid referenceType"
                );
            }

            var needsIdTypes = Set.of(
                    "PURCHASE",
                    "SALE",
                    "VISIT",
                    "REVERSAL"
            );

            if (needsIdTypes.contains(refType) && req.referenceId() == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "referenceId is required for this referenceType"
                );
            }
        }
    }

    @Transactional(readOnly = true)
    public Page<StockMovementResponse> listMovements(Long productId, Pageable pageable) {
        Page<StockMovementEntity> page =
                (productId == null)
                        ? movementRepository.findAll(pageable)
                        : movementRepository.findByProduct_Id(productId, pageable);

        return page.map(m -> new StockMovementResponse(
                m.getId(),
                m.getProduct().getId(),
                m.getMovementType(),
                m.getQuantity(),
                m.getUnitCost(),
                m.getNotes(),
                m.getReferenceType(),
                m.getReferenceId(),
                m.getCreatedBy(),
                m.getCreatedAt()
        ));
    }

    @Transactional(readOnly = true)
    public Page<ProductStockBalanceListDTO> listBalances(String query, Boolean belowMinStock, Pageable pageable) {
        Pageable normalizedPageable = normalizeBalancePageable(pageable);
        return balanceRepository.listBalances(query, belowMinStock, normalizedPageable);
    }

    private Pageable normalizeBalancePageable(Pageable pageable) {
        if (pageable == null) {
            return PageRequest.of(0, 20, Sort.by(Sort.Order.asc("product.name")));
        }

        Sort sort = pageable.getSort();
        if (sort.isUnsorted()) {
            return PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by(Sort.Order.asc("product.name"))
            );
        }

        Sort normalizedSort = Sort.by(
                sort.stream()
                        .map(order -> {
                            String property = order.getProperty();
                            String mappedProperty = switch (property) {
                                case "name" -> "product.name";
                                case "sku" -> "product.sku";
                                default -> property;
                            };
                            return new Sort.Order(order.getDirection(), mappedProperty);
                        })
                        .toList()
        );

        return PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), normalizedSort);
    }

    private BigDecimal nvl(BigDecimal v) {
        return v == null ? BigDecimal.ZERO : v;
    }
}
