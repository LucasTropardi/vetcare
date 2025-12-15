package com.lucast.vetcare.customers.tutor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TutorRepository extends JpaRepository<TutorEntity, Long> {

    Page<TutorEntity> findByActiveTrueAndNameContainingIgnoreCase(String name, Pageable pageable);

    Page<TutorEntity> findByActiveTrue(Pageable pageable);
}
