package com.lucast.vetcare.customers.pet;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<PetEntity, Long> {

    Page<PetEntity> findByActiveTrue(Pageable pageable);

    Page<PetEntity> findByActiveTrueAndTutorId(Long tutorId, Pageable pageable);

    Page<PetEntity> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

    Page<PetEntity> findByActiveTrueAndTutorIdAndNameContainingIgnoreCase(Long tutorId, String name, Pageable pageable);
}
