package com.bloodconnect.exception;

/**
 * =========================================================================
 * Unit 2 Concept: Custom Exception Subclassing & State Retention
 * Thrown when a lookup or query fails to find a requested entity
 * (such as a donor, recipient, or blood request) by its unique identifier.
 *
 * Retains both the entityType and entityId to provide structured diagnostics
 * and informative error messages to users and GUI panels.
 * =========================================================================
 */
public class EntityNotFoundException extends BloodConnectException {
    private static final long serialVersionUID = 1L;

    // Immutable fields identifying the entity category and missing ID
    private final String entityType;
    private final String entityId;

    /**
     * Constructs a new EntityNotFoundException with the entity type and missing ID.
     * Generates a message in the format: "[entityType] not found with ID: [entityId]".
     *
     * @param entityType name of the entity category (e.g., "Donor", "BloodRequest")
     * @param entityId   unique identifier that failed lookup
     * @throws IllegalArgumentException if entityType or entityId is null or blank
     */
    public EntityNotFoundException(String entityType, String entityId) {
        super(buildMessage(entityType, entityId));
        this.entityType = entityType.trim();
        this.entityId = entityId.trim();
    }

    /**
     * Constructs a new EntityNotFoundException with the entity type, missing ID, and underlying cause.
     * Generates a message in the format: "[entityType] not found with ID: [entityId]".
     *
     * @param entityType name of the entity category (e.g., "Donor", "BloodRequest")
     * @param entityId   unique identifier that failed lookup
     * @param cause      underlying throwable that triggered this exception
     * @throws IllegalArgumentException if entityType or entityId is null or blank
     */
    public EntityNotFoundException(String entityType, String entityId, Throwable cause) {
        super(buildMessage(entityType, entityId), cause);
        this.entityType = entityType.trim();
        this.entityId = entityId.trim();
    }

    /**
     * Helper method to validate constructor inputs and format the standard exception message.
     */
    private static String buildMessage(String entityType, String entityId) {
        if (entityType == null || entityType.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity type cannot be null or blank.");
        }
        if (entityId == null || entityId.trim().isEmpty()) {
            throw new IllegalArgumentException("Entity ID cannot be null or blank.");
        }
        return entityType.trim() + " not found with ID: " + entityId.trim();
    }

    /**
     * Returns the category of the entity that was searched.
     *
     * @return entity category name (e.g., "Donor", "BloodRequest")
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Returns the identifier that could not be found.
     *
     * @return missing entity ID
     */
    public String getEntityId() {
        return entityId;
    }
}
