package com.lucast.vetcare.stock;

import com.lucast.vetcare.stock.dto.ProductStockBalanceListDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface ProductStockBalanceRepository extends JpaRepository<ProductStockBalanceEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from ProductStockBalanceEntity b where b.productId = :productId")
    Optional<ProductStockBalanceEntity> findByProductIdForUpdate(@Param("productId") Long productId);

    @Query("""
        select new com.lucast.vetcare.stock.dto.ProductStockBalanceListDTO(
            p.id,
            p.sku,
            p.name,
            b.onHand,
            b.avgCost,
            p.minStock,
            (p.minStock > 0 and b.onHand < p.minStock)
        )
        from ProductStockBalanceEntity b
        join b.product p
        where (:q is null or :q = '' or lower(p.sku) like lower(concat('%', :q, '%')) or lower(p.name) like lower(concat('%', :q, '%')))
          and (:below is null or :below = false or (p.minStock > 0 and b.onHand < p.minStock))
        order by p.name asc
    """)
    Page<ProductStockBalanceListDTO> listBalances(@Param("q") String query,
                                                  @Param("below") Boolean belowMinStock,
                                                  Pageable pageable);
}
