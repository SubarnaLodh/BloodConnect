package com.bloodconnect.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * =========================================================================
 * Unit 2 Concept: Comparable Interface, Multi-Attribute Ranking & Value Objects
 * Represents the computed evaluation of a candidate donor against a blood request.
 * Contains distance, medical demo eligibility, compatibility score, and natural
 * ordering logic for the Smart Matching Engine.
 * =========================================================================
 */
public class MatchResult implements Comparable<MatchResult>, Serializable {
    private static final long serialVersionUID = 1L;

    private Donor donor;
    private double distanceKm;
    private double matchScore;
    private boolean isEligible;
    private boolean isExactBloodMatch;
    private String compatibilityType; // "Exact Match (B+ -> B+)" or "Universal Match (O- -> B+)"
    private String statusNote;
    private int rank;

    public MatchResult() {
    }

    public MatchResult(Donor donor, double distanceKm, double matchScore, boolean isEligible,
                       boolean isExactBloodMatch, String compatibilityType, String statusNote) {
        this.donor = donor;
        this.distanceKm = distanceKm;
        this.matchScore = matchScore;
        this.isEligible = isEligible;
        this.isExactBloodMatch = isExactBloodMatch;
        this.compatibilityType = compatibilityType;
        this.statusNote = statusNote;
    }

    public Donor getDonor() {
        return donor;
    }

    public void setDonor(Donor donor) {
        this.donor = donor;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public String getFormattedDistance() {
        return String.format("%.1f km", distanceKm);
    }

    public void setDistanceKm(double distanceKm) {
        this.distanceKm = distanceKm;
    }

    public double getMatchScore() {
        return matchScore;
    }

    public void setMatchScore(double matchScore) {
        this.matchScore = matchScore;
    }

    public boolean isEligible() {
        return isEligible;
    }

    public void setEligible(boolean eligible) {
        isEligible = eligible;
    }

    public boolean isExactBloodMatch() {
        return isExactBloodMatch;
    }

    public void setExactBloodMatch(boolean exactBloodMatch) {
        isExactBloodMatch = exactBloodMatch;
    }

    public String getCompatibilityType() {
        return compatibilityType;
    }

    public void setCompatibilityType(String compatibilityType) {
        this.compatibilityType = compatibilityType;
    }

    public String getStatusNote() {
        return statusNote;
    }

    public void setStatusNote(String statusNote) {
        this.statusNote = statusNote;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    /**
     * Natural Ordering for Smart Matching:
     * 1. Demonstration-eligible donors come before ineligible donors.
     * 2. Nearest distance comes first (ascending distance).
     * 3. Exact blood type match comes before compatible universal types.
     * 4. Highest overall match score comes first.
     */
    @Override
    public int compareTo(MatchResult other) {
        if (other == null) return -1;

        // 1. Eligible first
        if (this.isEligible != other.isEligible) {
            return this.isEligible ? -1 : 1;
        }

        // 2. Nearest distance (within 0.1 km considered tie)
        if (Math.abs(this.distanceKm - other.distanceKm) > 0.1) {
            return Double.compare(this.distanceKm, other.distanceKm);
        }

        // 3. Exact blood type match prioritized
        if (this.isExactBloodMatch != other.isExactBloodMatch) {
            return this.isExactBloodMatch ? -1 : 1;
        }

        // 4. Higher overall match score
        return Double.compare(other.matchScore, this.matchScore);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchResult)) return false;
        MatchResult that = (MatchResult) o;
        return Objects.equals(donor, that.donor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(donor);
    }

    @Override
    public String toString() {
        return String.format("#%d %s [%s] - %.1f km (Score: %.1f)",
                rank,
                donor != null ? donor.getFullName() : "N/A",
                donor != null && donor.getBloodGroup() != null ? donor.getBloodGroup().getSymbol() : "?",
                distanceKm,
                matchScore);
    }
}

