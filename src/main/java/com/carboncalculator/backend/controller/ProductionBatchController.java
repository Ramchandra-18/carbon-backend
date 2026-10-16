
package com.carboncalculator.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.carboncalculator.backend.dto.ProductionBatchRequest;
import com.carboncalculator.backend.dto.ProductionBatchResponse;
import com.carboncalculator.backend.entity.ProductionBatch;
import com.carboncalculator.backend.service.ProductionBatchService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/batches")
public class ProductionBatchController {

    private final ProductionBatchService batchService;

    public ProductionBatchController(
            ProductionBatchService batchService) {
        this.batchService = batchService;
    }

    // Create batch: Admin and Stakeholder only
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAKEHOLDER')")
    public ProductionBatchResponse createBatch(
            @Valid @RequestBody ProductionBatchRequest request) {

        ProductionBatch batch = batchService.createBatch(request);

        return ProductionBatchResponse.fromEntity(batch);
    }

    // Get batches for a specific product
    @GetMapping("/product/{productId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAKEHOLDER', 'REVIEWER')")
    public List<ProductionBatchResponse> getBatchesForProduct(
            @PathVariable Long productId) {

        List<ProductionBatch> batches =
                batchService.getBatchesForProduct(productId);

        return batches.stream()
                .map(ProductionBatchResponse::fromEntity)
                .toList();
    }

    // Get all batches: Admin only
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ProductionBatchResponse>>
            getAllBatchesForAdmin() {

        List<ProductionBatchResponse> batches =
                batchService.getAllBatchesForAdmin()
                        .stream()
                        .map(ProductionBatchResponse::fromEntity)
                        .toList();

        return ResponseEntity.ok(batches);
    }
}
