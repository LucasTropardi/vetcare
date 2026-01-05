package com.lucast.vetcare.fiscal.document.repository;

import com.lucast.vetcare.fiscal.document.FiscalEventEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface FiscalEventRepository extends JpaRepository<FiscalEventEntity, Long> {

    List<FiscalEventEntity> findByDocument_IdOrderByCreatedAtDesc(Long documentId);

    List<FiscalEventEntity> findByDocument_IdAndStatusOrderByCreatedAtDesc(Long documentId, String status);

    Page<FiscalEventEntity> findByDocument_Id(Long documentId, Pageable pageable);

    Page<FiscalEventEntity> findByDocument_IdAndCreatedAtBetween(Long documentId, OffsetDateTime from, OffsetDateTime to, Pageable pageable);

    Page<FiscalEventEntity> findByDocument_IdAndStatus(Long documentId, String status, Pageable pageable);
}
