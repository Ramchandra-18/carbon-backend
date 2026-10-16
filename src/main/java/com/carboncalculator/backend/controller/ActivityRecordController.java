
package com.carboncalculator.backend.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.carboncalculator.backend.dto.ActivityRecordRequest;
import com.carboncalculator.backend.dto.ActivityRecordResponse;
import com.carboncalculator.backend.entity.ActivityRecord;
import com.carboncalculator.backend.service.ActivityRecordService;

@RestController
@RequestMapping("/api/activity-records")
public class ActivityRecordController {

    private final ActivityRecordService activityRecordService;

    public ActivityRecordController(
            ActivityRecordService activityRecordService) {
        this.activityRecordService = activityRecordService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAKEHOLDER')")
    public ResponseEntity<ActivityRecordResponse> createRecord(
            @Valid @RequestBody ActivityRecordRequest request) {

        ActivityRecord record = activityRecordService.createRecord(
                request.getOrganizationId(),
                request.getEmissionFactorId(),
                request.getActivityName(),
                request.getQuantity(),
                request.getUnit(),
                request.getActivityDate(),
                request.getDescription()
        );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ActivityRecordResponse.fromEntity(record));
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAKEHOLDER', 'REVIEWER')")
    public ResponseEntity<List<ActivityRecordResponse>> getRecords(
            @PathVariable Long organizationId) {

        List<ActivityRecordResponse> responses =
                activityRecordService
                        .getRecordsForOrganization(organizationId)
                        .stream()
                        .map(ActivityRecordResponse::fromEntity)
                        .toList();

        return ResponseEntity.ok(responses);
    }
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ActivityRecordResponse>> getAllRecordsForAdmin() {
        return ResponseEntity.ok(
                activityRecordService.getAllRecordsForAdmin()
                        .stream()
                        .map(ActivityRecordResponse::fromEntity)
                        .toList());
    }
}
