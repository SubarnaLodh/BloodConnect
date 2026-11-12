package com.bloodconnect.model;

import java.io.Serializable;

/**
 * =========================================================================
 * Unit 1 & Unit 2 Concept: Enums and Lifecycle State Tracking
 * Represents the lifecycle stages of a blood donation request.
 * =========================================================================
 */
public enum RequestStatus implements Serializable {
    PENDING("Pending"),
    MATCHED("Matched"),
    FULFILLED("Fulfilled"),
    CANCELLED("Cancelled");

    // Field to hold the human-readable display label
    private final String displayName;

    // Enum constructor (private by default in Java)
    RequestStatus(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-friendly display name of this request status.
     *
     * @return display label (e.g., "Pending", "Matched", "Fulfilled", "Cancelled")
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Overriding toString() allows UI components (like JTable, JComboBox)
     * to render the friendly name directly.
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Utility method to safely parse a string into a RequestStatus.
     * Defaults to PENDING if input is null or unrecognized.
     *
     * @param text input string
     * @return matching RequestStatus enum constant
     */
    public static RequestStatus fromString(String text) {
        if (text == null) {
            return PENDING;
        }
        String clean = text.trim();
        for (RequestStatus status : values()) {
            if (status.displayName.equalsIgnoreCase(clean) || status.name().equalsIgnoreCase(clean)) {
                return status;
            }
        }
        return PENDING;
    }
}
