package com.carboncalculator.backend.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carboncalculator.backend.entity.EmissionAllocation;

public interface EmissionAllocationRepository
        extends JpaRepository<EmissionAllocation, Long> {

    boolean existsByActivityRecord_IdAndBatch_Id(
            Long activityRecordId, Long batchId);

    List<EmissionAllocation> findByActivityRecord_Id(
            Long activityRecordId);

    List<EmissionAllocation> findByBatch_Id(Long batchId);

    @Query("""
        SELECT COALESCE(SUM(a.allocatedEmissionKgCo2e), 0)
        FROM EmissionAllocation a
        WHERE a.activityRecord.id = :activityRecordId
        """)
    BigDecimal sumAllocatedEmissionsForActivity(
            @Param("activityRecordId") Long activityRecordId);

    @Query("""
        SELECT COALESCE(SUM(a.allocatedEmissionKgCo2e), 0)
        FROM EmissionAllocation a
        WHERE a.batch.id = :batchId
        """)
    BigDecimal sumAllocatedEmissionsForBatch(
            @Param("batchId") Long batchId);
}