
package com.carboncalculator.backend.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import com.carboncalculator.backend.dto.LoginRequest;
import com.carboncalculator.backend.dto.LoginResponse;
import com.carboncalculator.backend.dto.RegistrationRequest;
import com.carboncalculator.backend.entity.AppUser;
import com.carboncalculator.backend.enums.Role;
import com.carboncalculator.backend.repository.AppUserRepository;
import com.carboncalculator.backend.security.JwtService;
import com.carboncalculator.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final AppUserRepository appUserRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${app.jwt.expiration-ms:900000}")
    private long expirationMs;

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                request.getEmail().trim(),
                                request.getPassword()
                        )
                );

        AppUser user = appUserRepository
                .findByEmailIgnoreCase(authentication.getName())
                .filter(AppUser::isActive)
                .orElseThrow(() ->
                        new BadCredentialsException(
                                "Invalid email or password"
                        )
                );

        UserDetails principal =
                (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(principal);

        LoginResponse.UserInfo userInfo =
                new LoginResponse.UserInfo(
                        user.getId(),
                        user.getFullName(),
                        user.getEmail(),
                        user.getRole().name(),
                        user.getOrganization() == null
                                ? null
                                : user.getOrganization().getId()
                );

        return new LoginResponse(
                token,
                expirationMs / 1000,
                userInfo
        );
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody RegistrationRequest request) {

        // Public registration must not require Admin authentication.
        // Public accounts are created as Stakeholders.
        AppUser user = userService.registerPublicUser(
                request.getFullName(),
                request.getEmail(),
                request.getPassword(),
                Role.STAKEHOLDER,
                request.getOrganizationId()
        );

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("message", "Registration successful");
        response.put("id", user.getId());
        response.put("fullName", user.getFullName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put(
                "organizationId",
                user.getOrganization() == null
                        ? null
                        : user.getOrganization().getId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(
            Authentication authentication) {

        if (authentication == null
                || !authentication.isAuthenticated()) {
            throw new org.springframework.security.authentication
                    .AuthenticationCredentialsNotFoundException(
                            "Authentication required"
                    );
        }

        AppUser user = appUserRepository
                .findByEmailIgnoreCase(authentication.getName())
                .filter(AppUser::isActive)
                .orElseThrow(() ->
                        new AuthenticationException(
                                "User not found or inactive"
                        ) {}
                );

        Map<String, Object> response = new LinkedHashMap<>();

        response.put("id", user.getId());
        response.put("fullName", user.getFullName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole().name());
        response.put(
                "organizationId",
                user.getOrganization() == null
                        ? null
                        : user.getOrganization().getId()
        );

        return response;
    }
}
