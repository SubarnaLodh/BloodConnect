package com.bloodconnect.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * =========================================================================
 * Unit 1 & Unit 2 Concept: Abstraction, Encapsulation & Serializable
 * Abstract parent class representing any individual in the BloodConnect system.
 * Demonstrates:
 *  - Abstract class with an abstract method: getRole()
 *  - Encapsulation using private fields with validated getters and setters
 *  - Privacy protection through default contact masking
 * =========================================================================
 */
public abstract class Person implements Serializable {
    private static final long serialVersionUID = 1L;

    // Private fields to demonstrate encapsulation
    private String personId;
    private String fullName;
    private String phoneNumber;
    private String email;
    private Location location;

    /**
     * Default constructor for serialization support.
     */
    protected Person() {
    }

    /**
     * Parameterized constructor to initialize and validate all fields.
     *
     * @param personId    unique identifier for the person (e.g., "DONOR-101")
     * @param fullName    full legal or registered name
     * @param phoneNumber contact phone number
     * @param email       contact email address
     * @param location    geographical location (city, zone, coordinates)
     * @throws IllegalArgumentException if any field fails validation
     */
    public Person(String personId, String fullName, String phoneNumber, String email, Location location) {
        setPersonId(personId);
        setFullName(fullName);
        setPhoneNumber(phoneNumber);
        setEmail(email);
        setLocation(location);
    }

    // =========================================================================
    // Getters and Setters with Validation
    // =========================================================================

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        if (personId == null || personId.trim().isEmpty()) {
            throw new IllegalArgumentException("Person ID cannot be null or blank.");
        }
        this.personId = personId.trim();
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be null or blank.");
        }
        this.fullName = fullName.trim();
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Phone number cannot be null or blank.");
        }
        this.phoneNumber = phoneNumber.trim();
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or blank.");
        }
        String cleanEmail = email.trim();
        // Basic format validation: must contain '@' and '.' with valid placement
        int atIndex = cleanEmail.indexOf('@');
        int dotIndex = cleanEmail.lastIndexOf('.');
        if (atIndex <= 0 || dotIndex <= atIndex + 1 || dotIndex == cleanEmail.length() - 1) {
            throw new IllegalArgumentException("Invalid email format (e.g., user@example.com). Provided: " + email);
        }
        this.email = cleanEmail;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        if (location == null) {
            throw new IllegalArgumentException("Location cannot be null.");
        }
        this.location = location;
    }

    // =========================================================================
    // Privacy Protection: Masked Contact Details (Default Application Display)
    // =========================================================================

    /**
     * Returns a privacy-masked phone number, showing only the last 3 digits.
     * Preserves optional country code prefix if formatted with spaces.
     * Example: "+91 987654321" -> "+91 98***321"
     * Example: "9876543210"    -> "*******210"
     *
     * @return masked phone number string
     */
    public String getMaskedPhoneNumber() {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return "N/A";
        }
        String clean = phoneNumber.trim();
        if (clean.length() <= 3) {
            return "***";
        }

        String lastThree = clean.substring(clean.length() - 3);

        // If formatted with country code prefix like "+91 98...", keep prefix and mask middle
        if (clean.startsWith("+")) {
            int spaceIndex = clean.indexOf(' ');
            if (spaceIndex != -1 && spaceIndex + 3 < clean.length() - 3) {
                String prefix = clean.substring(0, spaceIndex + 3);
                return prefix + "***" + lastThree;
            }
        }

        // Standard masking: mask everything except the last 3 digits
        StringBuilder masked = new StringBuilder();
        int maskLength = clean.length() - 3;
        for (int i = 0; i < maskLength; i++) {
            char ch = clean.charAt(i);
            if (ch == ' ' || ch == '-') {
                masked.append(ch);
            } else {
                masked.append('*');
            }
        }
        masked.append(lastThree);
        return masked.toString();
    }

    /**
     * Returns a privacy-masked email username while preserving the domain.
     * Example: subarna@example.com becomes s*****@example.com
     *
     * @return masked email string
     */
    public String getMaskedEmail() {
        if (email == null || !email.contains("@")) {
            return "N/A";
        }
        int atIndex = email.indexOf('@');
        String username = email.substring(0, atIndex);
        String domain = email.substring(atIndex); // Includes '@'

        if (username.length() <= 1) {
            return "*" + domain;
        }

        StringBuilder masked = new StringBuilder();
        masked.append(username.charAt(0)); // Keep first letter
        for (int i = 1; i < username.length(); i++) {
            masked.append('*');
        }
        masked.append(domain);
        return masked.toString();
    }

    // =========================================================================
    // Abstract Methods & Standard Object Methods
    // =========================================================================

    /**
     * Abstract method to be implemented by child classes (e.g., Donor, Recipient).
     * Demonstrates Unit 2 Polymorphism.
     *
     * @return the role description of the person
     */
    public abstract String getRole();

    /**
     * Overrides toString() to return the person's ID and full name
     * without exposing sensitive contact details like phone number or email.
     */
    @Override
    public String toString() {
        return fullName + " (ID: " + personId + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person)) return false;
        Person person = (Person) o;
        return Objects.equals(personId, person.personId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(personId);
    }
}
