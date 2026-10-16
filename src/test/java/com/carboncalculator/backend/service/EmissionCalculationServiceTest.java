
package com.carboncalculator.backend.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.math.BigDecimal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.carboncalculator.backend.entity.EmissionFactor;

class EmissionCalculationServiceTest {

    private EmissionCalculationService service;

    @BeforeEach
    void setUp() {
        service = new EmissionCalculationService();
    }

    @Test
    void shouldCalculateEmissionsCorrectly() {
        EmissionFactor factor = new EmissionFactor();
        factor.setCo2eFactor(new BigDecimal("0.5"));

        BigDecimal result = service.calculateEmissions(
                new BigDecimal("100"), factor);

        assertEquals(new BigDecimal("50.000000"), result);
    }

    @Test
    void shouldReturnZeroForZeroQuantity() {
        EmissionFactor factor = new EmissionFactor();
        factor.setCo2eFactor(new BigDecimal("0.5"));

        BigDecimal result = service.calculateEmissions(
                BigDecimal.ZERO, factor);

        assertEquals(new BigDecimal("0.000000"), result);
    }

    @Test
    void shouldRejectNegativeQuantity() {
        EmissionFactor factor = new EmissionFactor();
        factor.setCo2eFactor(new BigDecimal("0.5"));

        assertThrows(IllegalArgumentException.class,
                () -> service.calculateEmissions(
                        new BigDecimal("-10"), factor));
    }

    @Test
    void shouldRejectNullQuantity() {
        EmissionFactor factor = new EmissionFactor();
        factor.setCo2eFactor(new BigDecimal("0.5"));

        assertThrows(IllegalArgumentException.class,
                () -> service.calculateEmissions(null, factor));
    }

    @Test
    void shouldRejectNullEmissionFactor() {
        assertThrows(IllegalArgumentException.class,
                () -> service.calculateEmissions(
                        new BigDecimal("100"), null));
    }

    @Test
    void shouldRejectNegativeEmissionFactor() {
        EmissionFactor factor = new EmissionFactor();
        factor.setCo2eFactor(new BigDecimal("-0.5"));

        assertThrows(IllegalArgumentException.class,
                () -> service.calculateEmissions(
                        new BigDecimal("100"), factor));
    }

    @Test
    void shouldRoundUsingHalfUp() {
        EmissionFactor factor = new EmissionFactor();
        factor.setCo2eFactor(new BigDecimal("0.1234565"));

        BigDecimal result = service.calculateEmissions(
                BigDecimal.ONE, factor);

        assertEquals(new BigDecimal("0.123457"), result);
    }
}
