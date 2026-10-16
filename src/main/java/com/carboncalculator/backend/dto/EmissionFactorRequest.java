
package com.carboncalculator.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;
@Data
public class EmissionFactorRequest {

    @NotBlank(message = "Emission factor name is required")
    private String name;

    @NotBlank(message = "Source is required")
    private String source;

    @NotBlank(message = "Source reference is required")
    private String sourceReference;

    @NotBlank(message = "Activity unit is required")
    private String activityUnit;

    @NotNull(message = "CO2e factor is required")
    @PositiveOrZero(message = "CO2e factor cannot be negative")
    private BigDecimal co2eFactor;

    @NotBlank(message = "Scope is required")
    private String scope;

    private String scope3Category;

    @NotNull(message = "Valid-from date is required")
    private LocalDate validFrom;

    private LocalDate validUntil;

    
}
