package com.bloodconnect.storage;

import com.bloodconnect.exception.StorageException;
import com.bloodconnect.model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * =========================================================================
 * Unit 2 & Unit 3 Concept: Collections Management, Data Seeding & File Persistence
 * Automatically pre-populates realistic, fictional sample data for donors,
 * hospitals, and emergency requests if persistent storage files are empty.
 *
 * Implements:
 * - Requirement 6: Fictional sample donor information.
 * - Requirement 7: Configurable demonstration eligibility variety (eligible, active cooldown, first-time).
 * - Requirement 8: Masked privacy protection with explicit consent states.
 * =========================================================================
 */
public final class DataSeeder {

    private DataSeeder() {
        // Prevent instantiation
    }

    /**
     * Seeds initial demo data if the underlying storage files are empty.
     *
     * @param donorStorage   donor persistent repository
     * @param requestStorage blood request persistent repository
     * @throws StorageException on file I/O or serialization error
     */
    public static void seedIfEmpty(Storage<Donor, String> donorStorage,
                                   Storage<BloodRequest, String> requestStorage) throws StorageException {
        if (donorStorage != null && !donorStorage.exists()) {
            donorStorage.saveAll(createSampleDonors());
        }
        if (requestStorage != null && !requestStorage.exists()) {
            requestStorage.saveAll(createSampleRequests());
        }
    }

    /**
     * Resets storage with fresh default demonstration data.
     * Useful for live viva demonstrations and automated UI testing.
     */
    public static void resetToSampleData(Storage<Donor, String> donorStorage,
                                         Storage<BloodRequest, String> requestStorage) throws StorageException {
        if (donorStorage != null) {
            donorStorage.saveAll(createSampleDonors());
        }
        if (requestStorage != null) {
            requestStorage.saveAll(createSampleRequests());
        }
    }

    /**
     * Generates 12 diverse, realistic fictional donors covering all 8 blood groups,
     * varied geographical zones, and different demonstration eligibility profiles.
     *
     * @return List of fictional demonstration donors
     */
    public static List<Donor> createSampleDonors() {
        List<Donor> donors = new ArrayList<>();

        // 1. Ludhiana - O- (Universal Donor, Eligible, 120 days cooldown)
        donors.add(new Donor(
                "DNR-101", "Aarav Sharma", "+91 98140 12345", "aarav.sharma@example.com",
                new Location("Ludhiana", "Civil Lines", 30.9010, 75.8573),
                BloodGroup.O_NEG, 28, 68.5, LocalDate.now().minusDays(120), true, 4, true
        ));

        // 2. Ludhiana - B+ (Exact Match Candidate, Eligible, First-time donor)
        donors.add(new Donor(
                "DNR-102", "Simran Kaur", "+91 98720 54321", "simran.kaur@example.com",
                new Location("Ludhiana", "Model Town", 30.8872, 75.8361),
                BloodGroup.B_POS, 23, 56.0, null, true, 0, true
        ));

        // 3. Ludhiana - A+ (Eligible, 100 days cooldown)
        donors.add(new Donor(
                "DNR-103", "Rohan Verma", "+91 94170 98765", "rohan.v@example.com",
                new Location("Ludhiana", "BRS Nagar", 30.8755, 75.8115),
                BloodGroup.A_POS, 32, 74.0, LocalDate.now().minusDays(100), true, 6, true
        ));

        // 4. Ludhiana - B+ (Ineligible by Cooldown: 45 days remaining of 90-day rule)
        donors.add(new Donor(
                "DNR-104", "Manpreet Singh", "+91 98888 11223", "manpreet.s@example.com",
                new Location("Ludhiana", "Sarabha Nagar", 30.8920, 75.8150),
                BloodGroup.B_POS, 29, 81.0, LocalDate.now().minusDays(45), true, 3, false
        ));

        // 5. Jalandhar - AB+ (Universal Recipient Type, Eligible)
        donors.add(new Donor(
                "DNR-105", "Pooja Malhotra", "+91 98150 33445", "pooja.m@example.com",
                new Location("Jalandhar", "Model Town", 31.3260, 75.5762),
                BloodGroup.AB_POS, 26, 54.5, LocalDate.now().minusDays(180), true, 2, true
        ));

        // 6. Jalandhar - O+ (Eligible)
        donors.add(new Donor(
                "DNR-106", "Gurpreet Dhillon", "+91 99140 77889", "gurpreet.d@example.com",
                new Location("Jalandhar", "Cantt Road", 31.2850, 75.6120),
                BloodGroup.O_POS, 35, 76.0, LocalDate.now().minusDays(95), true, 5, true
        ));

        // 7. Phagwara - A- (Eligible, Rh- candidate)
        donors.add(new Donor(
                "DNR-107", "Karan Kapoor", "+91 98765 44332", "karan.k@example.com",
                new Location("Phagwara", "GT Road Zone", 31.2240, 75.7708),
                BloodGroup.A_NEG, 24, 62.0, LocalDate.now().minusDays(140), true, 1, true
        ));

        // 8. Amritsar - B- (Eligible Rh- donor)
        donors.add(new Donor(
                "DNR-108", "Harpreet Gill", "+91 98141 55667", "harpreet.gill@example.com",
                new Location("Amritsar", "Ranjit Avenue", 31.6340, 74.8723),
                BloodGroup.B_NEG, 31, 70.0, LocalDate.now().minusDays(110), true, 4, true
        ));

        // 9. Chandigarh - O- (Universal Donor, Eligible)
        donors.add(new Donor(
                "DNR-109", "Neha Gupta", "+91 98780 22334", "neha.gupta@example.com",
                new Location("Chandigarh", "Sector 17", 30.7333, 76.7794),
                BloodGroup.O_NEG, 27, 58.0, LocalDate.now().minusDays(210), true, 3, true
        ));

        // 10. Patiala - AB- (Rare Blood Group, Eligible)
        donors.add(new Donor(
                "DNR-110", "Jaswinder Sandhu", "+91 98880 66778", "jaswinder.s@example.com",
                new Location("Patiala", "Urban Estate", 30.3398, 76.3869),
                BloodGroup.AB_NEG, 40, 85.0, LocalDate.now().minusDays(92), true, 7, true
        ));

        // 11. Ludhiana - O+ (Temporarily Flagged as Unavailable)
        donors.add(new Donor(
                "DNR-111", "Vikram Rathore", "+91 98155 99001", "vikram.r@example.com",
                new Location("Ludhiana", "Dugri Phase 1", 30.8650, 75.8450),
                BloodGroup.O_POS, 22, 60.0, LocalDate.now().minusDays(150), false, 2, false
        ));

        // 12. Ludhiana - A+ (Underweight Demonstration Profile: 46kg < 50kg)
        donors.add(new Donor(
                "DNR-112", "Ananya Sen", "+91 98722 33441", "ananya.sen@example.com",
                new Location("Ludhiana", "Pakhowal Road", 30.8520, 75.8200),
                BloodGroup.A_POS, 20, 46.0, null, true, 0, false
        ));

        return donors;
    }

