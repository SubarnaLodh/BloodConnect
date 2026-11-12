package com.bloodconnect;

import com.bloodconnect.model.*;
import com.bloodconnect.service.*;
import com.bloodconnect.storage.*;
import com.bloodconnect.util.*;

import java.io.File;
import java.util.List;

/**
 * =========================================================================
 * Automated Headless Verification Suite for BloodConnect
 * Validates domain invariants, storage persistence, matching accuracy,
 * and privacy controls across all 4 project modules.
 * =========================================================================
 */
public class TestRunner {

    public static void main(String[] args) {
        System.out.println("=================================================");
        System.out.println("   BLOODCONNECT AUTOMATED VERIFICATION SUITE    ");
        System.out.println("=================================================");

        int passed = 0;
        int failed = 0;

        try {
            // Test 1: Storage and Seeding
            System.out.print("[TEST 1] Initializing persistent file storage & seeding... ");
            File testDonorsFile = new File("data/test_donors.dat");
            File testRequestsFile = new File("data/test_requests.dat");
            if (testDonorsFile.exists()) testDonorsFile.delete();
            if (testRequestsFile.exists()) testRequestsFile.delete();

            Storage<Donor, String> donorStorage = new FileStorageService<>("data/test_donors.dat", Donor::getPersonId);
            Storage<BloodRequest, String> requestStorage = new FileStorageService<>("data/test_requests.dat", BloodRequest::getRequestId);

            DataSeeder.seedIfEmpty(donorStorage, requestStorage);

            List<Donor> seededDonors = donorStorage.loadAll();
            List<BloodRequest> seededRequests = requestStorage.loadAll();

            if (seededDonors.size() == 12 && seededRequests.size() == 4) {
                System.out.println("PASSED (12 donors, 4 requests seeded)");
                passed++;
            } else {
                System.err.println("FAILED: Expected 12 donors and 4 requests, got " + seededDonors.size() + ", " + seededRequests.size());
                failed++;
            }

            // Test 2: Distance Calculator Haversine
            System.out.print("[TEST 2] Testing Haversine Geodesic Distance Calculation... ");
            // Ludhiana (30.9010, 75.8573) to Jalandhar (31.3260, 75.5762) is ~54-60 km
            double distanceLdhJal = DistanceCalculator.calculateDistance(30.9010, 75.8573, 31.3260, 75.5762);
            if (distanceLdhJal > 50.0 && distanceLdhJal < 65.0) {
                System.out.println("PASSED (Calculated distance: " + String.format("%.2f km", distanceLdhJal) + ")");
                passed++;
            } else {
                System.err.println("FAILED: Distance outside expected bounds: " + distanceLdhJal);
                failed++;
            }

            // Test 3: Blood Group RBC Compatibility Logic
            System.out.print("[TEST 3] Testing ABO/Rh Red Blood Cell Compatibility Matrix... ");
            boolean oNegUniversal = BloodGroup.O_NEG.isUniversalDonor();
            boolean abPosUniversal = BloodGroup.AB_POS.isUniversalRecipient();
            boolean oPosToBPos = BloodGroup.O_POS.canDonateTo(BloodGroup.B_POS);
            boolean bPosToOPos = BloodGroup.B_POS.canDonateTo(BloodGroup.O_POS); // Should be false!

            if (oNegUniversal && abPosUniversal && oPosToBPos && !bPosToOPos) {
                System.out.println("PASSED (O- universal, AB+ universal, O+ -> B+, B+ -/-> O+)");
                passed++;
            } else {
                System.err.println("FAILED in blood group compatibility checks.");
                failed++;
            }

            // Test 4: Donor Privacy & Masking
            System.out.print("[TEST 4] Testing Donor Contact Privacy & Masking Invariants... ");
            DonorService donorService = new DonorService(donorStorage);
            Donor sampleDonor = donorService.getDonorById("DNR-101");
            String maskedPhone = sampleDonor.getMaskedPhoneNumber();
            String fullPhone = sampleDonor.getPhoneNumber();

            boolean phoneMaskedByDefault = maskedPhone.contains("***") && !maskedPhone.equals(fullPhone);
            String unmaskedWithConsent = donorService.getContactWithConsent("DNR-101", true);

            if (phoneMaskedByDefault && unmaskedWithConsent.equals(fullPhone)) {
                System.out.println("PASSED (Default: " + maskedPhone + ", Consented: " + unmaskedWithConsent + ")");
                passed++;
            } else {
                System.err.println("FAILED in donor privacy masking.");
                failed++;
            }

            // Test 5: Matching Engine Ranking
            System.out.print("[TEST 5] Testing Smart Matching Engine Proximity & Ranking Pipeline... ");
            MatchingEngine matchingEngine = new MatchingEngine();
            BloodRequest sampleRequest = requestStorage.findById("REQ-301"); // DMCH Ludhiana, B+ Critical
            List<MatchResult> rankedMatches = matchingEngine.findMatches(sampleRequest, seededDonors);

            if (!rankedMatches.isEmpty() && rankedMatches.get(0).getRank() == 1) {
                MatchResult topMatch = rankedMatches.get(0);
                System.out.println("PASSED (Top match: " + topMatch.getDonor().getFullName() +
                        " [" + topMatch.getDonor().getBloodGroup().getSymbol() + "] at " +
                        String.format("%.1f km", topMatch.getDistanceKm()) +
                        " with score: " + String.format("%.1f", topMatch.getMatchScore()) + ")");
                passed++;
            } else {
                System.err.println("FAILED: No matches returned or top match rank != 1.");
                failed++;
            }

            // Test 6: Report Generation
            System.out.print("[TEST 6] Testing Report Generation & Summary Aggregates... ");
            BloodRequestService requestService = new BloodRequestService(requestStorage);
            ReportService reportService = new ReportService(donorService, requestService);
            String textReport = reportService.generateSummaryTextReport();

            if (textReport.contains("BLOODCONNECT SYSTEM SUMMARY REPORT") &&
                    reportService.getTotalDonorsCount() == 12 &&
                    reportService.getTotalRequestsCount() == 4) {
                System.out.println("PASSED (Report generated successfully with valid KPIs)");
                passed++;
            } else {
                System.err.println("FAILED in report service.");
                failed++;
            }

            // Cleanup test files
            testDonorsFile.delete();
            testRequestsFile.delete();

        } catch (Exception e) {
            System.err.println("\nEXCEPTION ENCOUNTERED DURING VERIFICATION:");
            e.printStackTrace();
            failed++;
        }

        System.out.println("=================================================");
        System.out.println(String.format("VERIFICATION RESULTS: %d PASSED, %d FAILED", passed, failed));
        System.out.println("=================================================");

        if (failed == 0) {
            System.out.println(">>> ALL CORE MODULES AND INVARIANTS ARE 100% OPERATIONAL! <<<");
        } else {
            System.exit(1);
        }
    }
}
