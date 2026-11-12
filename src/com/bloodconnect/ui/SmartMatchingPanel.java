package com.bloodconnect.ui;

import com.bloodconnect.exception.BloodConnectException;
import com.bloodconnect.model.BloodRequest;
import com.bloodconnect.model.Donor;
import com.bloodconnect.model.MatchResult;
import com.bloodconnect.service.BloodRequestService;
import com.bloodconnect.service.DonorService;
import com.bloodconnect.service.MatchingEngine;
import com.bloodconnect.ui.components.ModernButton;
import com.bloodconnect.ui.components.ModernCard;
import com.bloodconnect.ui.components.ModernTable;
import com.bloodconnect.ui.components.StatusBadgeRenderer;
import com.bloodconnect.ui.components.UIConstants;
import com.bloodconnect.util.DistanceCalculator;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * =========================================================================
 * Unit 2 & Unit 3 Concept: Algorithmic Matching, Scoring Pipeline & Interactive GUI
 * Central intelligent matching panel of BloodConnect.
 * Evaluates compatible donors, calculates geodesic Haversine distance,
 * enforces demonstration eligibility, scores proximity, and assigns
 * ranked donors to emergency blood requests.
 *
 * Enforces:
 * - Requirement 9: Distance-based matching and ranking.
 * - Requirement 10: State transition and assignment verification.
 * =========================================================================
 */
public class SmartMatchingPanel extends JPanel {

    private final DonorService donorService;
    private final BloodRequestService requestService;
    private final MatchingEngine matchingEngine;
    private final MainFrame mainFrame;

    // Selector components
    private JComboBox<String> cmbRequests;
    private List<BloodRequest> activeRequestsList;
    private BloodRequest currentlySelectedRequest;

    private JLabel lblRequestDetails;
    private JCheckBox chkEligibleOnly;

    // KPI labels
    private JLabel lblEvaluatedCount;
    private JLabel lblCompatibleCount;
    private JLabel lblEligibleCount;
    private JLabel lblNearestDistance;

    // Table
    private DefaultTableModel resultsTableModel;
    private ModernTable resultsTable;
    private List<MatchResult> currentMatchesList;

    public SmartMatchingPanel(DonorService donorService, BloodRequestService requestService,
                              MatchingEngine matchingEngine, MainFrame mainFrame) {
        this.donorService = donorService;
        this.requestService = requestService;
        this.matchingEngine = matchingEngine;
        this.mainFrame = mainFrame;
        this.activeRequestsList = new ArrayList<>();
        this.currentMatchesList = new ArrayList<>();

        setLayout(new BorderLayout(20, 16));
        setBackground(UIConstants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        initComponents();
        loadActiveRequests();
    }

    private void initComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Smart Donor Matching & Dispatch Engine");
        title.setFont(UIConstants.FONT_PAGE_TITLE);
        title.setForeground(UIConstants.COLOR_TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Multifactor algorithmic donor ranking based on ABO/Rh compatibility, availability, and Haversine distance.");
        subtitle.setFont(UIConstants.FONT_REGULAR);
        subtitle.setForeground(UIConstants.COLOR_TEXT_SECONDARY);

        headerPanel.add(title);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(subtitle);

        // --- Top Selector Card ---
        ModernCard selectorCard = new ModernCard(new BorderLayout(16, 12));
        selectorCard.setAccentColor(UIConstants.COLOR_SUCCESS);

        JPanel topRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        topRow.setOpaque(false);

        JLabel lblSelect = new JLabel("Select Blood Request:");
        lblSelect.setFont(UIConstants.FONT_HEADER);
        lblSelect.setForeground(UIConstants.COLOR_TEXT_PRIMARY);

        cmbRequests = new JComboBox<>();
        cmbRequests.setFont(UIConstants.FONT_REGULAR);
        cmbRequests.setPreferredSize(new Dimension(480, 36));
        cmbRequests.addActionListener(e -> onSelectedRequestChanged());

        chkEligibleOnly = new JCheckBox("Show Eligible Candidates Only", false);
        chkEligibleOnly.setFont(UIConstants.FONT_REGULAR);
        chkEligibleOnly.setOpaque(false);
        chkEligibleOnly.addActionListener(e -> renderMatchResults());

        ModernButton btnRunMatching = new ModernButton("Run Smart Matching", ModernButton.Style.SUCCESS);
        btnRunMatching.setPreferredSize(new Dimension(190, 36));
        btnRunMatching.addActionListener(e -> runMatchingAlgorithm());

        topRow.add(lblSelect);
        topRow.add(cmbRequests);
        topRow.add(chkEligibleOnly);
        topRow.add(btnRunMatching);

        lblRequestDetails = new JLabel("Please select an active blood request above to initiate matching.");
        lblRequestDetails.setFont(UIConstants.FONT_REGULAR);
        lblRequestDetails.setForeground(UIConstants.COLOR_TEXT_SECONDARY);
        lblRequestDetails.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));

