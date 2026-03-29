package com.lucast.vetcare.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.lucast.vetcare.catalog.dto.ProductPosLookupDTO;

import java.util.Collection;
import java.util.List;

public interface ProductRepository
        extends JpaRepository<ProductEntity, Long>,
        JpaSpecificationExecutor<ProductEntity> {

    @Query("""
            select new com.lucast.vetcare.catalog.dto.ProductPosLookupDTO(
                p.id,
                p.sku,
                p.name,
                p.unit,
                p.itemType,
                p.active,
                p.salePrice,
                f.gtinEan,
                f.gtinEanTrib
            )
            from ProductEntity p
            left join p.fiscal f
            where (:active is null or p.active = :active)
              and (
                    :query is null
                    or trim(:query) = ''
                    or lower(p.name) like lower(concat('%', :query, '%'))
                    or lower(p.sku) like lower(concat('%', :query, '%'))
                    or str(p.id) = :query
                    or f.gtinEan = :query
                    or f.gtinEanTrib = :query
              )
            order by
              case
                when str(p.id) = :query then 0
                when lower(p.sku) = lower(:query) then 1
                when f.gtinEan = :query or f.gtinEanTrib = :query then 2
                when lower(p.name) = lower(:query) then 3
                else 4
              end,
              p.name asc
            """)
    Page<ProductPosLookupDTO> lookupForPos(
            @Param("query") String query,
            @Param("active") Boolean active,
            Pageable pageable
    );

    @Query(value = """
            select
                p.id as productId,
                pf.ncm as ncm,
                pf.cest as cest,
                pf.origin as origin,
                pf.gtin_ean as gtinEan,
                pf.gtin_ean_trib as gtinEanTrib,
                pf.u_trib as unitTrib,
                pf.trib_factor as tribFactor,
                pf.service_list_code as serviceListCode,
                tp.cfop as cfop,
                tp.icms_code as icmsCode,
                tp.icms_rate as icmsRate,
                tp.pis_code as pisCode,
                tp.pis_rate as pisRate,
                tp.cofins_code as cofinsCode,
                tp.cofins_rate as cofinsRate,
                tp.ipi_code as ipiCode,
                tp.ipi_rate as ipiRate
            from products p
            left join product_fiscal pf on pf.product_id = p.id
            left join product_tax_profiles ptp on ptp.product_id = p.id and ptp.is_default = true
            left join tax_profiles tp on tp.id = ptp.tax_profile_id
            where p.id in (:productIds)
            """, nativeQuery = true)
    List<ProductFiscalSnapshotProjection> findFiscalSnapshotsByProductIds(@Param("productIds") Collection<Long> productIds);
}
