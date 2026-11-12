package com.bloodconnect.exception;

/**
 * =========================================================================
 * Unit 2 Concept: Business Domain Exception Handling & State Retention
 * Thrown when an operation attempts to allocate or match a donor who does
 * not satisfy the configured educational demonstration eligibility criteria
 * (such as active 90-day cooldown, age restrictions, or minimum weight rules).
 *
 * Retains both the donorId and ruleViolated to provide actionable diagnostics
 * to the Smart Matching Engine and UI notifications.
 *
 * EDUCATIONAL DISCLAIMER:
 * These criteria represent configured academic demonstration rules for
 * algorithm matching and are NOT a substitute for professional clinical screening.
 * =========================================================================
 */
public class DonorIneligibleException extends BloodConnectException {
    private static final long serialVersionUID = 1L;

    // Immutable fields identifying the ineligible donor and the rule breached
    private final String donorId;
    private final String ruleViolated;

    /**
     * Constructs a new DonorIneligibleException with the donor ID, rule violated, and error message.
     *
     * @param donorId      unique identifier of the ineligible donor (cannot be null or blank)
     * @param ruleViolated name or description of the rule violated (cannot be null or blank)
     * @param message      detailed human-readable explanation of why the donor cannot donate
     * @throws IllegalArgumentException if donorId or ruleViolated is null or blank
     */
    public DonorIneligibleException(String donorId, String ruleViolated, String message) {
        super(message);
        if (donorId == null || donorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Donor ID cannot be null or blank.");
        }
        if (ruleViolated == null || ruleViolated.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule violated cannot be null or blank.");
        }
        this.donorId = donorId.trim();
        this.ruleViolated = ruleViolated.trim();
    }

    /**
     * Constructs a new DonorIneligibleException with donor ID, rule violated, message, and cause.
     *
     * @param donorId      unique identifier of the ineligible donor (cannot be null or blank)
     * @param ruleViolated name or description of the rule violated (cannot be null or blank)
     * @param message      detailed human-readable explanation of why the donor cannot donate
     * @param cause        underlying throwable that triggered this exception
     * @throws IllegalArgumentException if donorId or ruleViolated is null or blank
     */
    public DonorIneligibleException(String donorId, String ruleViolated, String message, Throwable cause) {
        super(message, cause);
        if (donorId == null || donorId.trim().isEmpty()) {
            throw new IllegalArgumentException("Donor ID cannot be null or blank.");
        }
        if (ruleViolated == null || ruleViolated.trim().isEmpty()) {
            throw new IllegalArgumentException("Rule violated cannot be null or blank.");
        }
        this.donorId = donorId.trim();
        this.ruleViolated = ruleViolated.trim();
    }

    /**
     * Returns the unique identifier of the ineligible donor.
     *
     * @return donor ID string
     */
    public String getDonorId() {
        return donorId;
    }

    /**
     * Returns the name or description of the demonstration rule that was violated.
     *
     * @return rule name (e.g., "Donation Cooldown Active", "Minimum Weight Constraint")
     */
    public String getRuleViolated() {
        return ruleViolated;
    }
}
