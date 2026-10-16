package com.carboncalculator.backend.dto;

import jakarta.validation.constraints.NotNull;

public class UserStatusRequest {
    @NotNull
    private Boolean active;
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}
