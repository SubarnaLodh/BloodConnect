package com.bloodconnect.util;

import com.bloodconnect.model.BloodGroup;

/**
 * =========================================================================
 * Unit 1 & Unit 2 Concept: Lookup Helpers, Text Formatting & Matrices
 * Utility class providing transfusion compatibility descriptions,
 * formatted tables, and ASCII matrices for reports and GUI tooltips.
 *
 * EDUCATIONAL DISCLAIMER:
 * Demonstrates standard red blood cell (RBC) ABO and Rh factor compatibility.
 * It is NOT a clinical substitute for crossmatch laboratory testing.
 * =========================================================================
 */
public final class CompatibilityMatrix {

    private CompatibilityMatrix() {
        // Prevent instantiation
    }

    /**
     * Produces a human-readable compatibility relationship string between donor and recipient.
     *
     * @param donor     donor's blood group
     * @param recipient recipient's blood group
     * @return friendly compatibility description
     */
    public static String getCompatibilityType(BloodGroup donor, BloodGroup recipient) {
        if (donor == null || recipient == null) {
            return "Indeterminate Compatibility";
        }
        if (!donor.canDonateTo(recipient)) {
            return "Incompatible (" + donor.getSymbol() + " cannot donate RBCs to " + recipient.getSymbol() + ")";
        }
        if (donor == recipient) {
            return "Exact Match (" + donor.getSymbol() + " -> " + recipient.getSymbol() + ")";
        }
        if (donor == BloodGroup.O_NEG) {
            return "Universal Donor Match (O- -> " + recipient.getSymbol() + ")";
        }
        if (recipient == BloodGroup.AB_POS) {
            return "Universal Recipient Match (" + donor.getSymbol() + " -> AB+)";
        }
        return "Compatible RBC Match (" + donor.getSymbol() + " -> " + recipient.getSymbol() + ")";
    }

    /**
     * Generates a clean text ASCII matrix showing the full 8x8 RBC transfusion rules.
     * Useful for reports and text console exports.
     *
     * @return formatted 8x8 ASCII compatibility grid
     */
    public static String getCompatibilityTextTable() {
        BloodGroup[] all = BloodGroup.values();
        StringBuilder sb = new StringBuilder();
        sb.append("=================================================================\n");
        sb.append("       RED BLOOD CELL (RBC) TRANSFUSION COMPATIBILITY MATRIX      \n");
        sb.append("=================================================================\n");
        sb.append(String.format("%-10s", "Donor \\ Rec:"));
        for (BloodGroup rec : all) {
            sb.append(String.format("%-7s", rec.getSymbol()));
        }
        sb.append("\n-----------------------------------------------------------------\n");

        for (BloodGroup donor : all) {
            sb.append(String.format("%-10s", donor.getSymbol()));
            for (BloodGroup rec : all) {
                boolean canDonate = donor.canDonateTo(rec);
                sb.append(String.format("%-7s", canDonate ? "[YES]" : "  -  "));
            }
            sb.append("\n");
        }
        sb.append("=================================================================\n");
        sb.append("Legend: [YES] Compatible Red Blood Cell Transfusion | [-] Incompatible\n");
        return sb.toString();
    }
}

