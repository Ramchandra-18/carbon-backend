
package com.carboncalculator.backend.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carboncalculator.backend.entity.ActivityRecord;
import com.carboncalculator.backend.entity.AppUser;
import com.carboncalculator.backend.entity.EmissionFactor;
import com.carboncalculator.backend.entity.Organization;
import com.carboncalculator.backend.repository.ActivityRecordRepository;
import com.carboncalculator.backend.repository.AppUserRepository;
import com.carboncalculator.backend.repository.EmissionFactorRepository;
import com.carboncalculator.backend.repository.OrganizationRepository;

@Service
public class ActivityRecordService {

    private final ActivityRecordRepository activityRecordRepository;
    private final EmissionFactorRepository emissionFactorRepository;
    private final OrganizationRepository organizationRepository;
    private final AppUserRepository appUserRepository;
    private final EmissionCalculationService calculationService;
    private final OrganizationAccessService organizationAccessService;
    private final AdminAccessService adminAccessService;

    public ActivityRecordService(
            ActivityRecordRepository activityRecordRepository,
            EmissionFactorRepository emissionFactorRepository,
            OrganizationRepository organizationRepository,
            AppUserRepository appUserRepository,
            EmissionCalculationService calculationService,
            OrganizationAccessService organizationAccessService,
            AdminAccessService adminAccessService) {

        this.activityRecordRepository = activityRecordRepository;
        this.emissionFactorRepository = emissionFactorRepository;
        this.organizationRepository = organizationRepository;
        this.appUserRepository = appUserRepository;
        this.calculationService = calculationService;
        this.organizationAccessService = organizationAccessService;
        this.adminAccessService = adminAccessService;
    }

    private AppUser getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {
            throw new AccessDeniedException("Authentication required");
        }

        return appUserRepository
                .findByEmailIgnoreCase(authentication.getName())
                .filter(AppUser::isActive)
                .orElseThrow(() ->
                        new AccessDeniedException("Active user not found"));
    }

    @Transactional
    public ActivityRecord createRecord(
            Long organizationId,
            Long emissionFactorId,
            String activityName,
            BigDecimal quantity,
            String unit,
            LocalDate activityDate,
            String description) {

        AppUser user = getCurrentUser();

        // Admin can access any organization; others remain restricted.
        organizationAccessService.checkAccess(user, organizationId);

        if (organizationId == null || emissionFactorId == null) {
            throw new IllegalArgumentException(
                    "Organization ID and emission factor ID are required");
        }

        if (activityName == null || activityName.isBlank()) {
            throw new IllegalArgumentException("Activity name is required");
        }

        if (unit == null || unit.isBlank()) {
            throw new IllegalArgumentException("Activity unit is required");
        }

        if (activityDate == null) {
            throw new IllegalArgumentException("Activity date is required");
        }

        if (quantity == null
                || quantity.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "Activity quantity must be greater than zero");
        }

        Organization organization = organizationRepository
                .findById(organizationId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Organization not found"));

        EmissionFactor factor = emissionFactorRepository
                .findById(emissionFactorId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Emission factor not found"));

        if (!factor.isActive()) {
            throw new IllegalArgumentException("Emission factor is inactive");
        }

        if (!unit.trim().equalsIgnoreCase(
                factor.getActivityUnit().trim())) {
            throw new IllegalArgumentException(
                    "Activity unit must match the emission factor unit: "
                            + factor.getActivityUnit());
        }

        if (factor.getValidFrom() != null
                && activityDate.isBefore(factor.getValidFrom())) {
            throw new IllegalArgumentException(
                    "Emission factor was not valid on the activity date");
        }

        if (factor.getValidUntil() != null
                && activityDate.isAfter(factor.getValidUntil())) {
            throw new IllegalArgumentException(
                    "Emission factor expired before the activity date");
        }

        BigDecimal emissions =
                calculationService.calculateEmissions(quantity, factor);

        ActivityRecord record = new ActivityRecord();
        record.setOrganization(organization);
        record.setEmissionFactor(factor);
        record.setActivityName(activityName.trim());
        record.setQuantity(quantity);
        record.setUnit(factor.getActivityUnit());
        record.setEmissionKgCo2e(emissions);
        record.setActivityDate(activityDate);
        record.setDescription(description);

        return activityRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<ActivityRecord> getRecordsForOrganization(
            Long organizationId) {

        AppUser user = getCurrentUser();

        organizationAccessService.checkAccess(user, organizationId);

        if (organizationId == null
                || !organizationRepository.existsById(organizationId)) {
            throw new IllegalArgumentException("Organization not found");
        }

        return activityRecordRepository
                .findByOrganization_Id(organizationId);
    }

    @Transactional(readOnly = true)
    public List<ActivityRecord> getAllRecordsForAdmin() {
        adminAccessService.requireAdmin();
        return activityRecordRepository.findAll();
    }
}
