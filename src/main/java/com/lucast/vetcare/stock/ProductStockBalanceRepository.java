package com.lucast.vetcare.stock;

import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;

public interface ProductStockBalanceRepository extends JpaRepository<ProductStockBalanceEntity, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select b from ProductStockBalanceEntity b where b.productId = :productId")
    Optional<ProductStockBalanceEntity> findByProductIdForUpdate(@Param("productId") Long productId);
}
