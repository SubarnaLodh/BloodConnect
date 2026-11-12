package com.bloodconnect.ui;

import com.bloodconnect.exception.BloodConnectException;
import com.bloodconnect.model.BloodGroup;
import com.bloodconnect.model.Donor;
import com.bloodconnect.service.DonorService;
import com.bloodconnect.ui.components.ModernButton;
import com.bloodconnect.ui.components.ModernCard;
import com.bloodconnect.ui.components.ModernTable;
import com.bloodconnect.ui.components.ModernTextField;
import com.bloodconnect.ui.components.StatusBadgeRenderer;
import com.bloodconnect.ui.components.UIConstants;
import com.bloodconnect.util.DateUtil;

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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

/**
 * =========================================================================
 * Unit 3 Concept: Swing JTable Search, Filtering, Row Selection & Dialog Interaction
 * Comprehensive donor management table with dynamic filtering (blood group,
 * city, availability, eligibility) and privacy-protected contact access.
 *
 * Enforces:
 * - Requirement 7: Configurable demonstration eligibility verification.
 * - Requirement 8: Privacy protection (masked contact details by default).
 * =========================================================================
 */
public class DonorListPanel extends JPanel {

    private final DonorService donorService;
    private final MainFrame mainFrame;

    // Filter controls
    private JComboBox<String> cmbBloodGroupFilter;
    private JComboBox<String> cmbAvailabilityFilter;
    private ModernTextField txtCitySearch;
    private JCheckBox chkEligibleOnly;

    // Table
    private DefaultTableModel tableModel;
    private ModernTable donorTable;

    public DonorListPanel(DonorService donorService, MainFrame mainFrame) {
        this.donorService = donorService;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(20, 16));
        setBackground(UIConstants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        initComponents();
        loadDonors();
    }

    private void initComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Registered Donors Directory");
        title.setFont(UIConstants.FONT_PAGE_TITLE);
        title.setForeground(UIConstants.COLOR_TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Search, filter, and manage donor availability and contact records with privacy compliance.");
        subtitle.setFont(UIConstants.FONT_REGULAR);
        subtitle.setForeground(UIConstants.COLOR_TEXT_SECONDARY);

        headerPanel.add(title);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(subtitle);

        // --- Filter Bar Card ---
        ModernCard filterCard = new ModernCard(new FlowLayout(FlowLayout.LEFT, 12, 8));

        JLabel lblBg = new JLabel("Blood Group:");
        lblBg.setFont(UIConstants.FONT_HEADER);
        cmbBloodGroupFilter = new JComboBox<>();
        cmbBloodGroupFilter.addItem("ALL");
        for (BloodGroup bg : BloodGroup.values()) {
            cmbBloodGroupFilter.addItem(bg.getSymbol());
        }
        cmbBloodGroupFilter.setPreferredSize(new Dimension(90, 34));

        JLabel lblAvail = new JLabel("Status:");
        lblAvail.setFont(UIConstants.FONT_HEADER);
        cmbAvailabilityFilter = new JComboBox<>(new String[]{"ALL", "Available", "Unavailable"});
        cmbAvailabilityFilter.setPreferredSize(new Dimension(110, 34));

        JLabel lblCity = new JLabel("City:");
        lblCity.setFont(UIConstants.FONT_HEADER);
        txtCitySearch = new ModernTextField("Filter city...", 10);
        txtCitySearch.setPreferredSize(new Dimension(130, 34));

        chkEligibleOnly = new JCheckBox("Eligible Only (Demo)", false);
        chkEligibleOnly.setFont(UIConstants.FONT_REGULAR);
        chkEligibleOnly.setOpaque(false);

        ModernButton btnApply = new ModernButton("Filter", ModernButton.Style.PRIMARY);
        btnApply.setPreferredSize(new Dimension(85, 34));
        btnApply.addActionListener(e -> loadDonors());

        ModernButton btnReset = new ModernButton("Reset", ModernButton.Style.OUTLINE);
        btnReset.setPreferredSize(new Dimension(85, 34));
        btnReset.addActionListener(e -> resetFilters());

        filterCard.add(lblBg);
        filterCard.add(cmbBloodGroupFilter);
        filterCard.add(lblAvail);
        filterCard.add(cmbAvailabilityFilter);
        filterCard.add(lblCity);
        filterCard.add(txtCitySearch);
        filterCard.add(chkEligibleOnly);
        filterCard.add(btnApply);
        filterCard.add(btnReset);

        // --- Table Section ---
        String[] columns = {
                "Donor ID", "Full Name", "Blood Type", "Age", "Weight",
                "City & Zone", "Last Donated", "Days Elapsed", "Available", "Demo Eligible", "Consent"
        };

        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        donorTable = new ModernTable(tableModel);
        donorTable.getColumnModel().getColumn(2).setCellRenderer(new StatusBadgeRenderer()); // Blood Type
        donorTable.getColumnModel().getColumn(8).setCellRenderer(new StatusBadgeRenderer()); // Available
        donorTable.getColumnModel().getColumn(9).setCellRenderer(new StatusBadgeRenderer()); // Demo Eligible
        donorTable.getColumnModel().getColumn(10).setCellRenderer(new StatusBadgeRenderer()); // Consent

        JScrollPane scrollPane = new JScrollPane(donorTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER));

        // --- Action Buttons Bar ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        actionPanel.setOpaque(false);

        ModernButton btnToggle = new ModernButton("Toggle Availability", ModernButton.Style.SECONDARY);
        btnToggle.setPreferredSize(new Dimension(170, 38));
        btnToggle.addActionListener(e -> handleToggleAvailability());

