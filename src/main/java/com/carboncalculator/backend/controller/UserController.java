package com.carboncalculator.backend.controller;

import java.util.List;

import com.carboncalculator.backend.dto.UserCreateRequest;
import com.carboncalculator.backend.dto.UserResponse;
import com.carboncalculator.backend.dto.UserStatusRequest;
import com.carboncalculator.backend.entity.AppUser;
import com.carboncalculator.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserResponse createUser(@Valid @RequestBody UserCreateRequest request) {
        try {
            AppUser user = userService.createUser(request.getFullName(), request.getEmail(),
                    request.getPassword(), request.getRole(), request.getOrganizationId());
            return new UserResponse(user);
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }

    @GetMapping
    public List<UserResponse> getAllUsers() {
        return userService.findAll().stream().map(UserResponse::new).toList();
    }

    @PatchMapping("/{userId}/status")
    public UserResponse updateUserStatus(@PathVariable Long userId,
                                         @Valid @RequestBody UserStatusRequest request) {
        try {
            return new UserResponse(userService.setUserActive(userId, request.getActive()));
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, ex.getMessage());
        }
    }
}
