
package com.carboncalculator.backend.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carboncalculator.backend.dto.ProductRequest;
import com.carboncalculator.backend.entity.AppUser;
import com.carboncalculator.backend.entity.Organization;
import com.carboncalculator.backend.entity.Product;
import com.carboncalculator.backend.repository.AppUserRepository;
import com.carboncalculator.backend.repository.OrganizationRepository;
import com.carboncalculator.backend.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final OrganizationRepository organizationRepository;
    private final AppUserRepository appUserRepository;
    private final OrganizationAccessService organizationAccessService;
    private final AdminAccessService adminAccessService;

    public ProductService(
            ProductRepository productRepository,
            OrganizationRepository organizationRepository,
            AppUserRepository appUserRepository,
            OrganizationAccessService organizationAccessService,
            AdminAccessService adminAccessService) {

        this.productRepository = productRepository;
        this.organizationRepository = organizationRepository;
        this.appUserRepository = appUserRepository;
        this.organizationAccessService = organizationAccessService;
        this.adminAccessService = adminAccessService;
    }

    @Transactional
    public Product createProduct(ProductRequest request) {

        if (request == null || request.getOrganizationId() == null) {
            throw new IllegalArgumentException(
                    "Organization ID is required");
        }

        if (request.getName() == null || request.getName().isBlank()) {
            throw new IllegalArgumentException("Product name is required");
        }

        if (request.getProductCode() == null
                || request.getProductCode().isBlank()) {
            throw new IllegalArgumentException(
                    "Product code is required");
        }

        AppUser currentUser = getCurrentUser();

        organizationAccessService.checkAccess(
                currentUser, request.getOrganizationId());

        Organization organization = organizationRepository
                .findById(request.getOrganizationId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Organization not found"));

        String productCode = request.getProductCode()
                .trim()
                .toUpperCase();

        if (productRepository.existsByProductCode(productCode)) {
            throw new IllegalArgumentException(
                    "Product code already exists");
        }

        Product product = new Product();
        product.setOrganization(organization);
        product.setName(request.getName().trim());
        product.setProductCode(productCode);
        product.setDescription(
                request.getDescription() == null
                        ? null
                        : request.getDescription().trim());

        return productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<Product> getProductsForOrganization(Long organizationId) {

        AppUser currentUser = getCurrentUser();

        organizationAccessService.checkAccess(
                currentUser, organizationId);

        if (organizationId == null
                || !organizationRepository.existsById(organizationId)) {
            throw new IllegalArgumentException(
                    "Organization not found");
        }

        return productRepository.findByOrganization_Id(organizationId);
    }

    /**
     * Retrieves products from every organization.
     * Only an active administrator can use this method.
     */
    @Transactional(readOnly = true)
    public List<Product> getAllProductsForAdmin() {

        adminAccessService.requireAdmin();

        return productRepository.findAll();
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
