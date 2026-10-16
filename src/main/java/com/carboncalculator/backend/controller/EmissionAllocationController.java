
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

import com.carboncalculator.backend.dto.EmissionAllocationRequest;
import com.carboncalculator.backend.dto.EmissionAllocationResponse;
import com.carboncalculator.backend.entity.EmissionAllocation;
import com.carboncalculator.backend.service.EmissionAllocationService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/emission-allocations")
public class EmissionAllocationController {

    private final EmissionAllocationService allocationService;

    public EmissionAllocationController(
            EmissionAllocationService allocationService) {
        this.allocationService = allocationService;
    }

    // Create allocation: Admin and Stakeholder only
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('ADMIN', 'STAKEHOLDER')")
    public EmissionAllocationResponse createAllocation(
            @Valid @RequestBody EmissionAllocationRequest request) {

        EmissionAllocation allocation =
                allocationService.createAllocation(request);

        return EmissionAllocationResponse.fromEntity(allocation);
    }

    // Get allocations for a specific batch
    @GetMapping("/batch/{batchId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAKEHOLDER', 'REVIEWER')")
    public List<EmissionAllocationResponse> getAllocationsForBatch(
            @PathVariable Long batchId) {

        List<EmissionAllocation> allocations =
                allocationService.getAllocationsForBatch(batchId);

        return allocations.stream()
                .map(EmissionAllocationResponse::fromEntity)
                .toList();
    }

    // Get all allocations: Admin only
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<EmissionAllocationResponse>>
            getAllAllocationsForAdmin() {

        List<EmissionAllocationResponse> allocations =
                allocationService.getAllAllocationsForAdmin()
                        .stream()
                        .map(EmissionAllocationResponse::fromEntity)
                        .toList();

        return ResponseEntity.ok(allocations);
    }
}
