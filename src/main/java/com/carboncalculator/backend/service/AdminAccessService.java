package com.carboncalculator.backend.service;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.carboncalculator.backend.entity.AppUser;
import com.carboncalculator.backend.repository.AppUserRepository;

@Service
public class AdminAccessService {

    private final AppUserRepository appUserRepository;

    public AdminAccessService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser requireAdmin() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication.getName() == null) {
            throw new AccessDeniedException("Authentication required");
        }

        AppUser user = appUserRepository
                .findByEmailIgnoreCase(authentication.getName())
                .filter(AppUser::isActive)
                .orElseThrow(() ->
                        new AccessDeniedException("Active user not found"));

        if (user.getRole() == null
                || !"ADMIN".equals(user.getRole().name())) {
            throw new AccessDeniedException("Admin access required");
        }

        return user;
    }
}