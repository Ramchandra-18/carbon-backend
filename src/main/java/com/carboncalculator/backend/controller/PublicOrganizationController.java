package com.carboncalculator.backend.controller;

import java.util.List;

import com.carboncalculator.backend.dto.PublicOrganizationResponse;
import com.carboncalculator.backend.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/organizations")
@RequiredArgsConstructor
public class PublicOrganizationController {
    private final OrganizationRepository organizationRepository;

    @GetMapping
    public List<PublicOrganizationResponse> getActiveOrganizations() {
    	return organizationRepository.findAll().stream()
    	        .filter(org -> org.isActive())
    	        .map(org -> new PublicOrganizationResponse(
    	                org.getId(),
    	                org.getName(),
    	                org.getOrganizationCode(),
    	                org.isActive()
    	        ))
    	        .toList();
    }
}