        ModernButton btnContact = new ModernButton("Reveal Contact (Consent)", ModernButton.Style.SUCCESS);
        btnContact.setPreferredSize(new Dimension(190, 38));
        btnContact.addActionListener(e -> handleRevealContact());

        ModernButton btnDelete = new ModernButton("Delete Donor", ModernButton.Style.DANGER);
        btnDelete.setPreferredSize(new Dimension(140, 38));
        btnDelete.addActionListener(e -> handleDeleteDonor());

        actionPanel.add(btnToggle);
        actionPanel.add(btnContact);
        actionPanel.add(btnDelete);

        // Assemble Top Header + Filter
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.add(headerPanel);
        topContainer.add(Box.createRigidArea(new Dimension(0, 12)));
        topContainer.add(filterCard);

        add(topContainer, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    public void loadDonors() {
        try {
            String selectedBg = (String) cmbBloodGroupFilter.getSelectedItem();
            BloodGroup group = ("ALL".equals(selectedBg)) ? null : BloodGroup.fromString(selectedBg);

            String selectedAvail = (String) cmbAvailabilityFilter.getSelectedItem();
            Boolean available = null;
            if ("Available".equals(selectedAvail)) available = true;
            else if ("Unavailable".equals(selectedAvail)) available = false;

            String city = txtCitySearch.getText().trim();
            Boolean eligibleOnly = chkEligibleOnly.isSelected() ? true : null;

            List<Donor> donors = donorService.filterDonors(group, available, city, eligibleOnly);
            tableModel.setRowCount(0);

            for (Donor d : donors) {
                String lastDate = (d.getLastDonationDate() != null) ? DateUtil.formatDate(d.getLastDonationDate()) : "Never";
                String daysElapsed = (d.getLastDonationDate() != null) ? (d.getDaysSinceLastDonation() + " days") : "N/A";

                tableModel.addRow(new Object[]{
                        d.getPersonId(),
                        d.getFullName(),
                        d.getBloodGroup(),
                        d.getAge() + " yrs",
                        String.format("%.1f kg", d.getWeightKg()),
                        d.getLocation() != null ? d.getLocation().toString() : "-",
                        lastDate,
                        daysElapsed,
                        d.isAvailable() ? "Available" : "Unavailable",
                        d.isEligibleForDonation() ? "Eligible" : "Ineligible",
                        d.isEmergencyConsentGranted() ? "YES" : "NO"
                });
            }
        } catch (BloodConnectException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to load donors: " + e.getMessage(),
                    "Storage Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void resetFilters() {
        cmbBloodGroupFilter.setSelectedIndex(0);
        cmbAvailabilityFilter.setSelectedIndex(0);
        txtCitySearch.setText("");
        chkEligibleOnly.setSelected(false);
        loadDonors();
    }

    private void handleToggleAvailability() {
        int selectedRow = donorTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a donor from the table to toggle availability.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String donorId = (String) tableModel.getValueAt(selectedRow, 0);
        try {
            boolean newState = donorService.toggleAvailability(donorId);
            loadDonors();
            mainFrame.refreshDashboard();
            JOptionPane.showMessageDialog(this,
                    "Donor " + donorId + " availability updated to: " + (newState ? "AVAILABLE" : "UNAVAILABLE"),
                    "Availability Updated",
                    JOptionPane.INFORMATION_MESSAGE);
        } catch (BloodConnectException e) {
            JOptionPane.showMessageDialog(this,
                    "Error updating donor availability: " + e.getMessage(),
                    "Operation Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleRevealContact() {
        int selectedRow = donorTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a donor from the table to view contact information.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String donorId = (String) tableModel.getValueAt(selectedRow, 0);
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

            boolean authorized = (choice == JOptionPane.YES_OPTION);

            if (authorized) {
                if (donor.isEmergencyConsentGranted()) {
                    String contact = donorService.getContactWithConsent(donorId, true);
                    JOptionPane.showMessageDialog(this,
                            "EMERGENCY CONTACT DETAILS REVEALED:\n\n" +
                                    "Donor Name  : " + donor.getFullName() + "\n" +
                                    "Phone Number: " + contact + "\n" +
                                    "Email       : " + donor.getEmail() + "\n" +
                                    "Blood Group : " + donor.getBloodGroup().getSymbol() + "\n" +
                                    "Location    : " + donor.getLocation() + "\n\n" +
                                    "Status: Explicit Emergency Outreach Consent Verified.",
                            "Contact Information",
                            JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this,
                            "ACCESS RESTRICTED BY DONOR PRIVACY PREFERENCE:\n\n" +
                                    "Donor " + donor.getFullName() + " has NOT granted explicit consent\n" +
                                    "for direct emergency contact unmasking.\n\n" +
                                    "Masked Contact: " + donor.getMaskedPhoneNumber() + "\n" +
                                    "Masked Email  : " + donor.getMaskedEmail() + "\n\n" +
                                    "Please coordinate through standard hospital dispatch channels.",
                            "Privacy Protected",
                            JOptionPane.WARNING_MESSAGE);
                }
            }
        } catch (BloodConnectException e) {
            JOptionPane.showMessageDialog(this,
                    "Error retrieving donor details: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleDeleteDonor() {
        int selectedRow = donorTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this,
                    "Please select a donor from the table to delete.",
                    "No Selection",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String donorId = (String) tableModel.getValueAt(selectedRow, 0);
        String donorName = (String) tableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete donor '" + donorName + "' (" + donorId + ")?\nThis action cannot be undone.",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                donorService.deleteDonor(donorId);
                loadDonors();
                mainFrame.refreshAllViews();
                JOptionPane.showMessageDialog(this,
                        "Donor " + donorId + " deleted successfully.",
                        "Deleted",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (BloodConnectException e) {
                JOptionPane.showMessageDialog(this,
                        "Failed to delete donor: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
