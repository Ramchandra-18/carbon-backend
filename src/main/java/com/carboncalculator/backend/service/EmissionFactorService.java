
package com.carboncalculator.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carboncalculator.backend.dto.EmissionFactorRequest;
import com.carboncalculator.backend.entity.EmissionFactor;
import com.carboncalculator.backend.entity.Scope3Category;
import com.carboncalculator.backend.repository.EmissionFactorRepository;

@Service
public class EmissionFactorService {

    private final EmissionFactorRepository emissionFactorRepository;

    public EmissionFactorService(
            EmissionFactorRepository emissionFactorRepository) {
        this.emissionFactorRepository = emissionFactorRepository;
    }

    @Transactional
    public EmissionFactor createFactor(EmissionFactorRequest request) {

        if (request == null) {
            throw new IllegalArgumentException(
                    "Emission factor request is required");
        }

        if (request.getScope() == null
                || request.getScope().isBlank()) {
            throw new IllegalArgumentException("Scope is required");
        }

        String scope = request.getScope().trim().toUpperCase();

        if (!List.of("SCOPE_1", "SCOPE_2", "SCOPE_3").contains(scope)) {
            throw new IllegalArgumentException(
                    "Scope must be SCOPE_1, SCOPE_2 or SCOPE_3");
        }

        String category = request.getScope3Category();

        if ("SCOPE_3".equals(scope)) {

            if (category == null || category.isBlank()) {
                throw new IllegalArgumentException(
                        "Scope 3 category is required for Scope 3 factors");
            }

            category = category.trim().toUpperCase();

            try {
                Scope3Category.valueOf(category);
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException(
                        "Invalid Scope 3 category. Use one of the 15 supported categories.");
            }

        } else if (category != null && !category.isBlank()) {

            throw new IllegalArgumentException(
                    "Scope 3 category is only allowed for Scope 3 factors");
        }

        if (request.getValidFrom() == null) {
            throw new IllegalArgumentException(
                    "Valid-from date is required");
        }

        if (request.getValidUntil() != null
                && request.getValidUntil().isBefore(request.getValidFrom())) {
            throw new IllegalArgumentException(
                    "Valid-until date cannot be before valid-from date");
        }

        if (request.getCo2eFactor() == null
                || request.getCo2eFactor().signum() < 0) {
            throw new IllegalArgumentException(
                    "CO2e factor must be zero or positive");
        }

        if (request.getName() == null || request.getName().isBlank()
                || request.getSource() == null || request.getSource().isBlank()
                || request.getSourceReference() == null
                        || request.getSourceReference().isBlank()
                || request.getActivityUnit() == null
                        || request.getActivityUnit().isBlank()) {
            throw new IllegalArgumentException(
                    "Name, source, source reference and activity unit are required");
        }

        EmissionFactor factor = new EmissionFactor();

        factor.setName(request.getName().trim());
        factor.setSource(request.getSource().trim());
        factor.setSourceReference(request.getSourceReference().trim());
        factor.setActivityUnit(request.getActivityUnit().trim());
        factor.setCo2eFactor(request.getCo2eFactor());
        factor.setScope(scope);
        factor.setScope3Category(
                "SCOPE_3".equals(scope) ? category : null);
        factor.setValidFrom(request.getValidFrom());
        factor.setValidUntil(request.getValidUntil());
        factor.setActive(true);

        return emissionFactorRepository.save(factor);
    }

    @Transactional(readOnly = true)
    public List<EmissionFactor> getActiveFactors() {
        return emissionFactorRepository.findAll()
                .stream()
                .filter(EmissionFactor::isActive)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmissionFactor getFactorById(Long id) {

        EmissionFactor factor = emissionFactorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Emission factor not found"));

        if (!factor.isActive()) {
            throw new IllegalArgumentException(
                    "Emission factor is inactive");
        }

        return factor;
    }

    @Transactional(readOnly = true)
    public List<EmissionFactor> getFactorsByScope(String scope) {

        if (scope == null || scope.isBlank()) {
            throw new IllegalArgumentException("Scope is required");
        }

        String normalizedScope = scope.trim().toUpperCase();

        if (!List.of("SCOPE_1", "SCOPE_2", "SCOPE_3")
                .contains(normalizedScope)) {
            throw new IllegalArgumentException("Invalid emissions scope");
        }

        return emissionFactorRepository
                .findByScopeAndActiveTrue(normalizedScope);
    }
}
