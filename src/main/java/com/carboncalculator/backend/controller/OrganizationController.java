
package com.carboncalculator.backend.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import com.carboncalculator.backend.dto.OrganizationRequest;
import com.carboncalculator.backend.entity.Organization;
import com.carboncalculator.backend.service.OrganizationService;

@RestController
@RequestMapping("/api/organizations")
public class OrganizationController {

    private final OrganizationService service;

    public OrganizationController(OrganizationService service) {
        this.service = service;
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public Organization create(
            @Valid @RequestBody OrganizationRequest request) {
        try {
            return service.create(request);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, ex.getMessage());
        }
    }
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<Organization> findAll() {
        return service.findAll();
    }
}
