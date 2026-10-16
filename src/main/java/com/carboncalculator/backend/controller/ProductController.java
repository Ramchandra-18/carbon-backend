package com.carboncalculator.backend.controller;

import java.nio.file.AccessDeniedException;
import java.util.List;


import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.carboncalculator.backend.dto.ProductRequest;
import com.carboncalculator.backend.dto.ProductResponse;
import com.carboncalculator.backend.entity.Product;
import com.carboncalculator.backend.service.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAKEHOLDER')")
    public ProductResponse createProduct(
            @Valid @RequestBody ProductRequest request) throws AccessDeniedException {

        Product product = productService.createProduct(request);
        return ProductResponse.fromEntity(product);
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAKEHOLDER', 'REVIEWER')")
    public List<ProductResponse> getProductsForOrganization(
            @PathVariable Long organizationId) throws AccessDeniedException {

        List<Product> products =
                productService.getProductsForOrganization(organizationId);

        return products.stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }
}