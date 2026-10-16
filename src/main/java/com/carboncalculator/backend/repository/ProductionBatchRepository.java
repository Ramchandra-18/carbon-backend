package com.carboncalculator.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carboncalculator.backend.entity.ProductionBatch;

public interface ProductionBatchRepository
        extends JpaRepository<ProductionBatch, Long> {

    Optional<ProductionBatch> findByBatchCode(String batchCode);

    boolean existsByBatchCode(String batchCode);

    List<ProductionBatch> findByProduct_Id(Long productId);

    List<ProductionBatch> findByProduct_Organization_Id(
            Long organizationId);

    boolean existsByProduct_IdAndBatchCode(
            Long productId, String batchCode);
}