    /**
     * Generates sample emergency and routine blood requests for simulation.
     *
     * @return List of initial BloodRequests
     */
    public static List<BloodRequest> createSampleRequests() {
        List<BloodRequest> requests = new ArrayList<>();

        // Request 1: Critical Emergency (B+ at Dayanand Medical College Hospital, Ludhiana)
        Location hospitalDmc = new Location("Ludhiana", "Civil Lines (DMCH)", 30.9020, 75.8390);
        Recipient rec1 = new Recipient(
                "REC-201", "Rajesh Khanna", "+91 98144 00112", "rajesh.contact@example.com",
                new Location("Ludhiana", "Civil Lines", 30.9010, 75.8573),
                BloodGroup.B_POS, 2, UrgencyLevel.CRITICAL, "Dayanand Medical College & Hospital (DMCH)",
                LocalDateTime.now().minusHours(2), hospitalDmc
        );
        BloodRequest req1 = new BloodRequest(
                "REQ-301", rec1, BloodGroup.B_POS, 2, 0,
                UrgencyLevel.CRITICAL, RequestStatus.PENDING, hospitalDmc,
                LocalDateTime.now().minusHours(2), new ArrayList<>()
        );
        requests.add(req1);

        // Request 2: Urgent Surgery Requirement (O- Universal needed at Christian Medical College, Ludhiana)
        Location hospitalCmc = new Location("Ludhiana", "Brown Road (CMCH)", 30.9125, 75.8610);
        Recipient rec2 = new Recipient(
                "REC-202", "Baldev Raj", "+91 98721 99887", "baldev.family@example.com",
                new Location("Ludhiana", "Field Ganj", 30.9050, 75.8550),
                BloodGroup.O_NEG, 1, UrgencyLevel.URGENT, "Christian Medical College & Hospital (CMCH)",
                LocalDateTime.now().minusHours(5), hospitalCmc
        );
        BloodRequest req2 = new BloodRequest(
                "REQ-302", rec2, BloodGroup.O_NEG, 1, 0,
                UrgencyLevel.URGENT, RequestStatus.PENDING, hospitalCmc,
                LocalDateTime.now().minusHours(5), new ArrayList<>()
        );
        requests.add(req2);

        // Request 3: Matched Trauma Case (A+ at SPS Hospitals, Ludhiana)
        Location hospitalSps = new Location("Ludhiana", "Sherpur Chowk (SPS)", 30.8900, 75.8850);
        Recipient rec3 = new Recipient(
                "REC-203", "Kavita Sethi", "+91 94177 33221", "kavita.attendant@example.com",
                new Location("Ludhiana", "Model Town Ext", 30.8800, 75.8300),
                BloodGroup.A_POS, 1, UrgencyLevel.URGENT, "SPS Hospitals",
                LocalDateTime.now().minusDays(1), hospitalSps
        );
        List<String> assignedDonorsReq3 = new ArrayList<>();
        assignedDonorsReq3.add("DNR-103");
        BloodRequest req3 = new BloodRequest(
                "REQ-303", rec3, BloodGroup.A_POS, 1, 0,
                UrgencyLevel.URGENT, RequestStatus.MATCHED, hospitalSps,
                LocalDateTime.now().minusDays(1), assignedDonorsReq3
        );
        requests.add(req3);

        // Request 4: Fulfilled Routine Requirement (O+ at Fortis Hospital, Ludhiana)
        Location hospitalFortis = new Location("Ludhiana", "Chandigarh Road (Fortis)", 30.9150, 75.9200);
        Recipient rec4 = new Recipient(
                "REC-204", "Gurmit Lal", "+91 99144 11229", "gurmit.care@example.com",
                new Location("Ludhiana", "Sector 32", 30.9080, 75.9100),
                BloodGroup.O_POS, 2, UrgencyLevel.ROUTINE, "Fortis Hospital Ludhiana",
                LocalDateTime.now().minusDays(3), hospitalFortis
        );
        List<String> assignedDonorsReq4 = new ArrayList<>();
        assignedDonorsReq4.add("DNR-106");
        BloodRequest req4 = new BloodRequest(
                "REQ-304", rec4, BloodGroup.O_POS, 2, 2,
                UrgencyLevel.ROUTINE, RequestStatus.FULFILLED, hospitalFortis,
                LocalDateTime.now().minusDays(3), assignedDonorsReq4
        );
        requests.add(req4);

        return requests;
    }
}

