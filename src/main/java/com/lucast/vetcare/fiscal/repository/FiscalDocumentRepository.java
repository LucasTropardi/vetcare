package com.lucast.vetcare.fiscal.repository;

import com.lucast.vetcare.fiscal.domain.FiscalDocumentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FiscalDocumentRepository extends JpaRepository<FiscalDocumentEntity, Long> {
    Optional<FiscalDocumentEntity> findTopBySaleIdOrderByIdDesc(Long saleId);
}
