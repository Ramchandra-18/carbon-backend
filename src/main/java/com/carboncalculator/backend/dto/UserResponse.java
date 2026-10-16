
package com.carboncalculator.backend.dto;

import java.time.LocalDateTime;

import com.carboncalculator.backend.entity.AppUser;
import com.carboncalculator.backend.enums.Role;

import lombok.Data;
@Data
public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private Long organizationId;
    private boolean active;
    private LocalDateTime createdAt;
    public UserResponse(AppUser user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.role = user.getRole();
        this.organizationId = user.getOrganization() == null
                ? null
                : user.getOrganization().getId();
        this.active = user.isActive();
        this.createdAt = user.getCreatedAt();
    }

}