        selectorCard.add(topRow, BorderLayout.NORTH);
        selectorCard.add(lblRequestDetails, BorderLayout.CENTER);

        // --- KPI Metrics Row ---
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setPreferredSize(new Dimension(0, 95));

        lblEvaluatedCount = new JLabel("0");
        lblCompatibleCount = new JLabel("0");
        lblEligibleCount = new JLabel("0");
        lblNearestDistance = new JLabel("-");

        kpiGrid.add(createMiniKpiCard("Evaluated Donors", lblEvaluatedCount, UIConstants.COLOR_SIDEBAR_ACTIVE));
        kpiGrid.add(createMiniKpiCard("Compatible Blood Types", lblCompatibleCount, UIConstants.COLOR_INFO));
        kpiGrid.add(createMiniKpiCard("Eligible Candidates", lblEligibleCount, UIConstants.COLOR_SUCCESS));
        kpiGrid.add(createMiniKpiCard("Nearest Distance", lblNearestDistance, UIConstants.COLOR_PRIMARY));

        // --- Center Results Table ---
        String[] columns = {
                "Rank", "Donor ID", "Donor Name", "Blood Type", "Compatibility",
                "Distance (km)", "Est. Transit", "Demo Status", "Match Score", "Diagnostic Note"
        };

        resultsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        resultsTable = new ModernTable(resultsTableModel);
        resultsTable.getColumnModel().getColumn(3).setCellRenderer(new StatusBadgeRenderer()); // Blood Type
        resultsTable.getColumnModel().getColumn(4).setCellRenderer(new StatusBadgeRenderer()); // Compatibility
        resultsTable.getColumnModel().getColumn(7).setCellRenderer(new StatusBadgeRenderer()); // Demo Status

