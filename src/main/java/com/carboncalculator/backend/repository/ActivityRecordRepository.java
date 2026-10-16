package com.carboncalculator.backend.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Query;

import com.carboncalculator.backend.entity.ActivityRecord;

import jakarta.persistence.LockModeType;

public interface ActivityRecordRepository
        extends JpaRepository<ActivityRecord, Long> {

    List<ActivityRecord> findByOrganization_Id(Long organizationId);

    List<ActivityRecord> findByOrganization_IdAndActivityDateBetween(
            Long organizationId,
            LocalDate startDate,
            LocalDate endDate
    );

    List<ActivityRecord> findByOrganization_IdAndEmissionFactor_Scope(
            Long organizationId,
            String scope
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT a FROM ActivityRecord a WHERE a.id = :id")
    Optional<ActivityRecord> findByIdForUpdate(
            @Param("id") Long id
    );
}