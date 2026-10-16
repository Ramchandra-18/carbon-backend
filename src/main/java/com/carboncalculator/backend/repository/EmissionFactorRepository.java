
package com.carboncalculator.backend.repository;

import com.carboncalculator.backend.entity.EmissionFactor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmissionFactorRepository
        extends JpaRepository<EmissionFactor, Long> {

    List<EmissionFactor> findByScopeAndActiveTrue(String scope);

    List<EmissionFactor> findByNameContainingIgnoreCaseAndActiveTrue(
            String name
    );

    List<EmissionFactor> findByScopeAndScope3CategoryAndActiveTrue(
            String scope,
            String scope3Category
    );
}
