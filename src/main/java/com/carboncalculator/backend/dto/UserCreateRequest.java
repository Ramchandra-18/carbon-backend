
package com.carboncalculator.backend.dto;

import com.carboncalculator.backend.enums.Role;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class UserCreateRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 100)
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Enter a valid email")
    @Size(max = 150)
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 12, max = 72,
          message = "Password must contain 12 to 72 characters")
    private String password;

    @NotNull(message = "Role is required")
    private Role role;

    private Long organizationId;

    
}
