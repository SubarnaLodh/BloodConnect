package com.bloodconnect.model;

import java.io.Serializable;

/**
 * =========================================================================
 * Unit 1 & Unit 2 Concept: Enums, Fields, Constructors & Methods
 * Represents the urgency level of a blood donation request.
 * Helps prioritize emergency matching and donor response times.
 * =========================================================================
 */
public enum UrgencyLevel implements Serializable {
    CRITICAL("Critical"),
    URGENT("Urgent"),
    ROUTINE("Routine");

    // Field to hold the human-readable display label
    private final String displayName;

    // Enum constructor (private by default in Java)
    UrgencyLevel(String displayName) {
        this.displayName = displayName;
    }

    /**
     * Returns the user-friendly display name of this urgency level.
     *
     * @return display label (e.g., "Critical", "Urgent", "Routine")
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Overriding toString() allows UI components (like JComboBox)
     * to automatically display the friendly name.
     */
    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Utility method to safely parse a string into an UrgencyLevel.
     * Defaults to ROUTINE if input is null or unrecognized.
     *
     * @param text input string
     * @return matching UrgencyLevel enum constant
     */
    public static UrgencyLevel fromString(String text) {
        if (text == null) {
            return ROUTINE;
        }
        String clean = text.trim();
        for (UrgencyLevel level : values()) {
            if (level.displayName.equalsIgnoreCase(clean) || level.name().equalsIgnoreCase(clean)) {
                return level;
            }
        }
        return ROUTINE;
    }
}
