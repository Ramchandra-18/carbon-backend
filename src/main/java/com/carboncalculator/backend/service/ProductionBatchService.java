
package com.carboncalculator.backend.service;

import java.util.List;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carboncalculator.backend.dto.ProductionBatchRequest;
import com.carboncalculator.backend.entity.AppUser;
import com.carboncalculator.backend.entity.Product;
import com.carboncalculator.backend.entity.ProductionBatch;
import com.carboncalculator.backend.repository.AppUserRepository;
import com.carboncalculator.backend.repository.ProductRepository;
import com.carboncalculator.backend.repository.ProductionBatchRepository;

@Service
public class ProductionBatchService {

    private final ProductionBatchRepository batchRepository;
    private final ProductRepository productRepository;
    private final AppUserRepository appUserRepository;
    private final OrganizationAccessService organizationAccessService;
    private final AdminAccessService adminAccessService;

    public ProductionBatchService(
            ProductionBatchRepository batchRepository,
            ProductRepository productRepository,
            AppUserRepository appUserRepository,
            OrganizationAccessService organizationAccessService,
            AdminAccessService adminAccessService) {

        this.batchRepository = batchRepository;
        this.productRepository = productRepository;
        this.appUserRepository = appUserRepository;
        this.organizationAccessService = organizationAccessService;
        this.adminAccessService = adminAccessService;
    }

    @Transactional
    public ProductionBatch createBatch(ProductionBatchRequest request) {

        if (request == null || request.getProductId() == null) {
            throw new IllegalArgumentException("Product ID is required");
        }

        AppUser currentUser = getCurrentUser();

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found"));

        organizationAccessService.checkAccess(
                currentUser, product.getOrganization().getId());

        if (!product.isActive()) {
            throw new IllegalArgumentException(
                    "Cannot create a batch for an inactive product");
        }

        if (request.getBatchCode() == null
                || request.getBatchCode().isBlank()) {
            throw new IllegalArgumentException("Batch code is required");
        }

        if (request.getProductionUnit() == null
                || request.getProductionUnit().isBlank()) {
            throw new IllegalArgumentException(
                    "Production unit is required");
        }

        String batchCode = request.getBatchCode()
                .trim()
                .toUpperCase();

        if (batchRepository.existsByBatchCode(batchCode)) {
            throw new IllegalArgumentException(
                    "Batch code already exists");
        }

        ProductionBatch batch = new ProductionBatch();
        batch.setProduct(product);
        batch.setBatchCode(batchCode);
        batch.setProductionQuantity(request.getProductionQuantity());
        batch.setProductionUnit(request.getProductionUnit().trim());
        batch.setProductionDate(request.getProductionDate());

        return batchRepository.save(batch);
    }

    @Transactional(readOnly = true)
    public List<ProductionBatch> getBatchesForProduct(Long productId) {

        AppUser currentUser = getCurrentUser();

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Product not found"));

        organizationAccessService.checkAccess(
                currentUser, product.getOrganization().getId());

        return batchRepository.findByProduct_Id(productId);
    }

    /**
     * Retrieves production batches across all organizations.
     * Only an active administrator can use this method.
     */
    @Transactional(readOnly = true)
    public List<ProductionBatch> getAllBatchesForAdmin() {

        adminAccessService.requireAdmin();

        return batchRepository.findAll();
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
                        new AccessDeniedException("Active user not found"));
    }
}
