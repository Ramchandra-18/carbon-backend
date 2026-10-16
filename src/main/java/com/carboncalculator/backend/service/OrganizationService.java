
package com.carboncalculator.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carboncalculator.backend.dto.OrganizationRequest;
import com.carboncalculator.backend.entity.Organization;
import com.carboncalculator.backend.repository.OrganizationRepository;

@Service
public class OrganizationService {

    private final OrganizationRepository repository;
    private final AdminAccessService adminAccessService;

    public OrganizationService(
            OrganizationRepository repository,
            AdminAccessService adminAccessService) {
        this.repository = repository;
        this.adminAccessService = adminAccessService;
    }

    @Transactional
    public Organization create(OrganizationRequest request) {

        // Only an active administrator can create organizations.
        adminAccessService.requireAdmin();

        if (request == null) {
            throw new IllegalArgumentException(
                    "Organization request is required");
        }

        if (request.getName() == null
                || request.getName().isBlank()) {
            throw new IllegalArgumentException(
                    "Organization name is required");
        }

        if (request.getOrganizationCode() == null
                || request.getOrganizationCode().isBlank()) {
            throw new IllegalArgumentException(
                    "Organization code is required");
        }

        String organizationCode =
                request.getOrganizationCode().trim();

        if (repository.existsByOrganizationCode(organizationCode)) {
            throw new IllegalArgumentException(
                    "Organization code already exists");
        }

        Organization organization = new Organization();
        organization.setName(request.getName().trim());
        organization.setOrganizationCode(organizationCode);
        organization.setIndustry(request.getIndustry());
        organization.setCountry(request.getCountry());

        return repository.save(organization);
    }

    @Transactional(readOnly = true)
    public List<Organization> findAll() {

        // Only an active administrator can list all organizations.
        adminAccessService.requireAdmin();

        return repository.findAll();
    }
}
