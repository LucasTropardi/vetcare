package com.lucast.vetcare.customers.tutor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TutorRepository extends JpaRepository<TutorEntity, Long> {

    @Query("""
            select t from TutorEntity t
            where (:active is null or t.active = :active)
              and (:hasQuery = false
                   or lower(t.name) like lower(concat('%', :q, '%'))
                   or t.document like concat('%', :q, '%')
                   or lower(t.phone) like lower(concat('%', :q, '%'))
                   or lower(t.email) like lower(concat('%', :q, '%')))
              and (:hasCompany is null
                   or (:hasCompany = true and exists (
                        select 1 from com.lucast.vetcare.customers.company.CustomerCompanyEntity c
                        where c.active = true and c.tutorId = t.id
                   ))
                   or (:hasCompany = false and not exists (
                        select 1 from com.lucast.vetcare.customers.company.CustomerCompanyEntity c
                        where c.active = true and c.tutorId = t.id
                   )))
              and (:hasPet is null
                   or (:hasPet = true and exists (
                        select 1 from com.lucast.vetcare.customers.pet.PetEntity p
                        where p.active = true and p.tutorId = t.id
                   ))
                   or (:hasPet = false and not exists (
                        select 1 from com.lucast.vetcare.customers.pet.PetEntity p
                        where p.active = true and p.tutorId = t.id
                   )))
              and (:hasContact is null
                   or (:hasContact = true and ((t.email is not null and trim(t.email) <> '') or (t.phone is not null and trim(t.phone) <> '')))
                   or (:hasContact = false and ((t.email is null or trim(t.email) = '') and (t.phone is null or trim(t.phone) = ''))))
            """)
    Page<TutorEntity> search(
            @Param("hasQuery") boolean hasQuery,
            @Param("q") String query,
            @Param("active") Boolean active,
            @Param("hasCompany") Boolean hasCompany,
            @Param("hasPet") Boolean hasPet,
            @Param("hasContact") Boolean hasContact,
            Pageable pageable
    );

    long countByActive(boolean active);

    @Query("""
            select count(t) from TutorEntity t
            where exists (
                select 1 from com.lucast.vetcare.customers.company.CustomerCompanyEntity c
                where c.active = true and c.tutorId = t.id
            )
            """)
    long countWithActiveCompany();

    @Query("""
            select count(t) from TutorEntity t
            where exists (
                select 1 from com.lucast.vetcare.customers.pet.PetEntity p
                where p.active = true and p.tutorId = t.id
            )
            """)
    long countWithActivePet();

    @Query("""
            select count(t) from TutorEntity t
            where (t.email is null or trim(t.email) = '')
              and (t.phone is null or trim(t.phone) = '')
            """)
    long countWithoutContact();
}
