package com.bloodconnect.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * =========================================================================
 * Unit 1 & Unit 2 Concept: Inheritance, Polymorphism, Encapsulation & Collections
 * Represents a registered blood donor in the BloodConnect application.
 * Extends the abstract Person parent class.
 *
 * EDUCATIONAL DISCLAIMER (Requirement 7):
 * The eligibility criteria in this class are educational demonstration rules
 * (e.g., age 18-65, weight >= 50kg, 90-day cooldown interval) designed for
 * algorithm matching and academic evaluation. They are NOT a substitute for
 * professional clinical evaluation or official medical screening.
 * =========================================================================
 */
public class Donor extends Person implements Serializable {
    private static final long serialVersionUID = 1L;

    // =========================================================================
    // Configurable Demonstration Eligibility Constants
    // =========================================================================
    public static final int MIN_AGE = 18;
    public static final int MAX_AGE = 65;
    public static final double MIN_WEIGHT_KG = 50.0;
    public static final int DONATION_COOLDOWN_DAYS = 90;

    // Private fields demonstrating encapsulation
    private BloodGroup bloodGroup;
    private int age;
    private double weightKg;
    private LocalDate lastDonationDate;
    private boolean available;
    private int totalDonations;
    private boolean emergencyConsentGranted = false;

    /**
     * Default constructor for serialization and framework support.
     * Consent defaults to false to protect donor privacy.
     */
    public Donor() {
        super();
        this.available = true;
        this.totalDonations = 0;
        this.emergencyConsentGranted = false;
    }

    /**
     * Full parameterized constructor initializing parent and child fields.
     * Requires totalDonations and emergencyConsentGranted to be explicitly provided.
     *
     * @param personId                unique donor identifier (e.g., "DONOR-101")
     * @param fullName                full name of the donor
     * @param phoneNumber             contact phone number
     * @param email                   contact email address
     * @param location                donor's geographical location
     * @param bloodGroup              donor's ABO/Rh blood group
     * @param age                     donor's age in years
     * @param weightKg                donor's weight in kilograms
     * @param lastDonationDate        date of the most recent donation (null if no prior recorded donation)
     * @param available               donor's current donation availability
     * @param totalDonations          cumulative lifetime donation count (must be non-negative)
     * @param emergencyConsentGranted explicit donor consent for emergency contact outreach
     */
    public Donor(String personId, String fullName, String phoneNumber, String email,
                 Location location, BloodGroup bloodGroup, int age, double weightKg,
                 LocalDate lastDonationDate, boolean available, int totalDonations,
                 boolean emergencyConsentGranted) {
        super(personId, fullName, phoneNumber, email, location);
        setBloodGroup(bloodGroup);
        setAge(age);
        setWeightKg(weightKg);
        setLastDonationDate(lastDonationDate);
        setAvailable(available);
        setTotalDonations(totalDonations);
        setEmergencyConsentGranted(emergencyConsentGranted);
    }

    // =========================================================================
    // Getters and Setters with Validation
    // =========================================================================

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        if (bloodGroup == null) {
            throw new IllegalArgumentException("Blood group cannot be null.");
        }
        this.bloodGroup = bloodGroup;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        if (age <= 0) {
            throw new IllegalArgumentException("Age must be a positive number. Provided: " + age);
        }
        this.age = age;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(double weightKg) {
        if (!Double.isFinite(weightKg) || weightKg <= 0.0) {
            throw new IllegalArgumentException("Weight must be a valid positive number. Provided: " + weightKg);
        }
        this.weightKg = weightKg;
    }

    public LocalDate getLastDonationDate() {
        return lastDonationDate;
    }

