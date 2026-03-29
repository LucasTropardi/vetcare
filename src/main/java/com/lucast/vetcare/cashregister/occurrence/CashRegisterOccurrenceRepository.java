package com.lucast.vetcare.cashregister.occurrence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CashRegisterOccurrenceRepository extends JpaRepository<CashRegisterOccurrenceEntity, Long> {
    Page<CashRegisterOccurrenceEntity> findByCashRegisterId(Long cashRegisterId, Pageable pageable);
}
