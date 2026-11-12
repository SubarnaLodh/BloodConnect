package com.bloodconnect.service;

import com.bloodconnect.exception.EntityNotFoundException;
import com.bloodconnect.exception.StorageException;
import com.bloodconnect.exception.ValidationException;
import com.bloodconnect.model.BloodGroup;
import com.bloodconnect.model.Donor;
import com.bloodconnect.storage.Storage;
import com.bloodconnect.util.ValidationUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * =========================================================================
 * Unit 2 & Unit 3 Concept: Service Layer Architecture, Collections & Storage
 * Service class managing registered donors, validation, search queries,
 * and donor contact privacy controls.
 *
 * Implements:
 * - Requirement 7: Configurable demonstration eligibility queries.
 * - Requirement 8: Privacy protection (masked contact details by default,
 *   revealed only upon explicit coordinator/emergency consent).
 * =========================================================================
 */
public class DonorService {

    private final Storage<Donor, String> donorStorage;

    /**
     * Constructs a DonorService backed by persistent storage.
     *
     * @param donorStorage storage repository for donors
     * @throws IllegalArgumentException if donorStorage is null
     */
    public DonorService(Storage<Donor, String> donorStorage) {
        if (donorStorage == null) {
            throw new IllegalArgumentException("Donor storage cannot be null.");
        }
        this.donorStorage = donorStorage;
    }

    /**
     * Registers a new donor after validating all input parameters.
     *
     * @param donor candidate donor to register
     * @throws ValidationException if any donor field fails validation
     * @throws StorageException    if file persistence fails
     */
    public void registerDonor(Donor donor) throws ValidationException, StorageException {
        if (donor == null) {
            throw new ValidationException("donor", "Donor object cannot be null.");
        }

        // Validate basic personal inputs using ValidationUtil
        ValidationUtil.validateDonorInput(
                donor.getFullName(),
                donor.getPhoneNumber(),
                donor.getEmail(),
                donor.getAge(),
                donor.getWeightKg()
        );

        if (donor.getBloodGroup() == null) {
            throw new ValidationException("bloodGroup", "A valid blood group must be selected.");
        }
        if (donor.getLocation() == null) {
            throw new ValidationException("location", "Donor location details must be specified.");
        }

        // Save to persistent file storage
        donorStorage.save(donor);
    }

    /**
     * Updates an existing donor's information.
     *
     * @param donor updated donor details
     * @throws EntityNotFoundException if donor ID does not exist in storage
     * @throws ValidationException     if updated details fail validation
     * @throws StorageException        if file persistence fails
     */
    public void updateDonor(Donor donor) throws EntityNotFoundException, ValidationException, StorageException {
        if (donor == null) {
            throw new ValidationException("donor", "Donor object cannot be null.");
        }

        // Verify donor exists
        Donor existing = donorStorage.findById(donor.getPersonId());
        if (existing == null) {
            throw new EntityNotFoundException("Donor", donor.getPersonId());
        }

        // Validate fields
        ValidationUtil.validateDonorInput(
                donor.getFullName(),
                donor.getPhoneNumber(),
                donor.getEmail(),
                donor.getAge(),
                donor.getWeightKg()
        );

        donorStorage.save(donor);
    }

    /**
     * Deletes a donor by unique ID.
     *
     * @param donorId identifier of the donor to remove
     * @return true if deleted
     * @throws EntityNotFoundException if donor is not found
     * @throws StorageException        if file operation fails
     */
    public boolean deleteDonor(String donorId) throws EntityNotFoundException, StorageException {
        if (donorId == null || donorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Donor ID cannot be null or blank.");
        }
        boolean removed = donorStorage.deleteById(donorId.trim());
        if (!removed) {
            throw new EntityNotFoundException("Donor", donorId);
        }
        return true;
    }

