package com.webapp.trackerpro.model;

public enum UserStatus {
    PENDING("Pending Approval"),
    ACTIVE("Active"),
    INACTIVE("Inactive"),
    REJECTED("Rejected");

    private final String displayName;

    UserStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}