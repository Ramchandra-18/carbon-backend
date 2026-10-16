
package com.carboncalculator.backend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "emission_factors")
public class EmissionFactor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false, length = 100)
    private String source;

    @Column(nullable = false, length = 100)
    private String sourceReference;

    @Column(nullable = false, length = 100)
    private String activityUnit;

    @Column(nullable = false, precision = 15, scale = 6)
    private BigDecimal co2eFactor;

    @Column(nullable = false, length = 50)
    private String scope;

    @Column(length = 100)
    private String scope3Category;

    @Column(nullable = false)
    private LocalDate validFrom;

    private LocalDate validUntil;

    @Column(nullable = false)
    private boolean active = true;

    
}
