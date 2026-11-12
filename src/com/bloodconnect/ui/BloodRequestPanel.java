package com.bloodconnect.ui;

import com.bloodconnect.exception.BloodConnectException;
import com.bloodconnect.exception.ValidationException;
import com.bloodconnect.model.BloodGroup;
import com.bloodconnect.model.BloodRequest;
import com.bloodconnect.model.Location;
import com.bloodconnect.model.Recipient;
import com.bloodconnect.model.RequestStatus;
import com.bloodconnect.model.UrgencyLevel;
import com.bloodconnect.service.BloodRequestService;
import com.bloodconnect.ui.components.ModernButton;
import com.bloodconnect.ui.components.ModernCard;
import com.bloodconnect.ui.components.ModernTable;
import com.bloodconnect.ui.components.ModernTextField;
import com.bloodconnect.ui.components.StatusBadgeRenderer;
import com.bloodconnect.ui.components.UIConstants;
import com.bloodconnect.util.ValidationUtil;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * =========================================================================
 * Unit 3 Concept: Swing Table Operations, Request Lifecycle & Form Processing
 * Panel managing patient blood requests, hospital emergency dispatch,
 * status updates, fulfillment tracking, and matching engine triggers.
 *
 * Enforces:
 * - Requirement 2: Emergency vs. Routine request categorization.
 * - Requirement 9: State transition invariants.
 * =========================================================================
 */
public class BloodRequestPanel extends JPanel {

    private final BloodRequestService requestService;
    private final MainFrame mainFrame;

    // Form inputs
    private ModernTextField txtRequestId;
    private ModernTextField txtPatientName;
    private ModernTextField txtHospitalName;
    private JComboBox<String> cmbHospitalPresets;
    private JComboBox<BloodGroup> cmbBloodGroup;
    private ModernTextField txtUnitsRequired;
    private JComboBox<UrgencyLevel> cmbUrgency;
    private ModernTextField txtHospitalCity;
    private ModernTextField txtHospitalZone;
    private ModernTextField txtHospitalLat;
    private ModernTextField txtHospitalLon;

    // Filter controls
    private JComboBox<String> cmbUrgencyFilter;
    private JComboBox<String> cmbStatusFilter;

    // Table
    private DefaultTableModel tableModel;
    private ModernTable requestTable;

    // Presets: [Preset Name, Hospital Name, City, Zone, Lat, Lon]
    private static final String[][] HOSPITAL_PRESETS = {
            {"DMCH - Ludhiana", "Dayanand Medical College & Hospital (DMCH)", "Ludhiana", "Civil Lines", "30.8985", "75.8322"},
            {"CMCH - Ludhiana", "Christian Medical College & Hospital (CMCH)", "Ludhiana", "Brown Road", "30.9125", "75.8601"},
            {"SPS Hospitals - Ludhiana", "SPS Hospitals", "Ludhiana", "Sherpur Chowk", "30.8872", "75.8821"},
            {"Fortis Hospital - Mohali", "Fortis Hospital", "Mohali", "Sector 62", "30.7025", "76.7210"},
            {"Civil Hospital - Jalandhar", "Civil Hospital", "Jalandhar", "Near Railway Station", "31.3210", "75.5830"},
            {"Custom Hospital...", "", "", "", "", ""}
    };

    public BloodRequestPanel(BloodRequestService requestService, MainFrame mainFrame) {
        this.requestService = requestService;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(20, 16));
        setBackground(UIConstants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        initComponents();
        loadRequests();
    }

