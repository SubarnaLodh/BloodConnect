package com.bloodconnect.service;

import com.bloodconnect.exception.StorageException;
import com.bloodconnect.model.BloodGroup;
import com.bloodconnect.model.BloodRequest;
import com.bloodconnect.model.Donor;
import com.bloodconnect.model.RequestStatus;
import com.bloodconnect.model.UrgencyLevel;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * =========================================================================
 * Unit 2 & Unit 3 Concept: Business Logic, Collections (Maps & Streams), Reporting
 * Generates analytics, aggregated statistics, and summary reports for the
 * BloodConnect dashboard and exportable documentation.
 *
 * Implements:
 * - Requirement 6: Reporting and analytics.
 * - Educational transparency: Explicitly details demonstration constraints.
 * =========================================================================
 */
public class ReportService {

    private final DonorService donorService;
    private final BloodRequestService requestService;

    /**
     * Constructs a ReportService with required domain services.
     *
     * @param donorService   donor service provider
     * @param requestService request service provider
     */
    public ReportService(DonorService donorService, BloodRequestService requestService) {
        if (donorService == null || requestService == null) {
            throw new IllegalArgumentException("Services cannot be null.");
        }
        this.donorService = donorService;
        this.requestService = requestService;
    }

    /**
     * Total number of registered donors in the system.
     */
    public int getTotalDonorsCount() throws StorageException {
        return donorService.getAllDonors().size();
    }

    /**
     * Total number of donors currently flagged as available.
     */
    public int getAvailableDonorsCount() throws StorageException {
        int count = 0;
        for (Donor d : donorService.getAllDonors()) {
            if (d.isAvailable()) count++;
        }
        return count;
    }

    /**
     * Total number of donors currently satisfying demonstration eligibility rules
     * (age, weight, availability, and cooldown).
     */
    public int getEligibleDonorsCount() throws StorageException {
        int count = 0;
        for (Donor d : donorService.getAllDonors()) {
            if (d.isEligibleForDonation()) count++;
        }
        return count;
    }

    /**
     * Total number of blood requests ever created.
     */
    public int getTotalRequestsCount() throws StorageException {
        return requestService.getAllRequests().size();
    }

    /**
     * Total number of active requests (Pending or Matched).
     */
    public int getActiveRequestsCount() throws StorageException {
        return requestService.getActiveRequests().size();
    }

    /**
     * Total number of fulfilled requests.
     */
    public int getFulfilledRequestsCount() throws StorageException {
        int count = 0;
        for (BloodRequest req : requestService.getAllRequests()) {
            if (req.getStatus() == RequestStatus.FULFILLED) count++;
        }
        return count;
    }

    /**
     * Total number of active emergency requests (Critical or Urgent).
     */
    public int getEmergencyRequestsCount() throws StorageException {
        return requestService.getEmergencyRequests().size();
    }

    /**
     * Total blood units required across all active and fulfilled requests.
     */
    public int getTotalUnitsRequired() throws StorageException {
        int sum = 0;
        for (BloodRequest req : requestService.getAllRequests()) {
            sum += req.getUnitsRequired();
        }
        return sum;
    }

    /**
     * Total blood units fulfilled across all requests.
     */
    public int getTotalUnitsFulfilled() throws StorageException {
        int sum = 0;
        for (BloodRequest req : requestService.getAllRequests()) {
            sum += req.getUnitsFulfilled();
        }
        return sum;
    }

    /**
     * Aggregates total donor count grouped by ABO/Rh blood group.
     */
    public Map<BloodGroup, Integer> getDonorCountByBloodGroup() throws StorageException {
        Map<BloodGroup, Integer> map = new EnumMap<>(BloodGroup.class);
        for (BloodGroup bg : BloodGroup.values()) {
            map.put(bg, 0);
        }
        for (Donor d : donorService.getAllDonors()) {
            if (d.getBloodGroup() != null) {
                map.put(d.getBloodGroup(), map.get(d.getBloodGroup()) + 1);
            }
        }
        return map;
    }

    /**
     * Aggregates available donor count grouped by blood group.
     */
    public Map<BloodGroup, Integer> getAvailableDonorCountByBloodGroup() throws StorageException {
        Map<BloodGroup, Integer> map = new EnumMap<>(BloodGroup.class);
        for (BloodGroup bg : BloodGroup.values()) {
            map.put(bg, 0);
        }
        for (Donor d : donorService.getAllDonors()) {
            if (d.isAvailable() && d.getBloodGroup() != null) {
                map.put(d.getBloodGroup(), map.get(d.getBloodGroup()) + 1);
            }
        }
        return map;
    }

    /**
     * Aggregates blood requests count grouped by request status.
     */
    public Map<RequestStatus, Integer> getRequestCountByStatus() throws StorageException {
        Map<RequestStatus, Integer> map = new EnumMap<>(RequestStatus.class);
        for (RequestStatus s : RequestStatus.values()) {
            map.put(s, 0);
        }
        for (BloodRequest r : requestService.getAllRequests()) {
            if (r.getStatus() != null) {
                map.put(r.getStatus(), map.get(r.getStatus()) + 1);
            }
        }
        return map;
    }

