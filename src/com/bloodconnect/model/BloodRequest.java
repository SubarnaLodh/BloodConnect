package com.bloodconnect.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * =========================================================================
 * Unit 1 & Unit 2 Concept: Encapsulation, Aggregation & State Transition Logic
 * Represents a blood donation request in the BloodConnect system.
 * Manages the lifecycle of emergency and routine requests, including
 * units fulfillment, donor assignments, and valid state transitions.
 *
 * EDUCATIONAL DISCLAIMER:
 * This application is designed for educational matching demonstration.
 * Assigning a donor profile in this software indicates an algorithmic match
 * and does NOT medically confirm crossmatch compatibility, clinical clearance,
 * or transfusion suitability.
 * =========================================================================
 */
public class BloodRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final DateTimeFormatter DISPLAY_FORMATTER = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    // Private fields demonstrating encapsulation
    private String requestId;
    private Recipient recipient;
    private BloodGroup requiredBloodGroup;
    private int unitsRequired;
    private int unitsFulfilled;
    private UrgencyLevel urgencyLevel;
    private RequestStatus status;
    private Location location;
    private LocalDateTime requestDateTime;
    private List<String> assignedDonorIds;

    /**
     * Default constructor initializing safe structural defaults.
     * Does not invent synthetic recipient, hospital, or personal details.
     */
    public BloodRequest() {
        this.status = RequestStatus.PENDING;
        this.unitsRequired = 1;
        this.unitsFulfilled = 0;
        this.assignedDonorIds = new ArrayList<>();
    }

    /**
     * Full parameterized constructor accepting every field.
     *
     * @param requestId          unique identifier for the blood request (e.g., "REQ-101")
     * @param recipient          associated recipient/patient profile
     * @param requiredBloodGroup required ABO/Rh blood group
     * @param unitsRequired      total units of blood needed (must be positive)
     * @param unitsFulfilled     units currently fulfilled (between 0 and unitsRequired)
     * @param urgencyLevel       urgency classification (Critical, Urgent, Routine)
     * @param status             current lifecycle status (Pending, Matched, Fulfilled, Cancelled)
     * @param location           geographic location of the transfusion request / hospital
     * @param requestDateTime    timestamp when the request was initiated
     * @param assignedDonorIds   initial list of assigned donor IDs (or null to initialize empty)
     */
    public BloodRequest(String requestId, Recipient recipient, BloodGroup requiredBloodGroup,
                        int unitsRequired, int unitsFulfilled, UrgencyLevel urgencyLevel,
                        RequestStatus status, Location location, LocalDateTime requestDateTime,
                        List<String> assignedDonorIds) {
        setRequestId(requestId);
        setRecipient(recipient);
        setRequiredBloodGroup(requiredBloodGroup);
        setUnitsRequired(unitsRequired);
        setUnitsFulfilled(unitsFulfilled);
        setUrgencyLevel(urgencyLevel);
        setStatus(status);
        setLocation(location);
        setRequestDateTime(requestDateTime);
        setAssignedDonorIds(assignedDonorIds);
    }

    // =========================================================================
    // Getters and Setters with Validation
    // =========================================================================

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            throw new IllegalArgumentException("Request ID cannot be null or blank.");
        }
        this.requestId = requestId.trim();
    }

    public Recipient getRecipient() {
        return recipient;
    }

    public void setRecipient(Recipient recipient) {
        if (recipient == null) {
            throw new IllegalArgumentException("Recipient cannot be null.");
        }
        this.recipient = recipient;
    }

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
        if (unitsFulfilled > unitsRequired) {
            throw new IllegalArgumentException("Units required (" + unitsRequired +
                    ") cannot be less than already fulfilled units (" + unitsFulfilled + ").");
        }
        this.unitsRequired = unitsRequired;
    }

    public int getUnitsFulfilled() {
        return unitsFulfilled;
    }

    public void setUnitsFulfilled(int unitsFulfilled) {
        if (unitsFulfilled < 0) {
            throw new IllegalArgumentException("Fulfilled units cannot be negative. Provided: " + unitsFulfilled);
        }
        if (unitsFulfilled > this.unitsRequired && this.unitsRequired > 0) {
            throw new IllegalArgumentException("Fulfilled units (" + unitsFulfilled +
                    ") cannot exceed required units (" + this.unitsRequired + ").");
        }
        this.unitsFulfilled = unitsFulfilled;
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

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Request status cannot be null.");
        }
        this.status = status;
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

    /**
     * Returns an unmodifiable view of assigned donor IDs for collection safety.
     */
    public List<String> getAssignedDonorIds() {
        return Collections.unmodifiableList(assignedDonorIds);
    }

    /**
     * Safely sets the assigned donor IDs, validating against null, blank, and duplicates.
     */
    public void setAssignedDonorIds(List<String> donorIds) {
        this.assignedDonorIds = new ArrayList<>();
        if (donorIds != null) {
            for (String donorId : donorIds) {
                if (donorId == null || donorId.trim().isEmpty()) {
                    throw new IllegalArgumentException("Assigned donor ID cannot be null or blank.");
                }
                String cleanId = donorId.trim();
                if (this.assignedDonorIds.contains(cleanId)) {
                    throw new IllegalArgumentException("Duplicate donor ID detected: " + cleanId);
                }
                this.assignedDonorIds.add(cleanId);
            }
        }
    }

    // =========================================================================
    // Lifecycle & Status Transition Methods
    // =========================================================================

    /**
     * Assigns a donor to this request.
     * Transition Rule:
     * - Cannot assign to CANCELLED or FULFILLED requests.
     * - Prevents duplicate assignments.
     * - If currently PENDING, assigning the first donor transitions status to MATCHED.
     * - Does NOT automatically fulfill the request.
     *
     * @param donorId identifier of the donor being assigned
     */
    public void addAssignedDonor(String donorId) {
        if (status == RequestStatus.CANCELLED) {
            throw new IllegalStateException("Cannot assign a donor to a cancelled request (Request ID: " + requestId + ").");
        }
        if (status == RequestStatus.FULFILLED) {
            throw new IllegalStateException("Cannot assign a donor to an already fulfilled request (Request ID: " + requestId + ").");
        }
        if (donorId == null || donorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Donor ID cannot be null or blank.");
        }

        String cleanId = donorId.trim();
        if (assignedDonorIds.contains(cleanId)) {
            throw new IllegalArgumentException("Donor ID '" + cleanId + "' is already assigned to this request.");
        }

        assignedDonorIds.add(cleanId);

        // First donor assignment transitions PENDING to MATCHED
        if (status == RequestStatus.PENDING) {
            this.status = RequestStatus.MATCHED;
        }
    }

    /**
     * Removes an assigned donor.
     * Transition Rule:
     * - If all assigned donors are removed and no units have been fulfilled,
     *   status returns from MATCHED to PENDING.
     *
     * @param donorId identifier of the donor to remove
     * @return true if the donor was present and removed, false otherwise
     */
    public boolean removeAssignedDonor(String donorId) {
        if (donorId == null || donorId.trim().isEmpty()) {
            return false;
        }

        boolean removed = assignedDonorIds.remove(donorId.trim());
        if (removed && assignedDonorIds.isEmpty() && status == RequestStatus.MATCHED && unitsFulfilled == 0) {
            this.status = RequestStatus.PENDING;
        }
        return removed;
    }

    /**
     * Updates the fulfilled blood units count.
     * Transition Rule:
     * - If fulfilled units equals unitsRequired, status transitions to FULFILLED.
     * - Fulfilled units cannot exceed unitsRequired.
     *
     * @param units updated fulfilled units count
     */
    public void updateFulfilledUnits(int units) {
        if (status == RequestStatus.CANCELLED) {
            throw new IllegalStateException("Cannot update fulfilled units on a cancelled request.");
        }
        if (units < 0) {
            throw new IllegalArgumentException("Fulfilled units cannot be negative. Provided: " + units);
        }
        if (units > unitsRequired) {
            throw new IllegalArgumentException("Fulfilled units (" + units +
                    ") cannot exceed required units (" + unitsRequired + ").");
        }

        this.unitsFulfilled = units;

        if (this.unitsFulfilled == this.unitsRequired) {
            this.status = RequestStatus.FULFILLED;
        }
    }

    /**
     * Explicitly marks this request as MATCHED.
     */
    public void markAsMatched() {
        if (status == RequestStatus.CANCELLED) {
            throw new IllegalStateException("Cannot match a cancelled request.");
        }
        if (status == RequestStatus.FULFILLED) {
            throw new IllegalStateException("Cannot match an already fulfilled request.");
        }
        this.status = RequestStatus.MATCHED;
    }

    /**
     * Explicitly marks this request as FULFILLED.
     * Transition Rule:
     * - A request can become FULFILLED only when unitsFulfilled equals unitsRequired.
     */
    public void markAsFulfilled() {
        if (status == RequestStatus.CANCELLED) {
            throw new IllegalStateException("Cannot fulfill a cancelled request.");
        }
        if (unitsFulfilled != unitsRequired) {
            throw new IllegalStateException("Cannot mark request as fulfilled: fulfilled units (" +
                    unitsFulfilled + ") does not equal required units (" + unitsRequired + ").");
        }
        this.status = RequestStatus.FULFILLED;
    }

    /**
     * Cancels this request.
     * Transition Rule:
     * - A FULFILLED request cannot be cancelled.
     */
    public void cancelRequest() {
        if (status == RequestStatus.FULFILLED) {
            throw new IllegalStateException("Cannot cancel an already fulfilled request.");
        }
        this.status = RequestStatus.CANCELLED;
    }

    /**
     * Checks if this request has been completely fulfilled.
     *
     * @return true if fulfilled units equals units required and status is FULFILLED
     */
    public boolean isFullyFulfilled() {
        return unitsFulfilled == unitsRequired && status == RequestStatus.FULFILLED;
    }

    /**
     * Returns the remaining units of blood needed to fulfill this request.
     *
     * @return remaining units (unitsRequired - unitsFulfilled)
     */
    public int getRemainingUnits() {
        return unitsRequired - unitsFulfilled;
    }

    public String getFormattedRequestTime() {
        return requestDateTime != null ? requestDateTime.format(DISPLAY_FORMATTER) : "N/A";
    }

    // =========================================================================
    // Standard Object Overrides
    // =========================================================================

    /**
     * Returns a summary of the request without leaking private personal details
     * like phone numbers or email addresses.
     */
    @Override
    public String toString() {
        String hospital = (recipient != null && recipient.getHospitalName() != null)
                ? recipient.getHospitalName()
                : "Hospital";
        String group = (requiredBloodGroup != null) ? requiredBloodGroup.getSymbol() : "Unknown";
        String urgency = (urgencyLevel != null) ? urgencyLevel.getDisplayName() : "Unknown";
        String stat = (status != null) ? status.getDisplayName() : "Unknown";

        return "#" + requestId + " [" + group + " - " + unitsRequired + " Units] @ " +
                hospital + " (Urgency: " + urgency + ", Status: " + stat + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BloodRequest)) return false;
        BloodRequest that = (BloodRequest) o;
        return Objects.equals(requestId, that.requestId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(requestId);
    }
}
