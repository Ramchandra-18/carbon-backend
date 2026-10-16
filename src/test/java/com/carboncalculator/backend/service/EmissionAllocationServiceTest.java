
package com.carboncalculator.backend.service;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import com.carboncalculator.backend.dto.EmissionAllocationRequest;
import com.carboncalculator.backend.entity.AppUser;
import com.carboncalculator.backend.enums.Role;
import com.carboncalculator.backend.repository.ActivityRecordRepository;
import com.carboncalculator.backend.repository.AppUserRepository;
import com.carboncalculator.backend.repository.EmissionAllocationRepository;
import com.carboncalculator.backend.repository.ProductionBatchRepository;

@ExtendWith(MockitoExtension.class)
class EmissionAllocationServiceTest {

    @Mock
    private EmissionAllocationRepository allocationRepository;

    @Mock
    private ActivityRecordRepository activityRecordRepository;

    @Mock
    private ProductionBatchRepository batchRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private OrganizationAccessService organizationAccessService;

    @Mock
    private AdminAccessService adminAccessService;

    @InjectMocks
    private EmissionAllocationService service;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "admin@carbon.local",
                        "test-password",
                        List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
                )
        );
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRejectAllocationWhenActivityDoesNotExist() {
        EmissionAllocationRequest request = new EmissionAllocationRequest();
        request.setActivityRecordId(999L);
        request.setBatchId(1L);
        request.setAllocatedEmissionKgCo2e(new BigDecimal("10"));

        AppUser admin = new AppUser();
        admin.setActive(true);
        admin.setRole(Role.ADMIN);

        when(appUserRepository.findByEmailIgnoreCase("admin@carbon.local"))
                .thenReturn(Optional.of(admin));

        when(activityRecordRepository.findByIdForUpdate(999L))
                .thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createAllocation(request)
        );

        assertEquals("Activity record not found", exception.getMessage());
    }

    @Test
    void shouldRejectNegativeAllocationAmount() {
        EmissionAllocationRequest request = new EmissionAllocationRequest();
        request.setActivityRecordId(1L);
        request.setBatchId(1L);
        request.setAllocatedEmissionKgCo2e(new BigDecimal("-10"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createAllocation(request)
        );

        assertEquals(
                "Allocated emissions must be greater than zero",
                exception.getMessage()
        );

        verifyNoInteractions(
                appUserRepository,
                activityRecordRepository,
                batchRepository,
                allocationRepository,
                organizationAccessService,
                adminAccessService
        );
    }
}
