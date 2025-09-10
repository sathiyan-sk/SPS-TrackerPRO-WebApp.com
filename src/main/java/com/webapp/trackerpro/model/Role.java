package com.webapp.trackerpro.model;

public enum Role {
    ADMIN("Admin"),
    STUDENT("Student"),
    FACULTY("Faculty"),
    HR("HR");

    private final String displayName;

    Role(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static Role fromString(String role) {
        for (Role r : Role.values()) {
            if (r.name().equalsIgnoreCase(role) || r.displayName.equalsIgnoreCase(role)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Invalid role: " + role);
    }
}