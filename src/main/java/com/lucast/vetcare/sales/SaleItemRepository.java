package com.lucast.vetcare.sales;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SaleItemRepository extends JpaRepository<SaleItemEntity, Long> {
    List<SaleItemEntity> findBySale_Id(Long saleId);
    Optional<SaleItemEntity> findByIdAndSale_Id(Long id, Long saleId);
}
