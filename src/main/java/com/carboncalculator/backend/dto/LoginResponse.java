
package com.carboncalculator.backend.dto;

public class LoginResponse {

    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UserInfo user;

    public LoginResponse(
            String accessToken,
            long expiresIn,
            UserInfo user) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public UserInfo getUser() {
        return user;
    }

    public static class UserInfo {
        private Long id;
        private String fullName;
        private String email;
        private String role;
        private Long organizationId;

        public UserInfo(
                Long id,
                String fullName,
                String email,
                String role,
                Long organizationId) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
            this.organizationId = organizationId;
        }

        public Long getId() { return id; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
        public Long getOrganizationId() { return organizationId; }
    }
}
