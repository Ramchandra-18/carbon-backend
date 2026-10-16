package com.carboncalculator.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.carboncalculator.backend.entity.EmissionAllocation;

import lombok.Data;
@Data
public class EmissionAllocationResponse {

    private Long id;
    private Long activityRecordId;
    private Long batchId;
    private BigDecimal allocatedEmissionKgCo2e;
    private LocalDateTime createdAt;

    public EmissionAllocationResponse() {
    }

    public static EmissionAllocationResponse fromEntity(
            EmissionAllocation allocation) {

        EmissionAllocationResponse response =
                new EmissionAllocationResponse();

        response.setId(allocation.getId());
        response.setActivityRecordId(
                allocation.getActivityRecord().getId());
        response.setBatchId(allocation.getBatch().getId());
        response.setAllocatedEmissionKgCo2e(
                allocation.getAllocatedEmissionKgCo2e());
        response.setCreatedAt(allocation.getCreatedAt());

        return response;
    }

   
}