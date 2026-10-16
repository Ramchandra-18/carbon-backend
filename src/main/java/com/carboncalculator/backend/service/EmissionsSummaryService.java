
package com.carboncalculator.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carboncalculator.backend.dto.EmissionsSummaryResponse;
import com.carboncalculator.backend.entity.ActivityRecord;
import com.carboncalculator.backend.entity.AppUser;
import com.carboncalculator.backend.repository.ActivityRecordRepository;
import com.carboncalculator.backend.repository.AppUserRepository;
import com.carboncalculator.backend.repository.OrganizationRepository;

@Service
public class EmissionsSummaryService {

    private final ActivityRecordRepository activityRecordRepository;
    private final AppUserRepository appUserRepository;
    private final OrganizationRepository organizationRepository;
    private final OrganizationAccessService organizationAccessService;
    private final AdminAccessService adminAccessService;

    public EmissionsSummaryService(
            ActivityRecordRepository activityRecordRepository,
            AppUserRepository appUserRepository,
            OrganizationRepository organizationRepository,
            OrganizationAccessService organizationAccessService,
            AdminAccessService adminAccessService) {

        this.activityRecordRepository = activityRecordRepository;
        this.appUserRepository = appUserRepository;
        this.organizationRepository = organizationRepository;
        this.organizationAccessService = organizationAccessService;
        this.adminAccessService = adminAccessService;
    }

    @Transactional(readOnly = true)
    public EmissionsSummaryResponse getSummary(Long organizationId) {

        AppUser currentUser = getCurrentUser();

        organizationAccessService.checkAccess(
                currentUser, organizationId);

        if (organizationId == null
                || !organizationRepository.existsById(organizationId)) {
            throw new IllegalArgumentException(
                    "Organization not found");
        }

        List<ActivityRecord> records =
                activityRecordRepository
                        .findByOrganization_Id(organizationId);

        return buildSummary(organizationId, records);
    }

    /**
     * Calculates emissions across all organizations.
     * Only an active administrator can access this summary.
     */
    @Transactional(readOnly = true)
    public EmissionsSummaryResponse getSummaryForAllOrganizations() {

        adminAccessService.requireAdmin();

        List<ActivityRecord> records =
                activityRecordRepository.findAll();

        BigDecimal scope1 = BigDecimal.ZERO;
        BigDecimal scope2 = BigDecimal.ZERO;
        BigDecimal scope3 = BigDecimal.ZERO;

        for (ActivityRecord record : records) {

            String scope = record.getEmissionFactor().getScope();
            BigDecimal emissions = record.getEmissionKgCo2e();

            if (emissions == null) {
                continue;
            }

            if ("SCOPE_1".equals(scope)) {
                scope1 = scope1.add(emissions);
            } else if ("SCOPE_2".equals(scope)) {
                scope2 = scope2.add(emissions);
            } else if ("SCOPE_3".equals(scope)) {
                scope3 = scope3.add(emissions);
            }
        }

        EmissionsSummaryResponse response =
                new EmissionsSummaryResponse();

        // No single organization represents this global summary.
        response.setOrganizationId(null);
        response.setScope1Emissions(scope1);
        response.setScope2Emissions(scope2);
        response.setScope3Emissions(scope3);
        response.setTotalEmissions(
                scope1.add(scope2).add(scope3));

        return response;
    }

    private EmissionsSummaryResponse buildSummary(
            Long organizationId,
            List<ActivityRecord> records) {

        BigDecimal scope1 = BigDecimal.ZERO;
        BigDecimal scope2 = BigDecimal.ZERO;
        BigDecimal scope3 = BigDecimal.ZERO;

        for (ActivityRecord record : records) {

            String scope = record.getEmissionFactor().getScope();
            BigDecimal emissions = record.getEmissionKgCo2e();

            if (emissions == null) {
                continue;
            }

            if ("SCOPE_1".equals(scope)) {
                scope1 = scope1.add(emissions);
            } else if ("SCOPE_2".equals(scope)) {
                scope2 = scope2.add(emissions);
            } else if ("SCOPE_3".equals(scope)) {
                scope3 = scope3.add(emissions);
            }
        }

        EmissionsSummaryResponse response =
                new EmissionsSummaryResponse();

        response.setOrganizationId(organizationId);
        response.setScope1Emissions(scope1);
        response.setScope2Emissions(scope2);
        response.setScope3Emissions(scope3);
        response.setTotalEmissions(
                scope1.add(scope2).add(scope3));

        return response;
    }

    private AppUser getCurrentUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null
                || "anonymousUser".equals(authentication.getName())) {
            throw new AccessDeniedException(
                    "Authentication is required");
        }

        return appUserRepository
                .findByEmailIgnoreCase(authentication.getName())
                .filter(AppUser::isActive)
                .orElseThrow(() ->
                        new AccessDeniedException(
                                "Active user not found"));
    }
}
