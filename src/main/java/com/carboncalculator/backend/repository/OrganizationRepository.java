
package com.carboncalculator.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carboncalculator.backend.entity.Organization;

public interface OrganizationRepository
        extends JpaRepository<Organization, Long> {

    Optional<Organization> findByOrganizationCode(
            String organizationCode);

    boolean existsByOrganizationCode(
            String organizationCode);
}
