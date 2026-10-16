package com.carboncalculator.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class ProductRequest {

    @NotNull(message = "Organization ID is required")
    private Long organizationId;

    @NotBlank(message = "Product name is required")
    @Size(max = 150, message = "Product name cannot exceed 150 characters")
    private String name;

    @NotBlank(message = "Product code is required")
    @Size(max = 60, message = "Product code cannot exceed 60 characters")
    private String productCode;

    @Size(max = 1000, message = "Description cannot exceed 1000 characters")
    private String description;

   
}