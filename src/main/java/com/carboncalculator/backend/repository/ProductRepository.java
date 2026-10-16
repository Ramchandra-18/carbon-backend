package com.carboncalculator.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carboncalculator.backend.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByProductCode(String productCode);

    boolean existsByProductCode(String productCode);

    List<Product> findByOrganization_Id(Long organizationId);

    boolean existsByOrganization_IdAndProductCode(
            Long organizationId, String productCode);
}