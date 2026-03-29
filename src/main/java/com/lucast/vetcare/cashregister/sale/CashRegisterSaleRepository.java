package com.lucast.vetcare.cashregister.sale;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CashRegisterSaleRepository extends JpaRepository<CashRegisterSaleEntity, Long> {

    Optional<CashRegisterSaleEntity> findBySaleId(Long saleId);

    @Query("select coalesce(max(c.saleNumber), 0) + 1 from CashRegisterSaleEntity c where c.cashRegisterId = :cashRegisterId")
    Long nextSaleNumber(@Param("cashRegisterId") Long cashRegisterId);
}
