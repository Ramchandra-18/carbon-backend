package com.carboncalculator.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.carboncalculator.backend.entity.ProductionBatch;

import lombok.Data;
@Data
public class ProductionBatchResponse {

    private Long id;
    private Long productId;
    private String batchCode;
    private BigDecimal productionQuantity;
    private String productionUnit;
    private LocalDate productionDate;
    private boolean active;
    private LocalDateTime createdAt;

    public ProductionBatchResponse() {
    }

    public static ProductionBatchResponse fromEntity(
            ProductionBatch batch) {

        ProductionBatchResponse response =
                new ProductionBatchResponse();

        response.setId(batch.getId());
        response.setProductId(batch.getProduct().getId());
        response.setBatchCode(batch.getBatchCode());
        response.setProductionQuantity(batch.getProductionQuantity());
        response.setProductionUnit(batch.getProductionUnit());
        response.setProductionDate(batch.getProductionDate());
        response.setActive(batch.isActive());
        response.setCreatedAt(batch.getCreatedAt());

        return response;
    }

   
}