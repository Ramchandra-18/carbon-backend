
package com.carboncalculator.backend.service;

import java.util.List;
import java.util.Locale;

import org.springframework.dao.DataIntegrityViolationException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carboncalculator.backend.entity.AppUser;
import com.carboncalculator.backend.entity.Organization;
import com.carboncalculator.backend.enums.Role;
import com.carboncalculator.backend.repository.AppUserRepository;
import com.carboncalculator.backend.repository.OrganizationRepository;

@Service
public class UserService {

    private final AppUserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminAccessService adminAccessService;

    public UserService(
            AppUserRepository userRepository,
            OrganizationRepository organizationRepository,
            PasswordEncoder passwordEncoder,
            AdminAccessService adminAccessService) {

        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.passwordEncoder = passwordEncoder;
        this.adminAccessService = adminAccessService;
    }

    /**
     * Admin-managed account creation.
     * Call only from an Admin-authorized operation.
     */
    @Transactional
    public AppUser createUser(
            String fullName,
            String email,
            String rawPassword,
            Role role,
            Long organizationId) {

        adminAccessService.requireAdmin();

        return createUserInternal(
                fullName, email, rawPassword, role, organizationId, true);
    }

    /**
     * Public registration allows Stakeholders and Reviewers only.
     * Stakeholders are activated immediately; Reviewers remain pending.
     */
    @Transactional
    public AppUser registerPublicUser(
            String fullName,
            String email,
            String rawPassword,
            Role requestedRole,
            Long organizationId) {

        if (requestedRole != Role.STAKEHOLDER
                && requestedRole != Role.REVIEWER) {
            throw new IllegalArgumentException(
                    "Public registration supports Stakeholder or Reviewer only");
        }

        return createUserInternal(
                fullName,
                email,
                rawPassword,
                requestedRole,
                organizationId,
                requestedRole == Role.STAKEHOLDER);
    }

    private AppUser createUserInternal(
            String fullName,
            String email,
            String rawPassword,
            Role role,
            Long organizationId,
            boolean active) {

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Full name is required");
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required");
        }

        if (rawPassword == null
                || rawPassword.length() < 12
                || rawPassword.length() > 72) {
            throw new IllegalArgumentException(
                    "Password must contain 12 to 72 characters");
        }

        if (role == null) {
            throw new IllegalArgumentException(
                    "User role is required");
        }

        String normalizedEmail =
                email.trim().toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmailIgnoreCase(normalizedEmail)) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }

        if (organizationId == null) {
            throw new IllegalArgumentException(
                    "Organization is required");
        }

        Organization organization = organizationRepository
                .findById(organizationId)
                .filter(Organization::isActive)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Active organization not found"));

        AppUser user = new AppUser();
        user.setFullName(fullName.trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setRole(role);
        user.setOrganization(organization);
        user.setActive(active);

        try {
            return userRepository.saveAndFlush(user);
        } catch (DataIntegrityViolationException exception) {
            throw new IllegalArgumentException(
                    "Email is already registered");
        }
    }

    /**
     * Lists all users. Restricted to active administrators.
     */
    @Transactional(readOnly = true)
    public List<AppUser> findAll() {

        adminAccessService.requireAdmin();

        return userRepository.findAll();
    }

    /**
     * Activates or deactivates a user.
     * Restricted to active administrators.
     */
    @Transactional
    public AppUser setUserActive(Long userId, boolean active) {

        adminAccessService.requireAdmin();

        AppUser user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found"));

        if (user.getRole() == Role.ADMIN && !active) {

            long activeAdmins = userRepository.findAll()
                    .stream()
                    .filter(existing ->
                            existing.getRole() == Role.ADMIN
                                    && existing.isActive())
                    .count();

            if (activeAdmins <= 1) {
                throw new IllegalArgumentException(
                        "Cannot deactivate the last active Admin");
            }
        }

        user.setActive(active);

        return userRepository.save(user);
    }
}
