package com.carboncalculator.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class ProductionBatchRequest {

    @NotNull(message = "Product ID is required")
    @Positive(message = "Product ID must be positive")
    private Long productId;

    @NotBlank(message = "Batch code is required")
    @Size(max = 80, message = "Batch code cannot exceed 80 characters")
    private String batchCode;

    @NotNull(message = "Production quantity is required")
    @DecimalMin(value = "0.001", message = "Production quantity must be greater than zero")
    private BigDecimal productionQuantity;

    @NotBlank(message = "Production unit is required")
    @Size(max = 30, message = "Production unit cannot exceed 30 characters")
    private String productionUnit;

    @NotNull(message = "Production date is required")
    @PastOrPresent(message = "Production date cannot be in the future")
    private LocalDate productionDate;

    
}