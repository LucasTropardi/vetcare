package com.lucast.vetcare.customers.company;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CustomerCompanyRepository extends JpaRepository<CustomerCompanyEntity, Long> {

    Optional<CustomerCompanyEntity> findByTutorIdAndActiveTrue(Long tutorId);

    Page<CustomerCompanyEntity> findByActiveTrue(Pageable pageable);

    Page<CustomerCompanyEntity> findByActiveTrueAndTutorId(Long tutorId, Pageable pageable);

    List<CustomerCompanyEntity> findByActiveTrueAndTutorIdOrderByLegalNameAsc(Long tutorId);

    @Query("""
            select c from CustomerCompanyEntity c
            where c.active = true
              and (lower(c.legalName) like lower(concat('%', :q, '%'))
                or lower(c.tradeName) like lower(concat('%', :q, '%'))
                or c.cnpj like concat('%', :q, '%'))
            """)
    Page<CustomerCompanyEntity> searchActive(@Param("q") String query, Pageable pageable);

    @Query("""
            select c from CustomerCompanyEntity c
            where c.active = true
              and c.tutorId = :tutorId
              and (lower(c.legalName) like lower(concat('%', :q, '%'))
                or lower(c.tradeName) like lower(concat('%', :q, '%'))
                or c.cnpj like concat('%', :q, '%'))
            """)
    Page<CustomerCompanyEntity> searchActiveByTutorId(
            @Param("tutorId") Long tutorId,
            @Param("q") String query,
            Pageable pageable
    );
}
