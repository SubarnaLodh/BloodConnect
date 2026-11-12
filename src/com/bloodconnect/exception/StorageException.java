package com.bloodconnect.exception;

/**
 * =========================================================================
 * Unit 2 & Unit 3 Concept: Exception Wrapping for File I/O & Serialization
 * Custom checked exception thrown when an error occurs during file storage,
 * reading, writing, binary serialization (ObjectOutputStream), or
 * deserialization (ObjectInputStream).
 *
 * Preserves underlying I/O causes (such as IOException, FileNotFoundException,
 * or ClassNotFoundException) to facilitate debugging while maintaining a clean
 * domain exception boundary.
 * =========================================================================
 */
public class StorageException extends BloodConnectException {
    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new StorageException with the specified error message.
     *
     * @param message human-readable description of the storage failure
     */
    public StorageException(String message) {
        super(message);
    }

    /**
     * Constructs a new StorageException with the specified error message and
     * the underlying cause of the failure (e.g., IOException, ClassNotFoundException).
     *
     * @param message human-readable description of the storage failure
     * @param cause   the underlying exception that caused this storage failure
     */
    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
