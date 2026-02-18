package com.lucast.vetcare.clinic.appointment;

import com.lucast.vetcare.common.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, Long>, JpaSpecificationExecutor<AppointmentEntity> {
    Page<AppointmentEntity> findByStatus(AppointmentStatus status, Pageable pageable);
    Page<AppointmentEntity> findByPetId(Long petId, Pageable pageable);
    Page<AppointmentEntity> findByVeterinarianUserId(Long vetUserId, Pageable pageable);

    @Query("""
            select count(a) > 0
            from AppointmentEntity a
            where a.status = com.lucast.vetcare.common.enums.AppointmentStatus.OPEN
              and a.petId = :petId
              and a.scheduledStartAt < :endAt
              and a.scheduledEndAt > :startAt
              and (:excludeId is null or a.id <> :excludeId)
            """)
    boolean existsOverlappingPetSchedule(
            @Param("petId") Long petId,
            @Param("startAt") OffsetDateTime startAt,
            @Param("endAt") OffsetDateTime endAt,
            @Param("excludeId") Long excludeId
    );

    @Query("""
            select count(a) > 0
            from AppointmentEntity a
            where a.status = com.lucast.vetcare.common.enums.AppointmentStatus.OPEN
              and a.veterinarianUserId = :vetUserId
              and a.scheduledStartAt < :endAt
              and a.scheduledEndAt > :startAt
              and (:excludeId is null or a.id <> :excludeId)
            """)
    boolean existsOverlappingVetSchedule(
            @Param("vetUserId") Long vetUserId,
            @Param("startAt") OffsetDateTime startAt,
            @Param("endAt") OffsetDateTime endAt,
            @Param("excludeId") Long excludeId
    );
}
