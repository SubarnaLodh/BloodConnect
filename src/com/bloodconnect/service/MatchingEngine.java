package com.bloodconnect.service;

import com.bloodconnect.model.*;
import com.bloodconnect.util.CompatibilityMatrix;
import com.bloodconnect.util.DistanceCalculator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * =========================================================================
 * Unit 2 Concept: Advanced Collections, Algorithms & Comparable Ranking
 * Central algorithmic engine of the BloodConnect application.
 *
 * Implements:
 * - Blood group compatibility checking using BloodGroup.canDonateTo()
 * - Availability and academic demonstration eligibility evaluation
 * - Geodesic distance calculation via DistanceCalculator (Haversine formula)
 * - Transparent match scoring based on proximity and exact blood group match
 * - Natural ordering and deterministic ranking using Comparable<MatchResult>
 *
 * MEDICAL DISCLAIMER:
 * This algorithm is designed for educational demonstration of red blood cell
 * compatibility and distance-based ranking. It does NOT replace clinical blood
 * typing, antibody screening, laboratory crossmatching, or hospital transfusion approval.
 * =========================================================================
 */
public class MatchingEngine {

    /**
     * Default constructor for MatchingEngine.
     */
    public MatchingEngine() {
    }

    /**
     * Finds and ranks all matching donor candidates for a given blood request.
     * Evaluates every donor in the provided list, calculates distance and match score,
     * assigns ranks, and retains ineligible candidates so the UI can explain reasons.
     *
     * @param request the active blood request (cannot be null)
     * @param donors  list of registered donors (cannot be null)
     * @return sorted List of MatchResult objects ranked by priority
     * @throws IllegalArgumentException if request or donors list is null
     */
    public List<MatchResult> findMatches(BloodRequest request, List<Donor> donors) {
        if (request == null) {
            throw new IllegalArgumentException("Blood request cannot be null.");
        }
        if (donors == null) {
            throw new IllegalArgumentException("Donor list cannot be null.");
        }

        List<MatchResult> evaluatedResults = new ArrayList<>();

        for (Donor donor : donors) {
            // Safely skip null donor entries in the collection
            if (donor == null) {
                continue;
            }

            MatchResult result = evaluateDonor(donor, request);
            if (result != null) {
                evaluatedResults.add(result);
            }
        }

        // Rank all evaluated matches
        return rankMatches(evaluatedResults);
    }

    /**
     * Evaluates a single donor candidate against a blood request.
     * Checks compatibility, availability, demonstration eligibility, distance,
     * and calculates the match score.
     *
     * @param donor   candidate donor to evaluate
     * @param request target blood request
     * @return MatchResult containing the full evaluation details
     * @throws IllegalArgumentException if donor or request is null
     */
    public MatchResult evaluateDonor(Donor donor, BloodRequest request) {
        if (donor == null) {
            throw new IllegalArgumentException("Donor cannot be null.");
        }
        if (request == null) {
            throw new IllegalArgumentException("Blood request cannot be null.");
        }

        BloodGroup donorGroup = donor.getBloodGroup();
        BloodGroup neededGroup = request.getRequiredBloodGroup();

        // 1. RBC Compatibility Check using BloodGroup's existing logic
        boolean isCompatible = (donorGroup != null && neededGroup != null && donorGroup.canDonateTo(neededGroup));
        boolean isExactMatch = (donorGroup != null && neededGroup != null && donorGroup == neededGroup);
        String compatibilityType = CompatibilityMatrix.getCompatibilityType(donorGroup, neededGroup);

        // 2. Availability Check
        boolean isAvailable = donor.isAvailable();

        // 3. Demonstration Eligibility Check (18-65 age, >=50kg, 90-day cooldown)
        boolean isDemoEligible = donor.isEligibleForDonation();

        // Strict Overall Eligibility: Must be compatible, available, and meet demo criteria
        boolean isOverallEligible = isCompatible && isAvailable && isDemoEligible;

        // 4. Safe Distance Calculation (handles missing locations without inventing coordinates)
        double distanceKm = Double.MAX_VALUE;
        Location donorLoc = donor.getLocation();
        Location reqLoc = request.getLocation();

        if (donorLoc != null && reqLoc != null) {
            try {
                distanceKm = DistanceCalculator.calculateDistance(donorLoc, reqLoc);
            } catch (Exception e) {
                // If coordinates are invalid, keep distance as MAX_VALUE
                distanceKm = Double.MAX_VALUE;
            }
        }

        // 5. Match Score Calculation
        double score = calculateMatchScore(donor, request, distanceKm);

        // 6. Build Diagnostic Status Note
        String statusNote;
        if (!isCompatible) {
            statusNote = "Incompatible: " + (donorGroup != null ? donorGroup.getSymbol() : "?") +
                    " cannot donate RBCs to " + (neededGroup != null ? neededGroup.getSymbol() : "?");
        } else if (!isAvailable) {
            statusNote = "Unavailable: Donor profile is marked inactive";
        } else if (!isDemoEligible) {
            List<String> ineligibilityReasons = donor.getIneligibilityReasons();
            statusNote = ineligibilityReasons.isEmpty()
                    ? "Ineligible under demonstration rules"
                    : ineligibilityReasons.get(0);
        } else {
            statusNote = "Eligible (" +
                    (donor.getLastDonationDate() == null
                            ? "First-time donor"
                            : donor.getDaysSinceLastDonation() + " days since last donation") + ")";
        }

        return new MatchResult(donor, distanceKm, score, isOverallEligible, isExactMatch, compatibilityType, statusNote);
    }

