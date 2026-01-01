package com.lucast.vetcare.sales;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SalePaymentRepository extends JpaRepository<SalePaymentEntity, Long> {
    List<SalePaymentEntity> findBySale_Id(Long saleId);
}
