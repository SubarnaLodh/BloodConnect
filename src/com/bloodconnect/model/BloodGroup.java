package com.bloodconnect.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * =========================================================================
 * Unit 1 & Unit 2 Concept: Enums, Encapsulation, and Static Lookup Helpers
 * Represents the 8 standard human blood groups with built-in medical
 * compatibility rules for red blood cell (RBC) transfusions.
 *
 * DISCLAIMER:
 * This implementation is designed for educational and algorithmic demonstration
 * of RBC compatibility. It is NOT a substitute for clinical blood typing,
 * crossmatching, antibody screening, or professional medical advice.
 * =========================================================================
 */
public enum BloodGroup implements Serializable {
    A_POS("A+"),
    A_NEG("A-"),
    B_POS("B+"),
    B_NEG("B-"),
    AB_POS("AB+"),
    AB_NEG("AB-"),
    O_POS("O+"),
    O_NEG("O-");

    private final String symbol;

    BloodGroup(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }

    // =========================================================================
    // Antigen & Rh Attribute Helper Methods
    // =========================================================================

    /**
     * Identifies if this blood group carries the Rh (Rhesus) factor antigen (Rh+).
     *
     * @return true if Rh-positive, false otherwise
     */
    public boolean isRhPositive() {
        return symbol.endsWith("+");
    }

    /**
     * Identifies if this blood group lacks the Rh antigen (Rh-).
     *
     * @return true if Rh-negative, false otherwise
     */
    public boolean isRhNegative() {
        return symbol.endsWith("-");
    }

    /**
     * Extracts the ABO classification ("A", "B", "AB", or "O") without the Rh sign.
     *
     * @return ABO type string
     */
    public String getAboType() {
        return symbol.substring(0, symbol.length() - 1);
    }

    /**
     * Identifies whether this blood group is the universal RBC donor (O negative).
     * O- red blood cells can be administered to all blood groups in emergency settings.
     *
     * @return true only if this blood group is O_NEG
     */
    public boolean isUniversalDonor() {
        return this == O_NEG;
    }

    /**
     * Identifies whether this blood group is the universal RBC recipient (AB positive).
     * AB+ individuals can safely receive RBCs from all 8 blood groups.
     *
     * @return true only if this blood group is AB_POS
     */
    public boolean isUniversalRecipient() {
        return this == AB_POS;
    }

    /**
     * Returns a string array containing all eight blood group symbols.
     * Ideal for populating Swing JComboBox dropdown components.
     *
     * @return String array of all symbols in enum order
     */
    public static String[] getAllSymbols() {
        BloodGroup[] groups = values();
        String[] symbols = new String[groups.length];
        for (int i = 0; i < groups.length; i++) {
            symbols[i] = groups[i].getSymbol();
        }
        return symbols;
    }

    // =========================================================================
    // Parsing & Transfusion Compatibility Logic
    // =========================================================================

    /**
     * Parse blood group from common string representation (e.g., "A+", "O-").
     *
     * @param text input blood group string
     * @return matching BloodGroup enum
     * @throws IllegalArgumentException if invalid
     */
    public static BloodGroup fromString(String text) {
        if (text == null) {
            throw new IllegalArgumentException("Blood group cannot be null");
        }
        String clean = text.trim().toUpperCase().replace("POSITIVE", "+").replace("NEGATIVE", "-");
        for (BloodGroup bg : values()) {
            if (bg.symbol.equalsIgnoreCase(clean) || bg.name().equalsIgnoreCase(clean)) {
                return bg;
            }
        }
        throw new IllegalArgumentException("Unknown blood group: " + text);
    }

    /**
     * Determines whether this blood group can donate red blood cells to the recipient.
     *
     * Educational Medical Transfusion Rules:
     * - O- is universal donor.
     * - AB+ is universal recipient.
     * - Rh- can donate to Rh- and Rh+.
     * - Rh+ can only donate to Rh+.
     *
     * @param recipient the target recipient blood group
     * @return true if compatible for RBC donation, false otherwise
     */
    public boolean canDonateTo(BloodGroup recipient) {
        if (recipient == null) return false;

        // O- can donate to everyone
        if (this == O_NEG) return true;

        // AB+ can receive from everyone
        if (recipient == AB_POS) return true;

        switch (this) {
            case O_POS:
                return recipient == O_POS || recipient == A_POS || recipient == B_POS || recipient == AB_POS;

            case A_NEG:
                return recipient == A_NEG || recipient == A_POS || recipient == AB_NEG || recipient == AB_POS;

            case A_POS:
                return recipient == A_POS || recipient == AB_POS;

            case B_NEG:
                return recipient == B_NEG || recipient == B_POS || recipient == AB_NEG || recipient == AB_POS;

            case B_POS:
                return recipient == B_POS || recipient == AB_POS;

            case AB_NEG:
                return recipient == AB_NEG || recipient == AB_POS;

            case AB_POS:
                return recipient == AB_POS;

            default:
                return false;
        }
    }

    /**
     * Determines whether this blood group can receive red blood cells from the donor.
     * Avoids duplicate logic by delegating directly to donor.canDonateTo(this).
     *
     * @param donor the candidate donor blood group
     * @return true if compatible for RBC reception, false otherwise
     */
    public boolean canReceiveFrom(BloodGroup donor) {
        if (donor == null) return false;
        return donor.canDonateTo(this);
    }

    /**
     * Returns an unmodifiable list of all compatible donor blood groups for this recipient.
     *
     * @return List of compatible donor BloodGroup enums
     */
    public List<BloodGroup> getCompatibleDonorGroups() {
        List<BloodGroup> compatible = new ArrayList<>();
        for (BloodGroup candidate : values()) {
            if (candidate.canDonateTo(this)) {
                compatible.add(candidate);
            }
        }
        return Collections.unmodifiableList(compatible);
    }

    /**
     * Returns an unmodifiable list of all recipient blood groups that this blood group
     * can donate red blood cells to.
     * Uses the existing canDonateTo() method to avoid any duplicate compatibility logic.
     *
     * @return List of compatible recipient BloodGroup enums
     */
    public List<BloodGroup> getCompatibleRecipientGroups() {
        List<BloodGroup> compatible = new ArrayList<>();
        for (BloodGroup candidate : values()) {
            if (this.canDonateTo(candidate)) {
                compatible.add(candidate);
            }
        }
        return Collections.unmodifiableList(compatible);
    }
}