    /**
     * Calculates a transparent, educational match score based on distance proximity
     * and exact blood-group preference.
     *
     * FORMULA:
     * 1. Proximity Score (0.0 to 100.0):
     *    ProximityScore = 100.0 / (1.0 + (distanceKm / 10.0))
     *    (If distance is unknown or infinite, ProximityScore = 0.0)
     * 2. Blood Match Bonus:
     *    +25.0 points for exact group match (e.g., B+ -> B+)
     *    +10.0 points for compatible alternative (e.g., O- -> B+)
     *    +0.0 points for incompatible groups
     * 3. Eligibility Attenuation:
     *    If donor is ineligible (incompatible, unavailable, or on cooldown),
     *    the score is scaled down by a factor of 0.25x.
     *
     * Note: This score is strictly an educational ranking indicator.
     *
     * @param donor      donor candidate
     * @param request    blood request
     * @param distanceKm calculated distance in kilometers
     * @return match score rounded to 1 decimal place
     */
    public double calculateMatchScore(Donor donor, BloodRequest request, double distanceKm) {
        if (donor == null || request == null) {
            return 0.0;
        }

        // 1. Proximity score
        double proximityScore = 0.0;
        if (Double.isFinite(distanceKm) && distanceKm >= 0.0 && distanceKm != Double.MAX_VALUE) {
            proximityScore = 100.0 / (1.0 + (distanceKm / 10.0));
        }

        // 2. Blood type compatibility bonus
        double bloodBonus = 0.0;
        BloodGroup donorGroup = donor.getBloodGroup();
        BloodGroup neededGroup = request.getRequiredBloodGroup();

        if (donorGroup != null && neededGroup != null) {
            if (donorGroup == neededGroup) {
                bloodBonus = 25.0; // Exact match bonus
            } else if (donorGroup.canDonateTo(neededGroup)) {
                bloodBonus = 10.0; // Compatible alternative bonus (e.g., universal donor)
            }
        }

        double rawScore = proximityScore + bloodBonus;

        // 3. Attenuate score if donor is not currently eligible
        boolean eligible = (donorGroup != null && neededGroup != null && donorGroup.canDonateTo(neededGroup))
                && donor.isAvailable()
                && donor.isEligibleForDonation();

        double finalScore = eligible ? rawScore : (rawScore * 0.25);

        // Round to 1 decimal place
        return Math.round(finalScore * 10.0) / 10.0;
    }

    /**
     * Ranks a list of match results using MatchResult's natural Comparable ordering.
     *
     * Natural Ordering Applied:
     * 1. Eligible donors come before ineligible donors.
     * 2. Shortest distance comes first.
     * 3. Exact blood-group match preferred over alternative compatible groups.
     * 4. Higher match score as tie-breaker.
     *
     * Consistently assigns sequential ranks (#1, #2, #3, ...) after sorting.
     *
     * @param results list of match results to sort and rank
     * @return sorted and ranked List of MatchResult objects
     */
    public List<MatchResult> rankMatches(List<MatchResult> results) {
        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }

        List<MatchResult> rankedList = new ArrayList<>(results);

        // Sort using MatchResult's natural Comparable<MatchResult> implementation
        Collections.sort(rankedList);

        // Assign integer ranks
        for (int i = 0; i < rankedList.size(); i++) {
            rankedList.get(i).setRank(i + 1);
        }

        return rankedList;
    }

    /**
     * Filters a list of match results to return only those who meet all eligibility criteria.
     *
     * @param results list of match results
     * @return new List containing only eligible MatchResults
     */
    public List<MatchResult> getEligibleMatches(List<MatchResult> results) {
        if (results == null || results.isEmpty()) {
            return new ArrayList<>();
        }

        List<MatchResult> eligibleOnly = new ArrayList<>();
        for (MatchResult mr : results) {
            if (mr != null && mr.isEligible()) {
                eligibleOnly.add(mr);
            }
        }
        return eligibleOnly;
    }
}