        JScrollPane scrollPane = new JScrollPane(resultsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER));

        // --- Bottom Actions Bar ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        actionPanel.setOpaque(false);

        ModernButton btnDistanceDetails = new ModernButton("Haversine Geodesic Details", ModernButton.Style.OUTLINE);
        btnDistanceDetails.setPreferredSize(new Dimension(220, 38));
        btnDistanceDetails.addActionListener(e -> handleShowDistanceDetails());

        ModernButton btnRevealContact = new ModernButton("Reveal Contact (Consent)", ModernButton.Style.SECONDARY);
        btnRevealContact.setPreferredSize(new Dimension(200, 38));
        btnRevealContact.addActionListener(e -> handleRevealSelectedDonorContact());

        ModernButton btnAssign = new ModernButton("Assign Donor to Request", ModernButton.Style.PRIMARY);
        btnAssign.setPreferredSize(new Dimension(210, 38));
        btnAssign.addActionListener(e -> handleAssignDonor());

        actionPanel.add(btnDistanceDetails);
        actionPanel.add(btnRevealContact);
        actionPanel.add(btnAssign);

        // Assemble Panels
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.add(headerPanel);
        topContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        topContainer.add(selectorCard);
        topContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        topContainer.add(kpiGrid);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private ModernCard createMiniKpiCard(String label, JLabel valueLabel, Color accentColor) {
        ModernCard card = new ModernCard(new BorderLayout(), accentColor);
        card.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));

        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(UIConstants.FONT_HEADER);
        lblTitle.setForeground(UIConstants.COLOR_TEXT_SECONDARY);

        valueLabel.setFont(UIConstants.FONT_PAGE_TITLE);
        valueLabel.setForeground(UIConstants.COLOR_TEXT_PRIMARY);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        return card;
    }

    public void loadActiveRequests() {
        try {
            activeRequestsList = requestService.getActiveRequests();
            cmbRequests.removeAllItems();

            if (activeRequestsList.isEmpty()) {
                cmbRequests.addItem("No active requests available (All fulfilled or cancelled)");
                lblRequestDetails.setText("No active requests found. Create a new request in the Blood Requests tab.");
                currentlySelectedRequest = null;
                resultsTableModel.setRowCount(0);
                return;
            }

            for (BloodRequest req : activeRequestsList) {
                String patient = req.getRecipient() != null ? req.getRecipient().getFullName() : "Unknown";
                String hosp = req.getRecipient() != null ? req.getRecipient().getHospitalName() : "Unknown";
                String item = String.format("%s: %s (%s) @ %s [%s]",
                        req.getRequestId(),
                        patient,
                        req.getRequiredBloodGroup().getSymbol(),
                        hosp,
                        req.getUrgencyLevel().getDisplayName());
                cmbRequests.addItem(item);
            }

            cmbRequests.setSelectedIndex(0);
            onSelectedRequestChanged();

        } catch (BloodConnectException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load active requests: " + e.getMessage(),
                    "Storage Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setSelectedRequest(String requestId) {
        loadActiveRequests();
        if (requestId != null && activeRequestsList != null) {
            for (int i = 0; i < activeRequestsList.size(); i++) {
                if (activeRequestsList.get(i).getRequestId().equalsIgnoreCase(requestId)) {
                    cmbRequests.setSelectedIndex(i);
                    runMatchingAlgorithm();
                    return;
                }
            }
        }
    }

    private void onSelectedRequestChanged() {
        int idx = cmbRequests.getSelectedIndex();
        if (idx >= 0 && idx < activeRequestsList.size()) {
            currentlySelectedRequest = activeRequestsList.get(idx);
            String patient = currentlySelectedRequest.getRecipient() != null ? currentlySelectedRequest.getRecipient().getFullName() : "-";
            String hosp = currentlySelectedRequest.getRecipient() != null ? currentlySelectedRequest.getRecipient().getHospitalName() : "-";
            String city = currentlySelectedRequest.getLocation() != null ? currentlySelectedRequest.getLocation().toString() : "-";

            lblRequestDetails.setText(String.format(
                    "<html><b>Target Recipient:</b> %s &nbsp;|&nbsp; <b>Facility:</b> %s &nbsp;|&nbsp; <b>Blood Group:</b> <font color='#D32F2F'><b>%s</b></font> &nbsp;|&nbsp; <b>Units:</b> %d/%d &nbsp;|&nbsp; <b>Urgency:</b> %s &nbsp;|&nbsp; <b>Location:</b> %s</html>",
                    patient, hosp,
                    currentlySelectedRequest.getRequiredBloodGroup().getSymbol(),
                    currentlySelectedRequest.getUnitsFulfilled(),
                    currentlySelectedRequest.getUnitsRequired(),
                    currentlySelectedRequest.getUrgencyLevel().getDisplayName(),
                    city
            ));
        } else {
            currentlySelectedRequest = null;
        }
    }

    public void runMatchingAlgorithm() {
        if (currentlySelectedRequest == null) {
            JOptionPane.showMessageDialog(this,
                    "Please select an active blood request first.",
                    "No Request",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Donor> allDonors = donorService.getAllDonors();
            currentMatchesList = matchingEngine.findMatches(currentlySelectedRequest, allDonors);

            // Compute KPIs
            int total = allDonors.size();
            int compatible = 0;
            int eligible = 0;
            double nearest = Double.MAX_VALUE;

            for (MatchResult res : currentMatchesList) {
                if (res.getDonor().getBloodGroup().canDonateTo(currentlySelectedRequest.getRequiredBloodGroup())) {
                    compatible++;
                }
                if (res.isEligible()) {
                    eligible++;
                    if (res.getDistanceKm() < nearest) {
                        nearest = res.getDistanceKm();
                    }
                }
            }

            lblEvaluatedCount.setText(String.valueOf(total));
            lblCompatibleCount.setText(String.valueOf(compatible));
            lblEligibleCount.setText(String.valueOf(eligible));
            lblNearestDistance.setText(nearest == Double.MAX_VALUE ? "N/A" : DistanceCalculator.formatDistance(nearest));

            renderMatchResults();

        } catch (BloodConnectException e) {
            JOptionPane.showMessageDialog(this,
                    "Matching engine failed: " + e.getMessage(),
                    "Execution Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void renderMatchResults() {
        resultsTableModel.setRowCount(0);
        if (currentMatchesList == null || currentMatchesList.isEmpty()) {
            return;
        }

        boolean eligibleOnly = chkEligibleOnly.isSelected();
        List<MatchResult> displayList = eligibleOnly
                ? matchingEngine.getEligibleMatches(currentMatchesList)
                : currentMatchesList;

        for (MatchResult mr : displayList) {
            Donor d = mr.getDonor();
            String distStr = DistanceCalculator.formatDistance(mr.getDistanceKm());
            String transitStr = (mr.getDistanceKm() < 1000.0)
                    ? (DistanceCalculator.estimateTransitMinutes(mr.getDistanceKm(), DistanceCalculator.DEFAULT_URBAN_SPEED_KMH) + " mins")
                    : "N/A";

            resultsTableModel.addRow(new Object[]{
                    "#" + mr.getRank(),
                    d.getPersonId(),
                    d.getFullName(),
                    d.getBloodGroup(),
                    mr.getCompatibilityType(),
                    distStr,
                    transitStr,
                    mr.isEligible() ? "Eligible" : "Ineligible",
                    String.format("%.1f pts", mr.getMatchScore()),
                    mr.getStatusNote()
            });
        }
    }

    private void handleAssignDonor() {
        int row = resultsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a donor from the ranked matches table to assign.",
                    "No Donor Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentlySelectedRequest == null) {
            return;
        }

        String donorId = (String) resultsTableModel.getValueAt(row, 1);
        String donorName = (String) resultsTableModel.getValueAt(row, 2);
        String status = (String) resultsTableModel.getValueAt(row, 7);

        if (!"Eligible".equalsIgnoreCase(status)) {
            int proceed = JOptionPane.showConfirmDialog(this,
                    "WARNING: Candidate " + donorName + " (" + donorId + ") is flagged as INELIGIBLE.\n" +
                            "Reason: " + resultsTableModel.getValueAt(row, 9) + "\n\n" +
                            "Do you still wish to proceed with emergency assignment?",
                    "Ineligible Donor Warning",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);

            if (proceed != JOptionPane.YES_OPTION) {
                return;
            }
        }

        try {
            requestService.assignDonorToRequest(currentlySelectedRequest.getRequestId(), donorId);

            JOptionPane.showMessageDialog(this,
                    "SUCCESSFULLY ASSIGNED DONOR!\n\n" +
                            "Donor: " + donorName + " (" + donorId + ")\n" +
                            "Assigned to Request: " + currentlySelectedRequest.getRequestId() + "\n" +
                            "Patient: " + currentlySelectedRequest.getRecipient().getFullName() + "\n" +
                            "Hospital: " + currentlySelectedRequest.getRecipient().getHospitalName() + "\n\n" +
                            "Request status transitioned to: MATCHED.",
                    "Donor Assignment Confirmed",
                    JOptionPane.INFORMATION_MESSAGE);

            mainFrame.refreshAllViews();
            loadActiveRequests();
            runMatchingAlgorithm();

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to assign donor: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRevealSelectedDonorContact() {
        int row = resultsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a donor from the table to view contact information.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String donorId = (String) resultsTableModel.getValueAt(row, 1);
        try {
            Donor donor = donorService.getDonorById(donorId);

            int choice = JOptionPane.showConfirmDialog(this,
                    "DONOR PRIVACY & CONFIDENTIALITY POLICY:\n\n" +
                            "Contact numbers are masked by default to protect volunteer privacy.\n" +
                            "Do you have emergency response authorization to access unmasked contact info for:\n" +
                            "Donor: " + donor.getFullName() + " (" + donor.getPersonId() + ")?",
                    "Coordinator Consent Authorization",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION) {
                if (donor.isEmergencyConsentGranted()) {
                    String contact = donorService.getContactWithConsent(donorId, true);
                    JOptionPane.showMessageDialog(this,
                            "EMERGENCY CONTACT DETAILS:\n\n" +
                                    "Donor Name  : " + donor.getFullName() + "\n" +
                                    "Phone Number: " + contact + "\n" +
                                    "Email       : " + donor.getEmail() + "\n" +
                                    "Location    : " + donor.getLocation() + "\n\n" +
                                    "Status: Verified emergency consent granted.",
                            "Contact Information",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "ACCESS RESTRICTED BY DONOR PRIVACY PREFERENCE:\n\n" +
                                    "Donor " + donor.getFullName() + " has NOT granted consent for direct unmasking.\n\n" +
                                    "Masked Contact: " + donor.getMaskedPhoneNumber() + "\n" +
                                    "Please coordinate outreach through registered regional coordinator.",
                            "Privacy Protected",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleShowDistanceDetails() {
        int row = resultsTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a ranked donor row to inspect geodesic calculation details.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentlySelectedRequest == null) return;

        String donorId = (String) resultsTableModel.getValueAt(row, 1);
        try {
            Donor donor = donorService.getDonorById(donorId);
            double dist = DistanceCalculator.calculateDistance(donor.getLocation(), currentlySelectedRequest.getLocation());
            int transit = DistanceCalculator.estimateTransitMinutes(dist, DistanceCalculator.DEFAULT_URBAN_SPEED_KMH);

            String details = String.format(
                    "HAVERSINE GEODESIC PROXIMITY AUDIT:\n\n" +
                            "Target Destination  : %s\n" +
                            "Hospital Location   : %s (Lat: %.4f, Lon: %.4f)\n\n" +
                            "Donor Origin        : %s\n" +
                            "Donor Location      : %s (Lat: %.4f, Lon: %.4f)\n\n" +
                            "Mathematical Metric : Haversine Great-Circle Surface Distance\n" +
                            "Calculated Distance : %.2f km (%s)\n" +
                            "Estimated Transit   : ~%d minutes (at 35 km/h urban emergency dispatch)\n" +
                            "Mean Earth Radius   : %.1f km (WGS-84 Sphere)\n\n" +
                            "Scoring Impact: Inverse-linear proximity bonus applied to final match score.",
                    currentlySelectedRequest.getRecipient().getHospitalName(),
                    currentlySelectedRequest.getLocation().toString(),
                    currentlySelectedRequest.getLocation().getLatitude(),
                    currentlySelectedRequest.getLocation().getLongitude(),
                    donor.getFullName(),
                    donor.getLocation().toString(),
                    donor.getLocation().getLatitude(),
                    donor.getLocation().getLongitude(),
                    dist,
                    DistanceCalculator.formatDistance(dist),
                    transit,
                    DistanceCalculator.EARTH_RADIUS_KM
            );

            JOptionPane.showMessageDialog(this, details, "Geodesic Distance Audit", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Calculation inspection error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}

