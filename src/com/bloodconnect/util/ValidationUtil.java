package com.bloodconnect.util;

import com.bloodconnect.exception.ValidationException;

import java.util.regex.Pattern;

/**
 * =========================================================================
 * Unit 1 & Unit 2 Concept: Regular Expressions, Static Methods & Input Sanitization
 * Central utility class for validating user inputs and form fields across the application.
 *
 * Enforces Requirement 10: Exception handling and robust input validation.
 * When a field fails validation, throws a ValidationException containing the exact
 * fieldName to enable Swing forms to highlight the offending input component.
 * =========================================================================
 */
public final class ValidationUtil {

    // Precompiled regex patterns for optimal performance
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(\\+?\\d{1,3}[- ]?)?\\d{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[a-zA-Z\\s.'-]{2,50}$");

    // Private constructor to prevent instantiation of utility class
    private ValidationUtil() {
    }

    /**
     * Validates whether a full name contains only valid alphabetic characters,
     * spaces, dots, or hyphens (2 to 50 characters).
     *
     * @param name input name string
     * @return true if valid, false otherwise
     */
    public static boolean isValidName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    /**
     * Validates phone number format. Supports 10-digit mobile numbers
     * with optional international country code prefixes (e.g., "+91 9876543210" or "9876543210").
     *
     * @param phone input phone string
     * @return true if valid, false otherwise
     */
    public static boolean isValidPhone(String phone) {
        if (phone == null) {
            return false;
        }
        String clean = phone.replaceAll("[\\s-]", "");
        return PHONE_PATTERN.matcher(clean).matches();
    }

    /**
     * Validates email address syntax according to standard format.
     *
     * @param email input email string
     * @return true if valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * Validates donor age within human demonstration boundaries (18 to 65 years).
     *
     * @param age age in years
     * @return true if within 18 and 65
     */
    public static boolean isValidAge(int age) {
        return age >= 18 && age <= 65;
    }

    /**
     * Validates donor weight within reasonable human physiological range.
     *
     * @param weightKg body weight in kilograms
     * @return true if between 45.0 kg and 200.0 kg
     */
    public static boolean isValidWeight(double weightKg) {
        return Double.isFinite(weightKg) && weightKg >= 45.0 && weightKg <= 200.0;
    }

    /**
     * Performs atomic validation of all donor registration form inputs.
     * Throws a ValidationException on the first constraint violated.
     *
     * @param name     donor's full name
     * @param phone    contact phone number
     * @param email    contact email address
     * @param age      donor age in years
     * @param weightKg donor weight in kilograms
     * @throws ValidationException on the first invalid field
     */
    public static void validateDonorInput(String name, String phone, String email,
                                          int age, double weightKg) throws ValidationException {
        if (!isValidName(name)) {
            throw new ValidationException("fullName", "Name must contain only alphabets and spaces (2 to 50 characters).");
        }
        if (!isValidPhone(phone)) {
            throw new ValidationException("phoneNumber", "Phone number must be a valid 10-digit mobile number.");
        }
        if (!isValidEmail(email)) {
            throw new ValidationException("email", "Email address format is invalid (e.g., donor@example.com).");
        }
        if (age < 18 || age > 65) {
            throw new ValidationException("age", "Demonstration criteria requires donor age to be between 18 and 65 years.");
        }
        if (weightKg < 50.0) {
            throw new ValidationException("weightKg", "Demonstration criteria requires donor weight of at least 50.0 kg.");
        }
    }

    /**
     * Performs atomic validation of blood request form inputs.
     *
     * @param patientName  name of patient/recipient
     * @param hospitalName name of admitting hospital
     * @param units        number of blood units requested
     * @throws ValidationException on the first invalid field
     */
    public static void validateRequestInput(String patientName, String hospitalName,
                                            int units) throws ValidationException {
        if (!isValidName(patientName)) {
            throw new ValidationException("patientName", "Patient name must contain only alphabets and spaces (2-50 characters).");
        }
        if (hospitalName == null || hospitalName.trim().length() < 3) {
            throw new ValidationException("hospitalName", "Hospital or clinic name must be at least 3 characters long.");
        }
        if (units <= 0 || units > 20) {
            throw new ValidationException("unitsRequired", "Requested blood units must be between 1 and 20.");
        }
    }
}
