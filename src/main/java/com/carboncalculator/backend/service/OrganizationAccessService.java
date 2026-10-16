package com.carboncalculator.backend.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import com.carboncalculator.backend.entity.AppUser;

@Service
public class OrganizationAccessService {

    public void checkAccess(AppUser user, Long organizationId) {

        if (user.getRole() == null) {
            throw new AccessDeniedException("User role is missing");
        }

        // Admin can access every organization.
        if ("ADMIN".equals(user.getRole().name())) {
            return;
        }

        // Other users must belong to the requested organization.
        if (user.getOrganization() == null
                || organizationId == null
                || !organizationId.equals(
                        user.getOrganization().getId())) {
            throw new AccessDeniedException(
                    "You cannot access another organization's data");
        }
    }
}