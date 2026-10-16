
package com.carboncalculator.backend.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.carboncalculator.backend.entity.ActivityRecord;

import lombok.Data;
@Data
public class ActivityRecordResponse {

    private Long id;
    private Long organizationId;
    private Long emissionFactorId;
    private String activityName;
    private BigDecimal quantity;
    private String unit;
    private BigDecimal emissionKgCo2e;
    private LocalDate activityDate;
    private String description;
    private LocalDateTime createdAt;

    public ActivityRecordResponse() {
    }

    public static ActivityRecordResponse fromEntity(ActivityRecord record) {
        ActivityRecordResponse response = new ActivityRecordResponse();

        response.setId(record.getId());
        response.setOrganizationId(record.getOrganization().getId());
        response.setEmissionFactorId(record.getEmissionFactor().getId());
        response.setActivityName(record.getActivityName());
        response.setQuantity(record.getQuantity());
        response.setUnit(record.getUnit());
        response.setEmissionKgCo2e(record.getEmissionKgCo2e());
        response.setActivityDate(record.getActivityDate());
        response.setDescription(record.getDescription());
        response.setCreatedAt(record.getCreatedAt());

        return response;
    }

   
}
