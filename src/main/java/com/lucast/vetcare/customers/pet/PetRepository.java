package com.lucast.vetcare.customers.pet;

import com.lucast.vetcare.common.enums.Species;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PetRepository extends JpaRepository<PetEntity, Long> {

    @Query("""
            select p from PetEntity p
            where (:active is null or p.active = :active)
              and (:tutorId is null or p.tutorId = :tutorId)
              and (:species is null or p.species = :species)
              and (:othersSpecies = false or p.species not in (com.lucast.vetcare.common.enums.Species.DOG, com.lucast.vetcare.common.enums.Species.CAT))
              and (:hasQuery = false
                   or lower(p.name) like lower(concat('%', :q, '%'))
                   or lower(p.tutor.name) like lower(concat('%', :q, '%')))
            """)
    Page<PetEntity> search(
            @Param("active") Boolean active,
            @Param("tutorId") Long tutorId,
            @Param("species") Species species,
            @Param("othersSpecies") boolean othersSpecies,
            @Param("hasQuery") boolean hasQuery,
            @Param("q") String q,
            Pageable pageable
    );

    long countByActive(boolean active);

    long countBySpecies(Species species);

    @Query("""
            select count(p) from PetEntity p
            where p.species not in (com.lucast.vetcare.common.enums.Species.DOG, com.lucast.vetcare.common.enums.Species.CAT)
            """)
    long countOthersSpecies();
}
