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

    List<CustomerCompanyEntity> findByActiveTrueAndTutorIdOrderByLegalNameAsc(Long tutorId);

    Page<CustomerCompanyEntity> findByActiveTrueAndTutorIdOrderByLegalNameAsc(Long tutorId, Pageable pageable);

    @Query("""
            select c from CustomerCompanyEntity c
            where (:active is null or c.active = :active)
              and (:tutorId is null or c.tutorId = :tutorId)
              and (:hasQuery = false
                   or lower(c.legalName) like lower(concat('%', :q, '%'))
                   or lower(c.tradeName) like lower(concat('%', :q, '%'))
                   or c.cnpj like concat('%', :q, '%'))
              and (:hasAddress is null
                   or (:hasAddress = true and exists (
                        select 1 from com.lucast.vetcare.customers.company.CustomerCompanyAddressEntity a
                        where a.customerCompanyId = c.id
                   ))
                   or (:hasAddress = false and not exists (
                        select 1 from com.lucast.vetcare.customers.company.CustomerCompanyAddressEntity a
                        where a.customerCompanyId = c.id
                   )))
              and (:hasFiscal is null
                   or (:hasFiscal = true and exists (
                        select 1 from com.lucast.vetcare.customers.company.CustomerCompanyFiscalEntity f
                        where f.customerCompanyId = c.id
                   ))
                   or (:hasFiscal = false and not exists (
                        select 1 from com.lucast.vetcare.customers.company.CustomerCompanyFiscalEntity f
                        where f.customerCompanyId = c.id
                   )))
              and (:hasContact is null
                   or (:hasContact = true and ((c.email is not null and trim(c.email) <> '') or (c.phone is not null and trim(c.phone) <> '')))
                   or (:hasContact = false and ((c.email is null or trim(c.email) = '') and (c.phone is null or trim(c.phone) = ''))))
            """)
    Page<CustomerCompanyEntity> search(
            @Param("hasQuery") boolean hasQuery,
            @Param("q") String query,
            @Param("tutorId") Long tutorId,
            @Param("active") Boolean active,
            @Param("hasAddress") Boolean hasAddress,
            @Param("hasFiscal") Boolean hasFiscal,
            @Param("hasContact") Boolean hasContact,
            Pageable pageable
    );

    long countByActive(boolean active);

    @Query("""
            select count(c) from CustomerCompanyEntity c
            where exists (
                select 1 from com.lucast.vetcare.customers.company.CustomerCompanyAddressEntity a
                where a.customerCompanyId = c.id
            )
            """)
    long countWithAddress();

    @Query("""
            select count(c) from CustomerCompanyEntity c
            where exists (
                select 1 from com.lucast.vetcare.customers.company.CustomerCompanyFiscalEntity f
                where f.customerCompanyId = c.id
            )
            """)
    long countWithFiscal();

    @Query("""
            select count(c) from CustomerCompanyEntity c
            where (c.email is null or trim(c.email) = '')
              and (c.phone is null or trim(c.phone) = '')
            """)
    long countWithoutContact();
}
