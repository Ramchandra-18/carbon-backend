
package com.carboncalculator.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
@Data
public class OrganizationRequest {

    @NotBlank(message = "Organization name is required")
    @Size(max = 150)
    private String name;

    @NotBlank(message = "Organization code is required")
    @Size(max = 50)
    private String organizationCode;

    @Size(max = 100)
    private String industry;

    @Size(max = 100)
    private String country;

   

   
}
