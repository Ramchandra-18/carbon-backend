package com.carboncalculator.backend.dto;

import java.time.LocalDateTime;

import com.carboncalculator.backend.entity.Product;

import lombok.Data;
@Data
public class ProductResponse {

    private Long id;
    private Long organizationId;
    private String name;
    private String productCode;
    private String description;
    private boolean active;
    private LocalDateTime createdAt;

    public ProductResponse() {
    }

    public static ProductResponse fromEntity(Product product) {

        ProductResponse response = new ProductResponse();

        response.setId(product.getId());
        response.setOrganizationId(product.getOrganization().getId());
        response.setName(product.getName());
        response.setProductCode(product.getProductCode());
        response.setDescription(product.getDescription());
        response.setActive(product.isActive());
        response.setCreatedAt(product.getCreatedAt());

        return response;
    }

   
}