    private void initComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Blood Requests & Transfusion Management");
        title.setFont(UIConstants.FONT_PAGE_TITLE);
        title.setForeground(UIConstants.COLOR_TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Initiate patient blood requirements, track fulfillment progress, and match available donors.");
        subtitle.setFont(UIConstants.FONT_REGULAR);
        subtitle.setForeground(UIConstants.COLOR_TEXT_SECONDARY);

        headerPanel.add(title);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(subtitle);

        // --- Top: Request Creation Card ---
        ModernCard createCard = new ModernCard(new GridBagLayout());
        createCard.setAccentColor(UIConstants.COLOR_PRIMARY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 10, 6, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        txtRequestId = new ModernTextField();
        generateNextRequestId();

        txtPatientName = new ModernTextField("e.g. Jaswinder Kaur");

        String[] presetNames = new String[HOSPITAL_PRESETS.length];
        for (int i = 0; i < HOSPITAL_PRESETS.length; i++) {
            presetNames[i] = HOSPITAL_PRESETS[i][0];
        }
        cmbHospitalPresets = new JComboBox<>(presetNames);
        cmbHospitalPresets.setFont(UIConstants.FONT_REGULAR);
        cmbHospitalPresets.setPreferredSize(new Dimension(200, 36));
        cmbHospitalPresets.addActionListener(e -> applyHospitalPreset());

        txtHospitalName = new ModernTextField();
        cmbBloodGroup = new JComboBox<>(BloodGroup.values());
        cmbBloodGroup.setFont(UIConstants.FONT_REGULAR);
        cmbBloodGroup.setPreferredSize(new Dimension(200, 36));

        txtUnitsRequired = new ModernTextField("2");
        cmbUrgency = new JComboBox<>(UrgencyLevel.values());
        cmbUrgency.setFont(UIConstants.FONT_REGULAR);
        cmbUrgency.setPreferredSize(new Dimension(200, 36));

        txtHospitalCity = new ModernTextField();
        txtHospitalZone = new ModernTextField();
        txtHospitalLat = new ModernTextField();
        txtHospitalLon = new ModernTextField();

        applyHospitalPreset(); // fill default preset

        int row = 0;
        addFormField(createCard, gbc, "Request ID:", txtRequestId, 0, row);
        addFormField(createCard, gbc, "Patient / Recipient Name *:", txtPatientName, 1, row++);

        addFormField(createCard, gbc, "Hospital Preset:", cmbHospitalPresets, 0, row);
        addFormField(createCard, gbc, "Hospital Facility Name *:", txtHospitalName, 1, row++);

        addFormField(createCard, gbc, "Required Blood Group *:", cmbBloodGroup, 0, row);
        addFormField(createCard, gbc, "Units Required (1 - 20) *:", txtUnitsRequired, 1, row++);

        addFormField(createCard, gbc, "Urgency Priority *:", cmbUrgency, 0, row);
        addFormField(createCard, gbc, "Hospital City & Zone *:", txtHospitalCity, 1, row++);

        // Coordinates row
        JPanel coordsPanel = new JPanel(new GridLayout(1, 2, 8, 0));
        coordsPanel.setOpaque(false);
        coordsPanel.add(txtHospitalLat);
        coordsPanel.add(txtHospitalLon);
        addFormField(createCard, gbc, "Hospital GPS (Lat, Long):", coordsPanel, 0, row);

        // Buttons in createCard
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        btnPanel.setOpaque(false);

        ModernButton btnClear = new ModernButton("Clear", ModernButton.Style.OUTLINE);
        btnClear.setPreferredSize(new Dimension(90, 36));
        btnClear.addActionListener(e -> clearCreateForm());

        ModernButton btnCreate = new ModernButton("+ Create Blood Request", ModernButton.Style.PRIMARY);
        btnCreate.setPreferredSize(new Dimension(190, 36));
        btnCreate.addActionListener(e -> handleCreateRequest());

        btnPanel.add(btnClear);
        btnPanel.add(btnCreate);

        gbc.gridx = 1;
        gbc.gridy = row++;
        createCard.add(btnPanel, gbc);

        // --- Center: Requests Table with Filters ---
        JPanel tableContainer = new JPanel(new BorderLayout(0, 10));
        tableContainer.setOpaque(false);

        // Filter Bar
        ModernCard filterCard = new ModernCard(new FlowLayout(FlowLayout.LEFT, 12, 6));

        JLabel lblUrgencyF = new JLabel("Filter Urgency:");
        lblUrgencyF.setFont(UIConstants.FONT_HEADER);
        cmbUrgencyFilter = new JComboBox<>(new String[]{"ALL", "CRITICAL", "URGENT", "ROUTINE"});
        cmbUrgencyFilter.setPreferredSize(new Dimension(110, 32));

        JLabel lblStatusF = new JLabel("Filter Status:");
        lblStatusF.setFont(UIConstants.FONT_HEADER);
        cmbStatusFilter = new JComboBox<>(new String[]{"ALL", "PENDING", "MATCHED", "FULFILLED", "CANCELLED"});
        cmbStatusFilter.setPreferredSize(new Dimension(120, 32));

        ModernButton btnFilter = new ModernButton("Filter", ModernButton.Style.PRIMARY);
        btnFilter.setPreferredSize(new Dimension(80, 32));
        btnFilter.addActionListener(e -> loadRequests());

        ModernButton btnReset = new ModernButton("Reset", ModernButton.Style.OUTLINE);
        btnReset.setPreferredSize(new Dimension(80, 32));
        btnReset.addActionListener(e -> {
            cmbUrgencyFilter.setSelectedIndex(0);
            cmbStatusFilter.setSelectedIndex(0);
            loadRequests();
        });

        filterCard.add(lblUrgencyF);
        filterCard.add(cmbUrgencyFilter);
        filterCard.add(lblStatusF);
        filterCard.add(cmbStatusFilter);
        filterCard.add(btnFilter);
        filterCard.add(btnReset);

        // Table
        String[] columns = {
                "Request ID", "Patient Name", "Hospital Name", "Blood Type",
                "Units Req.", "Units Fulfilled", "Urgency", "Status", "Request Date & Time", "Assigned Donors"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        requestTable = new ModernTable(tableModel);
        requestTable.getColumnModel().getColumn(3).setCellRenderer(new StatusBadgeRenderer()); // Blood Group
        requestTable.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer()); // Urgency
        requestTable.getColumnModel().getColumn(7).setCellRenderer(new StatusBadgeRenderer()); // Status

        JScrollPane scrollPane = new JScrollPane(requestTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER));

        tableContainer.add(filterCard, BorderLayout.NORTH);
        tableContainer.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Actions Bar ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        actionPanel.setOpaque(false);

        ModernButton btnSmartMatch = new ModernButton("Find Matches (Smart Algorithm)", ModernButton.Style.SUCCESS);
        btnSmartMatch.setPreferredSize(new Dimension(240, 38));
        btnSmartMatch.addActionListener(e -> handleLaunchMatchingForSelected());

        ModernButton btnFulfill = new ModernButton("Fulfill Units", ModernButton.Style.PRIMARY);
        btnFulfill.setPreferredSize(new Dimension(130, 38));
        btnFulfill.addActionListener(e -> handleFulfillUnits());

        ModernButton btnCancel = new ModernButton("Cancel Request", ModernButton.Style.DANGER);
        btnCancel.setPreferredSize(new Dimension(140, 38));
        btnCancel.addActionListener(e -> handleCancelRequest());

        actionPanel.add(btnSmartMatch);
        actionPanel.add(btnFulfill);
        actionPanel.add(btnCancel);

        // Assemble Panels
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.add(headerPanel);
        topContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        topContainer.add(createCard);

        add(topContainer, BorderLayout.NORTH);
        add(tableContainer, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, String labelText,
                              javax.swing.JComponent component, int col, int row) {
        JPanel fieldPanel = new JPanel(new BorderLayout(0, 4));
        fieldPanel.setOpaque(false);

        JLabel label = new JLabel(labelText);
        label.setFont(UIConstants.FONT_HEADER);
        label.setForeground(UIConstants.COLOR_TEXT_PRIMARY);

        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(component, BorderLayout.CENTER);

        gbc.gridx = col;
        gbc.gridy = row;
        panel.add(fieldPanel, gbc);
    }

    private void applyHospitalPreset() {
        int idx = cmbHospitalPresets.getSelectedIndex();
        if (idx >= 0 && idx < HOSPITAL_PRESETS.length - 1) {
            txtHospitalName.setText(HOSPITAL_PRESETS[idx][1]);
            txtHospitalCity.setText(HOSPITAL_PRESETS[idx][2]);
            txtHospitalZone.setText(HOSPITAL_PRESETS[idx][3]);
            txtHospitalLat.setText(HOSPITAL_PRESETS[idx][4]);
            txtHospitalLon.setText(HOSPITAL_PRESETS[idx][5]);
        }
    }

    private void generateNextRequestId() {
        try {
            int count = requestService.getAllRequests().size();
            txtRequestId.setText("REQ-" + (301 + count));
        } catch (Exception e) {
            txtRequestId.setText("REQ-" + (System.currentTimeMillis() % 10000));
        }
    }

    public void loadRequests() {
        try {
            String uStr = (String) cmbUrgencyFilter.getSelectedItem();
            UrgencyLevel urgency = ("ALL".equals(uStr)) ? null : UrgencyLevel.fromString(uStr);

            String sStr = (String) cmbStatusFilter.getSelectedItem();
            RequestStatus status = ("ALL".equals(sStr)) ? null : RequestStatus.fromString(sStr);

            List<BloodRequest> list = requestService.filterRequests(null, urgency, status);
            tableModel.setRowCount(0);

            for (BloodRequest req : list) {
                String patientName = req.getRecipient() != null ? req.getRecipient().getFullName() : "-";
                String hospName = req.getRecipient() != null ? req.getRecipient().getHospitalName() : "-";
                String donors = req.getAssignedDonorIds().isEmpty() ? "None" : String.join(", ", req.getAssignedDonorIds());

                tableModel.addRow(new Object[]{
                        req.getRequestId(),
                        patientName,
                        hospName,
                        req.getRequiredBloodGroup(),
                        req.getUnitsRequired(),
                        req.getUnitsFulfilled(),
                        req.getUrgencyLevel(),
                        req.getStatus(),
                        req.getFormattedRequestTime(),
                        donors
                });
            }
        } catch (BloodConnectException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load requests: " + e.getMessage(),
                    "Storage Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCreateRequest() {
        try {
            String reqId = txtRequestId.getText().trim();
            String patientName = txtPatientName.getText().trim();
            String hospitalName = txtHospitalName.getText().trim();
            BloodGroup group = (BloodGroup) cmbBloodGroup.getSelectedItem();

            int units;
            try {
                units = Integer.parseInt(txtUnitsRequired.getText().trim());
            } catch (NumberFormatException e) {
                throw new ValidationException("unitsRequired", "Units required must be an integer (1 to 20).");
            }

            UrgencyLevel urgency = (UrgencyLevel) cmbUrgency.getSelectedItem();

            String city = txtHospitalCity.getText().trim();
            String zone = txtHospitalZone.getText().trim();
            double lat;
            double lon;
            try {
                lat = Double.parseDouble(txtHospitalLat.getText().trim());
                lon = Double.parseDouble(txtHospitalLon.getText().trim());
            } catch (NumberFormatException e) {
                throw new ValidationException("location", "Hospital latitude and longitude must be valid coordinates.");
            }

            Location hospitalLoc = new Location(city, zone, lat, lon);
            LocalDateTime now = LocalDateTime.now();

            // Construct Recipient
            Recipient recipient = new Recipient(
                    "REC-" + reqId,
                    patientName,
                    "+91 9999900000",
                    "patient@hospital.org",
                    hospitalLoc,
                    group,
                    units,
                    urgency,
                    hospitalName,
                    now,
                    hospitalLoc
            );

            // Construct BloodRequest
            BloodRequest request = new BloodRequest(
                    reqId,
                    recipient,
                    group,
                    units,
                    0, // initially 0 units fulfilled
                    urgency,
                    RequestStatus.PENDING,
                    hospitalLoc,
                    now,
                    new ArrayList<>()
            );

            requestService.createRequest(request);

            JOptionPane.showMessageDialog(this,
                    "Blood Request " + reqId + " created successfully for " + patientName + " at " + hospitalName + ".",
                    "Request Created",
                    JOptionPane.INFORMATION_MESSAGE);

            clearCreateForm();
            loadRequests();
            mainFrame.refreshAllViews();

        } catch (ValidationException ve) {
            JOptionPane.showMessageDialog(this,
                    "Validation Error:\n" + ve.getMessage(),
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (BloodConnectException bce) {
            JOptionPane.showMessageDialog(this,
                    "Failed to create request: " + bce.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleLaunchMatchingForSelected() {
        int row = requestTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select an active blood request from the table to find matching donors.",
                    "No Request Selected",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String requestId = (String) tableModel.getValueAt(row, 0);
        mainFrame.launchMatchingForRequest(requestId);
    }

    private void handleFulfillUnits() {
        int row = requestTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a request from the table to update fulfillment.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reqId = (String) tableModel.getValueAt(row, 0);
        try {
            BloodRequest request = requestService.getRequestById(reqId);
            if (request.getStatus() == RequestStatus.FULFILLED) {
                JOptionPane.showMessageDialog(this,
                        "Request " + reqId + " is already completely fulfilled.",
                        "Notice",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            if (request.getStatus() == RequestStatus.CANCELLED) {
                JOptionPane.showMessageDialog(this,
                        "Cannot fulfill units on a cancelled request.",
                        "Cancelled Request",
                        JOptionPane.WARNING_MESSAGE);
                return;
            }

            String input = JOptionPane.showInputDialog(this,
                    "Request ID: " + reqId + "\n" +
                            "Units Required: " + request.getUnitsRequired() + "\n" +
                            "Units Already Fulfilled: " + request.getUnitsFulfilled() + "\n\n" +
                            "Enter newly updated total fulfilled units (0 to " + request.getUnitsRequired() + "):",
                    request.getUnitsRequired());

            if (input != null && !input.trim().isEmpty()) {
                int units = Integer.parseInt(input.trim());
                requestService.updateUnitsFulfilled(reqId, units);
                loadRequests();
                mainFrame.refreshAllViews();
                JOptionPane.showMessageDialog(this,
                        "Request " + reqId + " updated. Fulfilled: " + units + " / " + request.getUnitsRequired(),
                        "Updated",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer number of units.", "Input Error", JOptionPane.ERROR_MESSAGE);
        } catch (BloodConnectException e) {
            JOptionPane.showMessageDialog(this, "Failed to update units: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCancelRequest() {
        int row = requestTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a request to cancel.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String reqId = (String) tableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to cancel request " + reqId + "?\nThis will mark the request status as CANCELLED.",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                requestService.cancelRequest(reqId);
                loadRequests();
                mainFrame.refreshAllViews();
                JOptionPane.showMessageDialog(this, "Request " + reqId + " has been cancelled.", "Cancelled", JOptionPane.INFORMATION_MESSAGE);
            } catch (BloodConnectException e) {
                JOptionPane.showMessageDialog(this, "Failed to cancel request: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void clearCreateForm() {
        generateNextRequestId();
        txtPatientName.setText("");
        txtUnitsRequired.setText("2");
        cmbUrgency.setSelectedIndex(0);
        cmbBloodGroup.setSelectedIndex(0);
        cmbHospitalPresets.setSelectedIndex(0);
        applyHospitalPreset();
    }
}
