package com.bloodconnect.service;

import com.bloodconnect.exception.EntityNotFoundException;
import com.bloodconnect.exception.StorageException;
import com.bloodconnect.exception.ValidationException;
import com.bloodconnect.model.BloodGroup;
import com.bloodconnect.model.BloodRequest;
import com.bloodconnect.model.RequestStatus;
import com.bloodconnect.model.UrgencyLevel;
import com.bloodconnect.storage.Storage;
import com.bloodconnect.util.ValidationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * =========================================================================
 * Unit 2 & Unit 3 Concept: Service Layer Architecture, Collections & Storage
 * Service class managing blood donation requests, lifecycle state transitions,
 * unit fulfillments, donor assignments, and search filters.
 *
 * Implements:
 * - Requirement 2: Emergency vs. Routine request management.
 * - Requirement 9: State transition invariants (Pending -> Matched -> Fulfilled / Cancelled).
 * - Requirement 10: Input validation and exception handling.
 * =========================================================================
 */
public class BloodRequestService {

    private final Storage<BloodRequest, String> requestStorage;

    /**
     * Constructs a BloodRequestService backed by persistent storage.
     *
     * @param requestStorage storage repository for blood requests
     * @throws IllegalArgumentException if requestStorage is null
     */
    public BloodRequestService(Storage<BloodRequest, String> requestStorage) {
        if (requestStorage == null) {
            throw new IllegalArgumentException("Blood request storage cannot be null.");
        }
        this.requestStorage = requestStorage;
    }

    /**
     * Creates and persists a new blood donation request.
     *
     * @param request candidate blood request to register
     * @throws ValidationException if any input violates validation constraints
     * @throws StorageException    if file persistence fails
     */
    public void createRequest(BloodRequest request) throws ValidationException, StorageException {
        if (request == null) {
            throw new ValidationException("request", "Blood request cannot be null.");
        }
        if (request.getRequestId() == null || request.getRequestId().trim().isEmpty()) {
            throw new ValidationException("requestId", "Request ID cannot be null or blank.");
        }
        if (request.getRecipient() == null) {
            throw new ValidationException("recipient", "Recipient details cannot be null.");
        }
        if (request.getRequiredBloodGroup() == null) {
            throw new ValidationException("requiredBloodGroup", "Required blood group must be specified.");
        }
        if (request.getUrgencyLevel() == null) {
            throw new ValidationException("urgencyLevel", "Urgency level must be specified.");
        }
        if (request.getLocation() == null) {
            throw new ValidationException("location", "Hospital location must be specified.");
        }

        // Validate recipient and units using ValidationUtil
        ValidationUtil.validateRequestInput(
                request.getRecipient().getFullName(),
                request.getRecipient().getHospitalName(),
                request.getUnitsRequired()
        );

        requestStorage.save(request);
    }

    /**
     * Updates an existing blood request.
     *
     * @param request updated request
     * @throws EntityNotFoundException if request ID is not found in storage
     * @throws ValidationException     if updated details fail validation
     * @throws StorageException        if file persistence fails
     */
    public void updateRequest(BloodRequest request) throws EntityNotFoundException, ValidationException, StorageException {
        if (request == null) {
            throw new ValidationException("request", "Blood request cannot be null.");
        }

        BloodRequest existing = requestStorage.findById(request.getRequestId());
        if (existing == null) {
            throw new EntityNotFoundException("BloodRequest", request.getRequestId());
        }

        ValidationUtil.validateRequestInput(
                request.getRecipient().getFullName(),
                request.getRecipient().getHospitalName(),
                request.getUnitsRequired()
        );

        requestStorage.save(request);
    }

    /**
     * Retrieves a blood request by unique identifier.
     *
     * @param requestId request ID
     * @return matching BloodRequest
     * @throws EntityNotFoundException if request is not found
     * @throws StorageException        if file reading fails
     */
    public BloodRequest getRequestById(String requestId) throws EntityNotFoundException, StorageException {
        if (requestId == null || requestId.trim().isEmpty()) {
            throw new IllegalArgumentException("Request ID cannot be null or blank.");
        }
        BloodRequest req = requestStorage.findById(requestId.trim());
        if (req == null) {
            throw new EntityNotFoundException("BloodRequest", requestId);
        }
        return req;
    }

    /**
     * Returns an unmodifiable list of all registered blood requests.
     *
     * @return List of all BloodRequests
     * @throws StorageException if reading fails
     */
    public List<BloodRequest> getAllRequests() throws StorageException {
        return Collections.unmodifiableList(requestStorage.loadAll());
    }

    /**
     * Returns all active requests (either PENDING or MATCHED).
     *
     * @return List of active BloodRequests
     * @throws StorageException if reading fails
     */
    public List<BloodRequest> getActiveRequests() throws StorageException {
        List<BloodRequest> all = requestStorage.loadAll();
        List<BloodRequest> active = new ArrayList<>();
        for (BloodRequest req : all) {
            if (req != null && (req.getStatus() == RequestStatus.PENDING || req.getStatus() == RequestStatus.MATCHED)) {
                active.add(req);
            }
        }
        return Collections.unmodifiableList(active);
    }

