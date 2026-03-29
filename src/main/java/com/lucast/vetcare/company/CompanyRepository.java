package com.lucast.vetcare.company;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<CompanyEntity, Long> {

    Optional<CompanyEntity> findFirstByHeadquarterTrueOrderByIdAsc();

    Optional<CompanyEntity> findFirstByOrderByIdAsc();

    Optional<CompanyEntity> findFirstByOrderByHeadquarterDescIdAsc();
}
