
package com.carboncalculator.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
@Data
public class ActivityRecordRequest {

    @NotNull(message = "Organization ID is required")
    @Positive(message = "Organization ID must be positive")
    private Long organizationId;

    @NotNull(message = "Emission factor ID is required")
    @Positive(message = "Emission factor ID must be positive")
    private Long emissionFactorId;

    @NotBlank(message = "Activity name is required")
    private String activityName;

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.0001",
            message = "Quantity must be greater than zero")
    private BigDecimal quantity;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotNull(message = "Activity date is required")
    private LocalDate activityDate;

    private String description;

   
}
