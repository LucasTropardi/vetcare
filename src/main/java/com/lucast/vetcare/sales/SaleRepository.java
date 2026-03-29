package com.lucast.vetcare.sales;

import com.lucast.vetcare.common.enums.SaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface SaleRepository extends JpaRepository<SaleEntity, Long> {
    Optional<SaleEntity> findByAppointmentId(Long appointmentId);

    @Query(
            value = """
                    select
                        s.id as id,
                        s.company_id as companyId,
                        s.tutor_id as tutorId,
                        s.customer_company_id as customerCompanyId,
                        s.appointment_id as appointmentId,
                        s.status as status,
                        s.subtotal as subtotal,
                        s.discount as discount,
                        s.total as total,
                        coalesce(pay.paid_total, 0) as paidTotal,
                        greatest(s.total - coalesce(pay.paid_total, 0), 0) as remaining,
                        s.notes as notes,
                        s.created_by as createdBy,
                        s.confirmed_by as confirmedBy,
                        s.confirmed_at as confirmedAt,
                        s.canceled_by as canceledBy,
                        s.canceled_at as canceledAt,
                        s.created_at as createdAt,
                        s.updated_at as updatedAt,
                        coalesce(items.item_count, 0) as itemCount,
                        coalesce(nullif(trim(t.name), ''), nullif(trim(cc.trade_name), ''), nullif(trim(cc.legal_name), ''), nullif(trim(crs.customer_name), '')) as customerName,
                        coalesce(nullif(trim(t.document), ''), nullif(trim(cc.cnpj), ''), nullif(trim(crs.customer_document), '')) as customerDocument,
                        cr.register_code as registerCode,
                        crs.sale_number as saleNumber,
                        crs.fiscal_document_number as documentNumber,
                        fd.protocol as protocol,
                        case when s.status = 'CONFIRMED' and s.canceled_at is null then true else false end as canCancel,
                        case when coalesce(fd.xml_proc, fd.xml_signed, fd.xml) is not null and coalesce(fd.xml_proc, fd.xml_signed, fd.xml) <> '' then true else false end as xmlAvailable,
                        case when s.status = 'CONFIRMED' then true else false end as receiptAvailable
                    from sales s
                    left join tutors t on t.id = s.tutor_id
                    left join customer_companies cc on cc.id = s.customer_company_id
                    left join cash_register_sales crs on crs.sale_id = s.id
                    left join cash_registers cr on cr.id = crs.cash_register_id
                    left join fiscal_documents fd on fd.id = (
                        select max(fd2.id) from fiscal_documents fd2 where fd2.sale_id = s.id
                    )
                    left join (
                        select sale_id, count(*) as item_count
                        from sale_items
                        group by sale_id
                    ) items on items.sale_id = s.id
                    left join (
                        select sale_id, coalesce(sum(case when status = 'PAID' then amount else 0 end), 0) as paid_total
                        from sale_payments
                        group by sale_id
                    ) pay on pay.sale_id = s.id
                    where (cast(:status as varchar) is null or s.status = cast(:status as varchar))
                      and (cast(:dateFrom as timestamp with time zone) is null or coalesce(s.confirmed_at, s.updated_at, s.created_at) >= cast(:dateFrom as timestamp with time zone))
                      and (cast(:dateTo as timestamp with time zone) is null or coalesce(s.confirmed_at, s.updated_at, s.created_at) < cast(:dateTo as timestamp with time zone))
                      and (
                        :queryLike is null
                        or lower(coalesce(nullif(trim(t.name), ''), nullif(trim(cc.trade_name), ''), nullif(trim(cc.legal_name), ''), nullif(trim(crs.customer_name), ''), '')) like :queryLike
                        or lower(coalesce(nullif(trim(t.document), ''), nullif(trim(cc.cnpj), ''), nullif(trim(crs.customer_document), ''), '')) like :queryLike
                        or lower(coalesce(crs.fiscal_document_number, '')) like :queryLike
                        or lower(coalesce(fd.protocol, '')) like :queryLike
                        or (cast(:queryNumber as bigint) is not null and (s.id = cast(:queryNumber as bigint) or crs.sale_number = cast(:queryNumber as bigint)))
                      )
                    order by coalesce(s.confirmed_at, s.updated_at, s.created_at) desc, s.id desc
                    """,
            countQuery = """
                    select count(*)
                    from sales s
                    left join tutors t on t.id = s.tutor_id
                    left join customer_companies cc on cc.id = s.customer_company_id
                    left join cash_register_sales crs on crs.sale_id = s.id
                    left join fiscal_documents fd on fd.id = (
                        select max(fd2.id) from fiscal_documents fd2 where fd2.sale_id = s.id
                    )
                    where (cast(:status as varchar) is null or s.status = cast(:status as varchar))
                      and (cast(:dateFrom as timestamp with time zone) is null or coalesce(s.confirmed_at, s.updated_at, s.created_at) >= cast(:dateFrom as timestamp with time zone))
                      and (cast(:dateTo as timestamp with time zone) is null or coalesce(s.confirmed_at, s.updated_at, s.created_at) < cast(:dateTo as timestamp with time zone))
                      and (
                        :queryLike is null
                        or lower(coalesce(nullif(trim(t.name), ''), nullif(trim(cc.trade_name), ''), nullif(trim(cc.legal_name), ''), nullif(trim(crs.customer_name), ''), '')) like :queryLike
                        or lower(coalesce(nullif(trim(t.document), ''), nullif(trim(cc.cnpj), ''), nullif(trim(crs.customer_document), ''), '')) like :queryLike
                        or lower(coalesce(crs.fiscal_document_number, '')) like :queryLike
                        or lower(coalesce(fd.protocol, '')) like :queryLike
                        or (cast(:queryNumber as bigint) is not null and (s.id = cast(:queryNumber as bigint) or crs.sale_number = cast(:queryNumber as bigint)))
                      )
                    """,
            nativeQuery = true
    )
    Page<SaleListProjection> listSales(
            @Param("queryLike") String queryLike,
            @Param("queryNumber") Long queryNumber,
            @Param("status") String status,
            @Param("dateFrom") OffsetDateTime dateFrom,
            @Param("dateTo") OffsetDateTime dateTo,
            Pageable pageable
    );
}
