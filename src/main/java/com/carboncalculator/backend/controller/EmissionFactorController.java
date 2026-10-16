
package com.carboncalculator.backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.carboncalculator.backend.dto.EmissionFactorRequest;
import com.carboncalculator.backend.dto.EmissionFactorResponse;
import com.carboncalculator.backend.entity.EmissionFactor;
import com.carboncalculator.backend.service.EmissionFactorService;

@RestController
@RequestMapping("/api/emission-factors")
public class EmissionFactorController {

    private final EmissionFactorService emissionFactorService;

    public EmissionFactorController(
            EmissionFactorService emissionFactorService) {
        this.emissionFactorService = emissionFactorService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EmissionFactorResponse> createFactor(
            @Valid @RequestBody EmissionFactorRequest request) {

        EmissionFactor factor =
                emissionFactorService.createFactor(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(EmissionFactorResponse.fromEntity(factor));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAKEHOLDER', 'REVIEWER')")
    public ResponseEntity<List<EmissionFactorResponse>> getActiveFactors() {

        List<EmissionFactorResponse> responses =
                emissionFactorService.getActiveFactors()
                        .stream()
                        .map(EmissionFactorResponse::fromEntity)
                        .toList();

        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAKEHOLDER', 'REVIEWER')")
    public ResponseEntity<EmissionFactorResponse> getFactorById(
            @PathVariable Long id) {

        EmissionFactor factor =
                emissionFactorService.getFactorById(id);

        return ResponseEntity.ok(
                EmissionFactorResponse.fromEntity(factor));
    }

    @GetMapping("/scope/{scope}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAKEHOLDER', 'REVIEWER')")
    public ResponseEntity<List<EmissionFactorResponse>> getFactorsByScope(
            @PathVariable String scope) {

        List<EmissionFactorResponse> responses =
                emissionFactorService.getFactorsByScope(scope)
                        .stream()
                        .map(EmissionFactorResponse::fromEntity)
                        .toList();

        return ResponseEntity.ok(responses);
    }
}
