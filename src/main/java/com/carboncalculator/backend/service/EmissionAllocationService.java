
package com.carboncalculator.backend.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carboncalculator.backend.dto.EmissionAllocationRequest;
import com.carboncalculator.backend.entity.ActivityRecord;
import com.carboncalculator.backend.entity.AppUser;
import com.carboncalculator.backend.entity.EmissionAllocation;
import com.carboncalculator.backend.entity.ProductionBatch;
import com.carboncalculator.backend.repository.ActivityRecordRepository;
import com.carboncalculator.backend.repository.AppUserRepository;
import com.carboncalculator.backend.repository.EmissionAllocationRepository;
import com.carboncalculator.backend.repository.ProductionBatchRepository;

@Service
public class EmissionAllocationService {

    private final EmissionAllocationRepository allocationRepository;
    private final ActivityRecordRepository activityRecordRepository;
    private final ProductionBatchRepository batchRepository;
    private final AppUserRepository appUserRepository;
    private final OrganizationAccessService organizationAccessService;
    private final AdminAccessService adminAccessService;

    public EmissionAllocationService(
            EmissionAllocationRepository allocationRepository,
            ActivityRecordRepository activityRecordRepository,
            ProductionBatchRepository batchRepository,
            AppUserRepository appUserRepository,
            OrganizationAccessService organizationAccessService,
            AdminAccessService adminAccessService) {

        this.allocationRepository = allocationRepository;
        this.activityRecordRepository = activityRecordRepository;
        this.batchRepository = batchRepository;
        this.appUserRepository = appUserRepository;
        this.organizationAccessService = organizationAccessService;
        this.adminAccessService = adminAccessService;
    }

    @Transactional
    public EmissionAllocation createAllocation(
            EmissionAllocationRequest request) {

        if (request == null
                || request.getActivityRecordId() == null
                || request.getBatchId() == null) {
            throw new IllegalArgumentException(
                    "Activity record ID and batch ID are required");
        }

        // Validate the amount before authentication or database lookups.
        BigDecimal requestedAmount =
                request.getAllocatedEmissionKgCo2e();

        if (requestedAmount == null
                || requestedAmount.signum() <= 0) {
            throw new IllegalArgumentException(
                    "Allocated emissions must be greater than zero");
        }

        AppUser currentUser = getCurrentUser();

        // Lock the activity record to protect allocation totals
        // from concurrent transactions.
        ActivityRecord activity = activityRecordRepository
                .findByIdForUpdate(request.getActivityRecordId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Activity record not found"));

        ProductionBatch batch = batchRepository
                .findById(request.getBatchId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Batch not found"));

        Long activityOrganizationId =
                activity.getOrganization().getId();

        Long batchOrganizationId =
                batch.getProduct().getOrganization().getId();

        // Admin can access any organization.
        // Other roles are restricted to their assigned organization.
        organizationAccessService.checkAccess(
                currentUser, activityOrganizationId);

        if (!activityOrganizationId.equals(batchOrganizationId)) {
            throw new IllegalArgumentException(
                    "Activity record and batch must belong to the same organization");
        }

        if (!activity.getEmissionFactor().isActive()) {
            throw new IllegalArgumentException(
                    "Cannot allocate emissions from an inactive emission factor");
        }

        if (!batch.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot allocate emissions to an inactive batch");
        }

        boolean duplicate = allocationRepository
                .existsByActivityRecord_IdAndBatch_Id(
                        activity.getId(), batch.getId());

        if (duplicate) {
            throw new IllegalArgumentException(
                    "This activity has already been allocated to this batch");
        }

        BigDecimal alreadyAllocated = allocationRepository
                .sumAllocatedEmissionsForActivity(activity.getId());

        if (alreadyAllocated == null) {
            alreadyAllocated = BigDecimal.ZERO;
        }

        BigDecimal remaining = activity.getEmissionKgCo2e()
                .subtract(alreadyAllocated);

        if (remaining.signum() < 0
                || requestedAmount.compareTo(remaining) > 0) {
            throw new IllegalArgumentException(
                    "Allocation exceeds the activity's remaining unallocated emissions: "
                            + remaining);
        }

        EmissionAllocation allocation = new EmissionAllocation();
        allocation.setActivityRecord(activity);
        allocation.setBatch(batch);
        allocation.setAllocatedEmissionKgCo2e(requestedAmount);

        return allocationRepository.save(allocation);
    }

    @Transactional(readOnly = true)
    public List<EmissionAllocation> getAllocationsForBatch(Long batchId) {

        AppUser currentUser = getCurrentUser();

        ProductionBatch batch = batchRepository.findById(batchId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Batch not found"));

        Long organizationId =
                batch.getProduct().getOrganization().getId();

        organizationAccessService.checkAccess(
                currentUser, organizationId);

        return allocationRepository.findByBatch_Id(batchId);
    }

    /**
     * Returns allocations across all organizations.
     * This operation is restricted to active administrators.
     */
    @Transactional(readOnly = true)
    public List<EmissionAllocation> getAllAllocationsForAdmin() {

        adminAccessService.requireAdmin();

        return allocationRepository.findAll();
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
