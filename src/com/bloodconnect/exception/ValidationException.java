package com.bloodconnect.exception;

/**
 * =========================================================================
 * Unit 2 Concept: Custom Exception Subclassing & State Retention
 * Thrown when an input value fails validation constraints (such as invalid
 * email format, non-numeric phone number, or out-of-range numeric fields).
 *
 * Retains the name of the invalid field to allow service and GUI layers to
 * identify and highlight the exact form field that caused the error.
 * =========================================================================
 */
public class ValidationException extends BloodConnectException {
    private static final long serialVersionUID = 1L;

    // Name of the field that failed validation (immutable after construction)
    private final String fieldName;

    /**
     * Constructs a new ValidationException with the invalid field name and error message.
     *
     * @param fieldName name of the input field that failed validation (cannot be null or blank)
     * @param message   human-readable description of why validation failed
     * @throws IllegalArgumentException if fieldName is null or blank
     */
    public ValidationException(String fieldName, String message) {
        super(message);
        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or blank.");
        }
        this.fieldName = fieldName.trim();
    }

    /**
     * Constructs a new ValidationException with the invalid field name, error message, and underlying cause.
     *
     * @param fieldName name of the input field that failed validation (cannot be null or blank)
     * @param message   human-readable description of why validation failed
     * @param cause     underlying throwable that caused this exception
     * @throws IllegalArgumentException if fieldName is null or blank
     */
    public ValidationException(String fieldName, String message, Throwable cause) {
        super(message, cause);
        if (fieldName == null || fieldName.trim().isEmpty()) {
            throw new IllegalArgumentException("Field name cannot be null or blank.");
        }
        this.fieldName = fieldName.trim();
    }

    /**
     * Returns the name of the field that failed validation.
     *
     * @return invalid field name
     */
    public String getFieldName() {
        return fieldName;
    }
}
