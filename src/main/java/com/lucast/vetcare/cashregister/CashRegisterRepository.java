package com.lucast.vetcare.cashregister;

import com.lucast.vetcare.common.enums.CashRegisterStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CashRegisterRepository extends JpaRepository<CashRegisterEntity, Long> {

    boolean existsByCompanyIdAndRegisterCodeAndStatus(Long companyId, String registerCode, CashRegisterStatus status);

    java.util.Optional<CashRegisterEntity> findFirstByCompanyIdAndRegisterCodeAndStatusOrderByOpenedAtDesc(
            Long companyId,
            String registerCode,
            CashRegisterStatus status
    );

    @Query("""
            select c
              from CashRegisterEntity c
             where (:companyId is null or c.companyId = :companyId)
               and (:status is null or c.status = :status)
               and (:registerCode is null or lower(c.registerCode) like lower(concat('%', :registerCode, '%')))
            """)
    Page<CashRegisterEntity> search(
            @Param("companyId") Long companyId,
            @Param("status") CashRegisterStatus status,
            @Param("registerCode") String registerCode,
            Pageable pageable
    );
}
