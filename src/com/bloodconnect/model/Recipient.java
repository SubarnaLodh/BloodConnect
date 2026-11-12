package com.bloodconnect.model;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * =========================================================================
 * Unit 1 & Unit 2 Concept: Inheritance, Polymorphism, and Encapsulation
 * Represents a patient or recipient requiring a blood donation in the
 * BloodConnect application. Extends the abstract Person parent class.
 *
 * EDUCATIONAL DISCLAIMER:
 * This class represents a demonstration profile for blood matching simulations.
 * It does NOT perform medical assessments, clinical diagnoses, or transfusion
 * suitability evaluations.
 * =========================================================================
 */
public class Recipient extends Person implements Serializable {
    private static final long serialVersionUID = 1L;

    // Private fields demonstrating encapsulation
    private BloodGroup requiredBloodGroup;
    private int unitsRequired;
    private UrgencyLevel urgencyLevel;
    private String hospitalName;
    private LocalDateTime requestDateTime;
    private Location hospitalLocation;

    /**
     * Default constructor for serialization and framework support.
     * Does not invent default personal or hospital values.
     */
    public Recipient() {
        super();
    }

    /**
     * Full parameterized constructor initializing parent and recipient fields.
     *
     * @param personId           unique identifier for the recipient (e.g., "REC-101")
     * @param fullName           recipient's full name
     * @param phoneNumber        emergency contact phone number
     * @param email              emergency contact email address
     * @param location           recipient's local area/residential location
     * @param requiredBloodGroup required ABO/Rh blood group
     * @param unitsRequired      number of blood units needed (must be positive)
     * @param urgencyLevel       urgency classification (Critical, Urgent, Routine)
     * @param hospitalName       admitting hospital or healthcare facility name
     * @param requestDateTime    timestamp when the request was initiated
     * @param hospitalLocation   geographic location of the hospital
     */
    public Recipient(String personId, String fullName, String phoneNumber, String email,
                     Location location, BloodGroup requiredBloodGroup, int unitsRequired,
                     UrgencyLevel urgencyLevel, String hospitalName,
                     LocalDateTime requestDateTime, Location hospitalLocation) {
        super(personId, fullName, phoneNumber, email, location);
        setRequiredBloodGroup(requiredBloodGroup);
        setUnitsRequired(unitsRequired);
        setUrgencyLevel(urgencyLevel);
        setHospitalName(hospitalName);
        setRequestDateTime(requestDateTime);
        setHospitalLocation(hospitalLocation);
    }

    // =========================================================================
    // Getters and Setters with Validation
    // =========================================================================

    public BloodGroup getRequiredBloodGroup() {
        return requiredBloodGroup;
    }

    public void setRequiredBloodGroup(BloodGroup requiredBloodGroup) {
        if (requiredBloodGroup == null) {
            throw new IllegalArgumentException("Required blood group cannot be null.");
        }
        this.requiredBloodGroup = requiredBloodGroup;
    }

    public int getUnitsRequired() {
        return unitsRequired;
    }

    public void setUnitsRequired(int unitsRequired) {
        if (unitsRequired <= 0) {
            throw new IllegalArgumentException("Units required must be a positive integer. Provided: " + unitsRequired);
        }
        this.unitsRequired = unitsRequired;
    }

    public UrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(UrgencyLevel urgencyLevel) {
        if (urgencyLevel == null) {
            throw new IllegalArgumentException("Urgency level cannot be null.");
        }
        this.urgencyLevel = urgencyLevel;
    }

    public String getHospitalName() {
        return hospitalName;
    }

    public void setHospitalName(String hospitalName) {
        if (hospitalName == null || hospitalName.trim().isEmpty()) {
            throw new IllegalArgumentException("Hospital name cannot be null or blank.");
        }
        this.hospitalName = hospitalName.trim();
    }

    public LocalDateTime getRequestDateTime() {
        return requestDateTime;
    }

    public void setRequestDateTime(LocalDateTime requestDateTime) {
        if (requestDateTime == null) {
            throw new IllegalArgumentException("Request date and time cannot be null.");
        }
        if (requestDateTime.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Request date and time cannot be in the future. Provided: " + requestDateTime);
        }
        this.requestDateTime = requestDateTime;
    }

    public Location getHospitalLocation() {
        return hospitalLocation;
    }

    public void setHospitalLocation(Location hospitalLocation) {
        if (hospitalLocation == null) {
            throw new IllegalArgumentException("Hospital location cannot be null.");
        }
        this.hospitalLocation = hospitalLocation;
    }

    // =========================================================================
    // Implementation of Abstract Methods & toString
    // =========================================================================

    /**
     * Implements the abstract method from Person.
     * Demonstrates Unit 2 Polymorphism.
     *
     * @return "Blood Recipient"
     */
    @Override
    public String getRole() {
        return "Blood Recipient";
    }

    /**
     * Returns a formatted summary of the recipient showing name, ID,
     * required blood group, units, and urgency level without exposing
     * private contact info.
     */
    @Override
    public String toString() {
        return getFullName() + " (ID: " + getPersonId() + ", Needs: " +
                (requiredBloodGroup != null ? requiredBloodGroup.getSymbol() : "Unknown") +
                ", Units: " + unitsRequired +
                ", Urgency: " + (urgencyLevel != null ? urgencyLevel.getDisplayName() : "Unknown") + ")";
    }
}
