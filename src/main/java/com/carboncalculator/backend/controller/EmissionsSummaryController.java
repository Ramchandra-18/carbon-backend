
package com.carboncalculator.backend.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carboncalculator.backend.dto.EmissionsSummaryResponse;
import com.carboncalculator.backend.service.EmissionsSummaryService;

@RestController
@RequestMapping("/api/emissions-summary")
public class EmissionsSummaryController {

    private final EmissionsSummaryService emissionsSummaryService;

    public EmissionsSummaryController(
            EmissionsSummaryService emissionsSummaryService) {
        this.emissionsSummaryService = emissionsSummaryService;
    }

    @GetMapping("/organization/{organizationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'STAKEHOLDER', 'REVIEWER')")
    public ResponseEntity<EmissionsSummaryResponse> getSummary(
            @PathVariable Long organizationId) {

        EmissionsSummaryResponse response =
                emissionsSummaryService.getSummary(organizationId);

        return ResponseEntity.ok(response);
    }
}