    public void setLastDonationDate(LocalDate lastDonationDate) {
        if (lastDonationDate != null && lastDonationDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Last donation date cannot be in the future. Provided: " + lastDonationDate);
        }
        this.lastDonationDate = lastDonationDate;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public int getTotalDonations() {
        return totalDonations;
    }

    public void setTotalDonations(int totalDonations) {
        if (totalDonations < 0) {
            throw new IllegalArgumentException("Total donations cannot be negative. Provided: " + totalDonations);
        }
        this.totalDonations = totalDonations;
    }

    public boolean isEmergencyConsentGranted() {
        return emergencyConsentGranted;
    }

    public void setEmergencyConsentGranted(boolean emergencyConsentGranted) {
        this.emergencyConsentGranted = emergencyConsentGranted;
    }

    // =========================================================================
    // Demonstration Eligibility & Cooldown Calculations (Requirement 7, 8, 9, 10)
    // =========================================================================

    /**
     * Calculates the number of days elapsed since the donor's last donation.
     * If no previous donation date exists, cooldown is treated as satisfied by
     * returning Integer.MAX_VALUE.
     *
     * @return days elapsed since last donation, or Integer.MAX_VALUE for donors without prior records
     */
    public long getDaysSinceLastDonation() {
        if (lastDonationDate == null) {
            return Integer.MAX_VALUE; // Cooldown treated as satisfied
        }
        return ChronoUnit.DAYS.between(lastDonationDate, LocalDate.now());
    }

    /**
     * Evaluates whether the donor satisfies all configured demonstration criteria:
     * 1. Donor is marked available.
     * 2. Age is within [MIN_AGE, MAX_AGE].
     * 3. Weight meets MIN_WEIGHT_KG.
     * 4. Donation cooldown interval has passed.
     *
     * @return true if all criteria are satisfied, false otherwise
     */
    public boolean isEligibleForDonation() {
        return available
                && age >= MIN_AGE
                && age <= MAX_AGE
                && weightKg >= MIN_WEIGHT_KG
                && getDaysSinceLastDonation() >= DONATION_COOLDOWN_DAYS;
    }

    /**
     * Returns a List of strings detailing every demonstration check that failed.
     * If the donor is eligible, an empty list is returned.
     *
     * @return List of ineligibility reason descriptions
     */
    public List<String> getIneligibilityReasons() {
        List<String> reasons = new ArrayList<>();

        if (!available) {
            reasons.add("Donor profile is currently set to unavailable.");
        }
        if (age < MIN_AGE) {
            reasons.add("Age (" + age + ") is below the demonstration minimum of " + MIN_AGE + " years.");
        }
        if (age > MAX_AGE) {
            reasons.add("Age (" + age + ") exceeds the demonstration maximum of " + MAX_AGE + " years.");
        }
        if (weightKg < MIN_WEIGHT_KG) {
            reasons.add("Weight (" + weightKg + " kg) is below the demonstration minimum of " + MIN_WEIGHT_KG + " kg.");
        }

        long daysSince = getDaysSinceLastDonation();
        if (daysSince < DONATION_COOLDOWN_DAYS) {
            long daysRemaining = DONATION_COOLDOWN_DAYS - daysSince;
            reasons.add("Donation cooldown active: " + daysSince + " days since last donation (" +
                    daysRemaining + " days remaining of " + DONATION_COOLDOWN_DAYS + "-day rule).");
        }

        return reasons;
    }

    /**
     * Uses BloodGroup.java to retrieve all recipient blood groups that this donor
     * can safely donate red blood cells to.
     *
     * @return unmodifiable list of compatible recipient blood groups
     */
    public List<BloodGroup> getCompatibleRecipientGroups() {
        if (bloodGroup == null) {
            return Collections.emptyList();
        }
        return bloodGroup.getCompatibleRecipientGroups();
    }

    // =========================================================================
    // Implementation of Abstract Methods & toString
    // =========================================================================

    /**
     * Implements the abstract method from Person.
     * Demonstrates Unit 2 Polymorphism.
     *
     * @return "Blood Donor"
     */
    @Override
    public String getRole() {
        return "Blood Donor";
    }

    /**
     * Returns a concise description of the donor without exposing private contact info.
     */
    @Override
    public String toString() {
        return getFullName() + " (ID: " + getPersonId() + ", Blood Group: " +
                (bloodGroup != null ? bloodGroup.getSymbol() : "Unknown") +
                ", Available: " + (available ? "Yes" : "No") + ")";
    }
}
