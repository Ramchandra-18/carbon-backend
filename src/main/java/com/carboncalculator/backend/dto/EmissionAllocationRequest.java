package com.carboncalculator.backend.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class EmissionAllocationRequest {

    @NotNull(message = "Activity record ID is required")
    @Positive(message = "Activity record ID must be positive")
    private Long activityRecordId;

    @NotNull(message = "Batch ID is required")
    @Positive(message = "Batch ID must be positive")
    private Long batchId;

    @NotNull(message = "Allocated emissions are required")
    @DecimalMin(
        value = "0.000001",
        message = "Allocated emissions must be greater than zero"
    )
    private BigDecimal allocatedEmissionKgCo2e;

    
}