    /**
     * Returns all emergency requests (CRITICAL or URGENT) that are currently active.
     *
     * @return List of emergency BloodRequests
     * @throws StorageException if reading fails
     */
    public List<BloodRequest> getEmergencyRequests() throws StorageException {
        List<BloodRequest> all = requestStorage.loadAll();
        List<BloodRequest> emergency = new ArrayList<>();
        for (BloodRequest req : all) {
            if (req != null &&
                    (req.getStatus() == RequestStatus.PENDING || req.getStatus() == RequestStatus.MATCHED) &&
                    (req.getUrgencyLevel() == UrgencyLevel.CRITICAL || req.getUrgencyLevel() == UrgencyLevel.URGENT)) {
                emergency.add(req);
            }
        }
        return Collections.unmodifiableList(emergency);
    }

    /**
     * Assigns a donor to the specified request.
     *
     * @param requestId target request ID
     * @param donorId   donor ID to assign
     * @throws EntityNotFoundException if request is not found
     * @throws StorageException        if storage save fails
     */
    public void assignDonorToRequest(String requestId, String donorId)
            throws EntityNotFoundException, StorageException {
        BloodRequest request = getRequestById(requestId);
        request.addAssignedDonor(donorId);
        requestStorage.save(request);
    }

    /**
     * Removes an assigned donor from the specified request.
     *
     * @param requestId target request ID
     * @param donorId   donor ID to unassign
     * @return true if donor was assigned and removed
     * @throws EntityNotFoundException if request is not found
     * @throws StorageException        if storage save fails
     */
    public boolean removeDonorFromRequest(String requestId, String donorId)
            throws EntityNotFoundException, StorageException {
        BloodRequest request = getRequestById(requestId);
        boolean removed = request.removeAssignedDonor(donorId);
        if (removed) {
            requestStorage.save(request);
        }
        return removed;
    }

    /**
     * Updates the number of units fulfilled for a request.
     *
     * @param requestId      target request ID
     * @param fulfilledUnits updated units count
     * @throws EntityNotFoundException if request is not found
     * @throws StorageException        if storage save fails
     */
    public void updateUnitsFulfilled(String requestId, int fulfilledUnits)
            throws EntityNotFoundException, StorageException {
        BloodRequest request = getRequestById(requestId);
        request.updateFulfilledUnits(fulfilledUnits);
        requestStorage.save(request);
    }

    /**
     * Fully fulfills a request by setting fulfilled units equal to required units
     * and marking the status as FULFILLED.
     *
     * @param requestId target request ID
     * @throws EntityNotFoundException if request is not found
     * @throws StorageException        if storage save fails
     */
    public void fulfillRequest(String requestId) throws EntityNotFoundException, StorageException {
        BloodRequest request = getRequestById(requestId);
        request.updateFulfilledUnits(request.getUnitsRequired());
        request.markAsFulfilled();
        requestStorage.save(request);
    }

    /**
     * Cancels an active blood request.
     *
     * @param requestId target request ID
     * @throws EntityNotFoundException if request is not found
     * @throws StorageException        if storage save fails
     */
    public void cancelRequest(String requestId) throws EntityNotFoundException, StorageException {
        BloodRequest request = getRequestById(requestId);
        request.cancelRequest();
        requestStorage.save(request);
    }

    /**
     * Deletes a blood request by ID.
     *
     * @param requestId request ID to remove
     * @return true if deleted
     * @throws EntityNotFoundException if request is not found
     * @throws StorageException        if storage operation fails
     */
    public boolean deleteRequest(String requestId) throws EntityNotFoundException, StorageException {
        if (requestId == null || requestId.trim().isEmpty()) {
            throw new IllegalArgumentException("Request ID cannot be null or blank.");
        }
        boolean removed = requestStorage.deleteById(requestId.trim());
        if (!removed) {
            throw new EntityNotFoundException("BloodRequest", requestId);
        }
        return true;
    }

    /**
     * Filters blood requests by required blood group, urgency level, and request status.
     * Passing null ignores that filter parameter.
     *
     * @param group   target blood group (or null for all)
     * @param urgency target urgency level (or null for all)
     * @param status  target request status (or null for all)
     * @return filtered List of BloodRequests
     * @throws StorageException on storage read error
     */
    public List<BloodRequest> filterRequests(BloodGroup group, UrgencyLevel urgency,
                                             RequestStatus status) throws StorageException {
        List<BloodRequest> all = requestStorage.loadAll();
        List<BloodRequest> filtered = new ArrayList<>();

        for (BloodRequest req : all) {
            if (req == null) continue;

            if (group != null && req.getRequiredBloodGroup() != group) {
                continue;
            }
            if (urgency != null && req.getUrgencyLevel() != urgency) {
                continue;
            }
            if (status != null && req.getStatus() != status) {
                continue;
            }
            filtered.add(req);
        }

        return Collections.unmodifiableList(filtered);
    }
}