    /**
     * Aggregates blood requests count grouped by urgency level.
     */
    public Map<UrgencyLevel, Integer> getRequestCountByUrgency() throws StorageException {
        Map<UrgencyLevel, Integer> map = new EnumMap<>(UrgencyLevel.class);
        for (UrgencyLevel u : UrgencyLevel.values()) {
            map.put(u, 0);
        }
        for (BloodRequest r : requestService.getAllRequests()) {
            if (r.getUrgencyLevel() != null) {
                map.put(r.getUrgencyLevel(), map.get(r.getUrgencyLevel()) + 1);
            }
        }
        return map;
    }

    /**
     * Generates a comprehensive, formatted plain text report suitable for display
     * in the Swing UI or file export.
     */
    public String generateSummaryTextReport() throws StorageException {
        StringBuilder sb = new StringBuilder();
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm:ss");

        sb.append("======================================================================\n");
        sb.append("                   BLOODCONNECT SYSTEM SUMMARY REPORT                 \n");
        sb.append("         Smart Blood Donor Matching and Emergency Response System     \n");
        sb.append("======================================================================\n");
        sb.append("Generated On        : ").append(LocalDateTime.now().format(dtf)).append("\n");
        sb.append("Operational Region  : Punjab (Ludhiana, Jalandhar, Amritsar, Chandigarh)\n");
        sb.append("----------------------------------------------------------------------\n\n");

        sb.append("[1] KEY PERFORMANCE INDICATORS (KPIs)\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append(String.format("  Total Registered Donors    : %d\n", getTotalDonorsCount()));
        sb.append(String.format("  Currently Available Donors : %d\n", getAvailableDonorsCount()));
        sb.append(String.format("  Fully Eligible Donors      : %d (satisfying demo rules)\n", getEligibleDonorsCount()));
        sb.append(String.format("  Total Blood Requests       : %d\n", getTotalRequestsCount()));
        sb.append(String.format("  Active Requests (Open)     : %d\n", getActiveRequestsCount()));
        sb.append(String.format("  Emergency SOS Requests     : %d\n", getEmergencyRequestsCount()));
        sb.append(String.format("  Fulfilled Requests         : %d\n", getFulfilledRequestsCount()));
        sb.append(String.format("  Blood Units Required       : %d Units\n", getTotalUnitsRequired()));
        sb.append(String.format("  Blood Units Fulfilled      : %d Units (%.1f%% fulfillment)\n\n",
                getTotalUnitsFulfilled(),
                getTotalUnitsRequired() == 0 ? 0.0 : ((double) getTotalUnitsFulfilled() / getTotalUnitsRequired()) * 100.0));

        sb.append("[2] DONOR ROSTER BY BLOOD GROUP\n");
        sb.append("----------------------------------------------------------------------\n");
        sb.append(String.format("  %-10s | %-12s | %-15s | %-15s\n", "Blood Type", "Total Donors", "Available", "Universal Status"));
        sb.append("  -----------+--------------+-----------------+-----------------\n");
        Map<BloodGroup, Integer> totalByGroup = getDonorCountByBloodGroup();
        Map<BloodGroup, Integer> availByGroup = getAvailableDonorCountByBloodGroup();
        for (BloodGroup bg : BloodGroup.values()) {
            String note = bg.isUniversalDonor() ? "Universal Donor" :
                    (bg.isUniversalRecipient() ? "Universal Recip." : "-");
            sb.append(String.format("  %-10s | %-12d | %-15d | %-15s\n",
                    bg.getSymbol(),
                    totalByGroup.getOrDefault(bg, 0),
                    availByGroup.getOrDefault(bg, 0),
                    note));
        }
        sb.append("\n");

        sb.append("[3] REQUEST LIFECYCLE BREAKDOWN\n");
        sb.append("----------------------------------------------------------------------\n");
        Map<RequestStatus, Integer> statusMap = getRequestCountByStatus();
        for (RequestStatus s : RequestStatus.values()) {
            sb.append(String.format("  %-15s : %d\n", s.getDisplayName(), statusMap.getOrDefault(s, 0)));
        }
        sb.append("\n");

        sb.append("[4] REQUEST URGENCY BREAKDOWN\n");
        sb.append("----------------------------------------------------------------------\n");
        Map<UrgencyLevel, Integer> urgencyMap = getRequestCountByUrgency();
        for (UrgencyLevel u : UrgencyLevel.values()) {
            sb.append(String.format("  %-15s : %d\n", u.getDisplayName(), urgencyMap.getOrDefault(u, 0)));
        }
        sb.append("\n");

        sb.append("======================================================================\n");
        sb.append("EDUCATIONAL & DEMONSTRATION DISCLAIMER:\n");
        sb.append("This software demonstrates Object-Oriented Programming and Collections\n");
        sb.append("in Core Java. Proximity and compatibility scores are algorithmic heuristics\n");
        sb.append("and do NOT constitute definitive clinical advice or laboratory crossmatching.\n");
        sb.append("======================================================================\n");

        return sb.toString();
    }
}

