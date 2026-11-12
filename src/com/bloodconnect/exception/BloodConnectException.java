package com.bloodconnect.exception;

/**
 * =========================================================================
 * Unit 2 Concept: Custom Exception Hierarchy & Checked Exceptions
 * Base checked exception for the BloodConnect application.
 * All domain, validation, eligibility, and storage exceptions in this
 * system extend this class to support organized and centralized error
 * handling across services and GUI panels.
 * =========================================================================
 */
public class BloodConnectException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new BloodConnectException with the specified detail message.
     *
     * @param message human-readable description of the error
     */
    public BloodConnectException(String message) {
        super(message);
    }

    /**
     * Constructs a new BloodConnectException with the specified detail message
     * and underlying cause.
     *
     * @param message human-readable description of the error
     * @param cause   the underlying throwable that caused this exception
     */
    public BloodConnectException(String message, Throwable cause) {
        super(message, cause);
    }
}
