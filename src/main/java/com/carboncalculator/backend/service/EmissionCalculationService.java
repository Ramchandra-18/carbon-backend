
package com.carboncalculator.backend.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import com.carboncalculator.backend.entity.EmissionFactor;

@Service
public class EmissionCalculationService {

    public BigDecimal calculateEmissions(
            BigDecimal quantity,
            EmissionFactor emissionFactor) {

        if (quantity == null) {
            throw new IllegalArgumentException(
                    "Activity quantity is required");
        }

        if (quantity.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Activity quantity cannot be negative");
        }

        if (emissionFactor == null) {
            throw new IllegalArgumentException(
                    "Emission factor is required");
        }

        BigDecimal factor = emissionFactor.getCo2eFactor();

        if (factor == null
                || factor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "Emission factor must be zero or positive");
        }

        return quantity.multiply(factor)
                .setScale(6, RoundingMode.HALF_UP);
    }
}