    /**
     * Retrieves a donor by unique ID.
     *
     * @param donorId donor identifier
     * @return matching Donor
     * @throws EntityNotFoundException if donor is not found
     * @throws StorageException        if file reading fails
     */
    public Donor getDonorById(String donorId) throws EntityNotFoundException, StorageException {
        if (donorId == null || donorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Donor ID cannot be null or blank.");
        }
        Donor donor = donorStorage.findById(donorId.trim());
        if (donor == null) {
            throw new EntityNotFoundException("Donor", donorId);
        }
        return donor;
    }

    /**
     * Returns an unmodifiable list of all registered donors.
     *
     * @return List of all Donors
     * @throws StorageException if reading fails
     */
    public List<Donor> getAllDonors() throws StorageException {
        return Collections.unmodifiableList(donorStorage.loadAll());
    }

    /**
     * Toggles a donor's availability status (Available <-> Unavailable).
     *
     * @param donorId donor identifier
     * @return new availability state
     * @throws EntityNotFoundException if donor is not found
     * @throws StorageException        if saving fails
     */
    public boolean toggleAvailability(String donorId) throws EntityNotFoundException, StorageException {
        Donor donor = getDonorById(donorId);
        boolean newState = !donor.isAvailable();
        donor.setAvailable(newState);
        donorStorage.save(donor);
        return newState;
    }

    /**
     * Filters registered donors by blood group, availability, and city.
     * Passing null or "ALL" for a filter ignores that criterion.
     *
     * @param bloodGroup     blood group filter (null for all)
     * @param availableOnly  if true, returns only available donors
     * @param cityFilter     case-insensitive city substring (null for all)
     * @param eligibleOnly   if true, returns only donors satisfying demo eligibility
     * @return filtered List of Donors
     * @throws StorageException on storage read error
     */
    public List<Donor> filterDonors(BloodGroup bloodGroup, Boolean availableOnly,
                                    String cityFilter, Boolean eligibleOnly) throws StorageException {
        List<Donor> all = donorStorage.loadAll();
        List<Donor> filtered = new ArrayList<>();

        for (Donor donor : all) {
            if (donor == null) continue;

            // Blood group filter
            if (bloodGroup != null && donor.getBloodGroup() != bloodGroup) {
                continue;
            }

            // Availability filter
            if (Boolean.TRUE.equals(availableOnly) && !donor.isAvailable()) {
                continue;
            }

            // City filter
            if (cityFilter != null && !cityFilter.trim().isEmpty() && !cityFilter.equalsIgnoreCase("ALL")) {
                if (donor.getLocation() == null ||
                        !donor.getLocation().getCity().toLowerCase().contains(cityFilter.trim().toLowerCase())) {
                    continue;
                }
            }

            // Demonstration eligibility filter
            if (Boolean.TRUE.equals(eligibleOnly) && !donor.isEligibleForDonation()) {
                continue;
            }

            filtered.add(donor);
        }

        return Collections.unmodifiableList(filtered);
    }

    // =========================================================================
    // Privacy & Consent-Gated Contact Access (Requirement 8)
    // =========================================================================

    /**
     * Returns a donor's privacy-masked contact number by default.
     *
     * @param donorId donor ID
     * @return masked phone number (e.g., "+91 98***321")
     * @throws EntityNotFoundException if donor is not found
     * @throws StorageException        if reading fails
     */
    public String getMaskedContact(String donorId) throws EntityNotFoundException, StorageException {
        Donor donor = getDonorById(donorId);
        return donor.getMaskedPhoneNumber();
    }

    /**
     * Reveals the unmasked contact number only if explicit emergency or coordinator consent is granted.
     *
     * @param donorId            donor ID
     * @param coordinatorConsent explicit verification flag
     * @return unmasked contact number if consented, or masked number otherwise
     * @throws EntityNotFoundException if donor is not found
     * @throws StorageException        if reading fails
     */
    public String getContactWithConsent(String donorId, boolean coordinatorConsent)
            throws EntityNotFoundException, StorageException {
        Donor donor = getDonorById(donorId);
        if (coordinatorConsent && donor.isEmergencyConsentGranted()) {
            return donor.getPhoneNumber();
        }
        return donor.getMaskedPhoneNumber();
    }
}

