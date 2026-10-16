
package com.carboncalculator.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.carboncalculator.backend.entity.EmissionFactor;

import lombok.Data;
@Data
public class EmissionFactorResponse {

    private Long id;
    private String name;
    private String source;
    private String sourceReference;
    private String activityUnit;
    private BigDecimal co2eFactor;
    private String scope;
    private String scope3Category;
    private LocalDate validFrom;
    private LocalDate validUntil;
    private boolean active;

    public EmissionFactorResponse() {
    }

    public static EmissionFactorResponse fromEntity(
            EmissionFactor factor) {

        EmissionFactorResponse response =
                new EmissionFactorResponse();

        response.setId(factor.getId());
        response.setName(factor.getName());
        response.setSource(factor.getSource());
        response.setSourceReference(factor.getSourceReference());
        response.setActivityUnit(factor.getActivityUnit());
        response.setCo2eFactor(factor.getCo2eFactor());
        response.setScope(factor.getScope());
        response.setScope3Category(factor.getScope3Category());
        response.setValidFrom(factor.getValidFrom());
        response.setValidUntil(factor.getValidUntil());
        response.setActive(factor.isActive());

        return response;
    }

   
}
