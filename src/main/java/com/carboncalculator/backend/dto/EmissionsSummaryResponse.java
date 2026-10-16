
package com.carboncalculator.backend.dto;

import java.math.BigDecimal;

import lombok.Data;
@Data
public class EmissionsSummaryResponse {

    private Long organizationId;

    private BigDecimal scope1Emissions = BigDecimal.ZERO;
    private BigDecimal scope2Emissions = BigDecimal.ZERO;
    private BigDecimal scope3Emissions = BigDecimal.ZERO;
    private BigDecimal totalEmissions = BigDecimal.ZERO;

   
}
