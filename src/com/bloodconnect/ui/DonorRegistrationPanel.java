package com.bloodconnect.ui;

import com.bloodconnect.exception.BloodConnectException;
import com.bloodconnect.exception.ValidationException;
import com.bloodconnect.model.BloodGroup;
import com.bloodconnect.model.Donor;
import com.bloodconnect.model.Location;
import com.bloodconnect.service.DonorService;
import com.bloodconnect.ui.components.ModernButton;
import com.bloodconnect.ui.components.ModernCard;
import com.bloodconnect.ui.components.ModernTextField;
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * =========================================================================
 * Unit 3 Concept: Swing Form Layouts (GridBagLayout), Event Handling & Input Validation
 * Panel for capturing and registering new volunteer blood donors into the system.
 * Features automated ID generation, location presets, live validation, and
 * privacy consent controls.
 * =========================================================================
 */
public class DonorRegistrationPanel extends JPanel {

    private final DonorService donorService;
    private final MainFrame mainFrame;

    // Form fields
    private ModernTextField txtDonorId;
    private ModernTextField txtFullName;
    private ModernTextField txtPhone;
    private ModernTextField txtEmail;
    private JComboBox<BloodGroup> cmbBloodGroup;
    private ModernTextField txtAge;
    private ModernTextField txtWeight;
    private JComboBox<String> cmbLocationPresets;
    private ModernTextField txtCity;
    private ModernTextField txtZone;
    private ModernTextField txtLatitude;
    private ModernTextField txtLongitude;
    private ModernTextField txtLastDonationDate;
    private ModernTextField txtTotalDonations;
    private JCheckBox chkAvailable;
    private JCheckBox chkEmergencyConsent;

    // Presets data: [Name, City, Zone, Lat, Lon]
    private static final String[][] LOCATION_PRESETS = {
            {"Ludhiana - Civil Lines", "Ludhiana", "Civil Lines", "30.9010", "75.8573"},
            {"Ludhiana - Model Town", "Ludhiana", "Model Town", "30.8845", "75.8362"},
            {"Ludhiana - Focal Point", "Ludhiana", "Focal Point", "30.8920", "75.9120"},
            {"Jalandhar - Model Town", "Jalandhar", "Model Town", "31.3260", "75.5762"},
            {"Phagwara - GT Road", "Phagwara", "GT Road", "31.2240", "75.7708"},
            {"Amritsar - Mall Road", "Amritsar", "Mall Road", "31.6340", "74.8723"},
            {"Chandigarh - Sector 17", "Chandigarh", "Sector 17", "30.7333", "76.7794"},
            {"Patiala - Leela Bhawan", "Patiala", "Leela Bhawan", "30.3398", "76.3869"},
            {"Custom Location...", "", "", "", ""}
    };

    public DonorRegistrationPanel(DonorService donorService, MainFrame mainFrame) {
        this.donorService = donorService;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        initComponents();
    }

    private void initComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("Register New Blood Donor");
        title.setFont(UIConstants.FONT_PAGE_TITLE);
        title.setForeground(UIConstants.COLOR_TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Enroll a volunteer donor into the regional matching network with verification.");
        subtitle.setFont(UIConstants.FONT_REGULAR);
        subtitle.setForeground(UIConstants.COLOR_TEXT_SECONDARY);

        headerPanel.add(title);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(subtitle);

        // --- Main Form Card ---
        ModernCard formCard = new ModernCard(new GridBagLayout());
        formCard.setAccentColor(UIConstants.COLOR_PRIMARY);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 12, 8, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 0.5;

        // Initialize Form Fields
        txtDonorId = new ModernTextField();
        generateNextDonorId();

        txtFullName = new ModernTextField("e.g. Gurpreet Singh");
        txtPhone = new ModernTextField("e.g. 9876543210");
        txtEmail = new ModernTextField("e.g. gurpreet@example.com");

        cmbBloodGroup = new JComboBox<>(BloodGroup.values());
        cmbBloodGroup.setFont(UIConstants.FONT_REGULAR);
        cmbBloodGroup.setPreferredSize(new Dimension(200, 38));

        txtAge = new ModernTextField("e.g. 28");
        txtWeight = new ModernTextField("e.g. 68.5");

        String[] presetNames = new String[LOCATION_PRESETS.length];
        for (int i = 0; i < LOCATION_PRESETS.length; i++) {
            presetNames[i] = LOCATION_PRESETS[i][0];
        }
        cmbLocationPresets = new JComboBox<>(presetNames);
        cmbLocationPresets.setFont(UIConstants.FONT_REGULAR);
        cmbLocationPresets.setPreferredSize(new Dimension(200, 38));
        cmbLocationPresets.addActionListener(e -> applyLocationPreset());

        txtCity = new ModernTextField("e.g. Ludhiana");
        txtZone = new ModernTextField("e.g. Civil Lines");
        txtLatitude = new ModernTextField("e.g. 30.9010");
        txtLongitude = new ModernTextField("e.g. 75.8573");

        applyLocationPreset(); // initialize with first preset

        txtLastDonationDate = new ModernTextField("YYYY-MM-DD or leave blank if first time");
        txtTotalDonations = new ModernTextField("0");

        chkAvailable = new JCheckBox("Mark as available for active blood donation calls", true);
        chkAvailable.setFont(UIConstants.FONT_REGULAR);
        chkAvailable.setOpaque(false);

        chkEmergencyConsent = new JCheckBox(
                "<html><b>Grant explicit consent</b> to reveal unmasked contact details during critical emergencies (Privacy Protected)</html>",
                false
        );
        chkEmergencyConsent.setFont(UIConstants.FONT_REGULAR);
        chkEmergencyConsent.setOpaque(false);

        // Layout Fields in 2 Columns
        // Row 0: Donor ID & Full Name
        int row = 0;
        addFormField(formCard, gbc, "Donor ID (Auto-Generated):", txtDonorId, 0, row);
        addFormField(formCard, gbc, "Full Name *:", txtFullName, 1, row++);

        // Row 1: Contact Phone & Email
        addFormField(formCard, gbc, "Mobile Number * (10 Digits):", txtPhone, 0, row);
        addFormField(formCard, gbc, "Email Address *:", txtEmail, 1, row++);

        // Row 2: Blood Group & Age
        addFormField(formCard, gbc, "ABO/Rh Blood Group *:", cmbBloodGroup, 0, row);
        addFormField(formCard, gbc, "Age (18 - 65 Years) *:", txtAge, 1, row++);

        // Row 3: Weight & Location Preset
        addFormField(formCard, gbc, "Weight in Kg (Min 50.0) *:", txtWeight, 0, row);
        addFormField(formCard, gbc, "Geographic Location Preset:", cmbLocationPresets, 1, row++);

        // Row 4: City & Zone
        addFormField(formCard, gbc, "City *:", txtCity, 0, row);
        addFormField(formCard, gbc, "Zone / Area:", txtZone, 1, row++);

        // Row 5: Coordinates (Lat / Lon)
        addFormField(formCard, gbc, "Latitude [-90 to 90]:", txtLatitude, 0, row);
        addFormField(formCard, gbc, "Longitude [-180 to 180]:", txtLongitude, 1, row++);

        // Row 6: Last Donation Date & Total Donations
        addFormField(formCard, gbc, "Last Donation Date (Optional):", txtLastDonationDate, 0, row);
        addFormField(formCard, gbc, "Total Lifetime Donations:", txtTotalDonations, 1, row++);

        // Row 7: Checkboxes spanning full width
        gbc.gridx = 0;
        gbc.gridy = row++;
        gbc.gridwidth = 2;
        formCard.add(chkAvailable, gbc);

        gbc.gridy = row++;
        formCard.add(chkEmergencyConsent, gbc);

        // Row 8: Educational Disclaimer Banner
        JLabel disclaimerLabel = new JLabel("<html><font color='#64748B'><i>Note: In accordance with demonstration criteria, donors must be 18–65 years old, weigh at least 50 kg, and observe a 90-day cooldown between donations.</i></font></html>");
        disclaimerLabel.setFont(UIConstants.FONT_SMALL);
        gbc.gridy = row++;
        formCard.add(disclaimerLabel, gbc);

        // Row 9: Action Buttons
        JPanel buttonRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        buttonRow.setOpaque(false);

        ModernButton btnClear = new ModernButton("Clear Form", ModernButton.Style.OUTLINE);
        btnClear.setPreferredSize(new Dimension(130, 38));
        btnClear.addActionListener(e -> clearForm());

        ModernButton btnSubmit = new ModernButton("Register Donor", ModernButton.Style.PRIMARY);
        btnSubmit.setPreferredSize(new Dimension(160, 38));
        btnSubmit.addActionListener(e -> handleRegisterDonor());

        buttonRow.add(btnClear);
        buttonRow.add(btnSubmit);

        gbc.gridy = row++;
        formCard.add(buttonRow, gbc);

        JScrollPane scrollPane = new JScrollPane(formCard);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        add(headerPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
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
        gbc.gridwidth = 1;
        panel.add(fieldPanel, gbc);
    }

    private void applyLocationPreset() {
        int idx = cmbLocationPresets.getSelectedIndex();
        if (idx >= 0 && idx < LOCATION_PRESETS.length - 1) {
            txtCity.setText(LOCATION_PRESETS[idx][1]);
            txtZone.setText(LOCATION_PRESETS[idx][2]);
            txtLatitude.setText(LOCATION_PRESETS[idx][3]);
            txtLongitude.setText(LOCATION_PRESETS[idx][4]);
        }
    }

    private void generateNextDonorId() {
        try {
            int count = donorService.getAllDonors().size();
            txtDonorId.setText("DNR-" + (101 + count));
        } catch (Exception e) {
            txtDonorId.setText("DNR-" + (System.currentTimeMillis() % 10000));
        }
    }

    private void handleRegisterDonor() {
        try {
            String donorId = txtDonorId.getText().trim();
            String fullName = txtFullName.getText().trim();
            String phone = txtPhone.getText().trim();
            String email = txtEmail.getText().trim();
            BloodGroup group = (BloodGroup) cmbBloodGroup.getSelectedItem();

            int age;
            try {
                age = Integer.parseInt(txtAge.getText().trim());
            } catch (NumberFormatException e) {
                throw new ValidationException("age", "Donor age must be a valid integer number.");
            }

            double weight;
            try {
                weight = Double.parseDouble(txtWeight.getText().trim());
            } catch (NumberFormatException e) {
                throw new ValidationException("weightKg", "Donor weight must be a valid numerical value in kg.");
            }

            String city = txtCity.getText().trim();
            String zone = txtZone.getText().trim();
            double lat;
            double lon;
            try {
                lat = Double.parseDouble(txtLatitude.getText().trim());
                lon = Double.parseDouble(txtLongitude.getText().trim());
            } catch (NumberFormatException e) {
                throw new ValidationException("location", "Latitude and Longitude must be valid decimal coordinates.");
            }

            Location location = new Location(city, zone, lat, lon);

            LocalDate lastDonationDate = null;
            String dateText = txtLastDonationDate.getText().trim();
            if (!dateText.isEmpty() && !dateText.equalsIgnoreCase("YYYY-MM-DD or leave blank if first time")) {
                try {
                    lastDonationDate = DateUtil.parseDate(dateText);
                } catch (DateTimeParseException e) {
                    throw new ValidationException("lastDonationDate", "Invalid date format. Please use YYYY-MM-DD (e.g. 2024-05-15).");
                }
            }

            int totalDonations = 0;
            String donationsText = txtTotalDonations.getText().trim();
            if (!donationsText.isEmpty()) {
                try {
                    totalDonations = Integer.parseInt(donationsText);
                } catch (NumberFormatException e) {
                    throw new ValidationException("totalDonations", "Total donations must be a non-negative integer.");
                }
            }

            boolean available = chkAvailable.isSelected();
            boolean emergencyConsent = chkEmergencyConsent.isSelected();

            // Construct Donor model
            Donor donor = new Donor(
                    donorId,
                    fullName,
                    phone,
                    email,
                    location,
                    group,
                    age,
                    weight,
                    lastDonationDate,
                    available,
                    totalDonations,
                    emergencyConsent
            );

            // Register through service layer
            donorService.registerDonor(donor);

            JOptionPane.showMessageDialog(this,
                    "Donor successfully registered!\n" +
                            "ID: " + donor.getPersonId() + "\n" +
                            "Name: " + donor.getFullName() + "\n" +
                            "Blood Group: " + donor.getBloodGroup().getSymbol() + "\n" +
                            "Location: " + donor.getLocation(),
                    "Registration Successful",
                    JOptionPane.INFORMATION_MESSAGE);

            clearForm();
            mainFrame.refreshAllViews();
            mainFrame.showView(MainFrame.VIEW_DONORS);

        } catch (ValidationException ve) {
            highlightFieldError(ve.getFieldName());
            JOptionPane.showMessageDialog(this,
                    "Validation Error:\n" + ve.getMessage(),
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE);
        } catch (BloodConnectException bce) {
            JOptionPane.showMessageDialog(this,
                    "Failed to register donor: " + bce.getMessage(),
                    "System Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Unexpected error: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void highlightFieldError(String fieldName) {
        if ("fullName".equalsIgnoreCase(fieldName)) txtFullName.setError(true);
        else if ("phoneNumber".equalsIgnoreCase(fieldName)) txtPhone.setError(true);
        else if ("email".equalsIgnoreCase(fieldName)) txtEmail.setError(true);
        else if ("age".equalsIgnoreCase(fieldName)) txtAge.setError(true);
        else if ("weightKg".equalsIgnoreCase(fieldName)) txtWeight.setError(true);
        else if ("location".equalsIgnoreCase(fieldName)) {
            txtLatitude.setError(true);
            txtLongitude.setError(true);
        } else if ("lastDonationDate".equalsIgnoreCase(fieldName)) txtLastDonationDate.setError(true);
        else if ("totalDonations".equalsIgnoreCase(fieldName)) txtTotalDonations.setError(true);
    }

    private void clearForm() {
        generateNextDonorId();
        txtFullName.setText("");
        txtPhone.setText("");
        txtEmail.setText("");
        txtAge.setText("");
        txtWeight.setText("");
        txtLastDonationDate.setText("");
        txtTotalDonations.setText("0");
        chkAvailable.setSelected(true);
        chkEmergencyConsent.setSelected(false);
        cmbLocationPresets.setSelectedIndex(0);
        applyLocationPreset();

        // Reset error styling
        txtFullName.setError(false);
        txtPhone.setError(false);
        txtEmail.setError(false);
        txtAge.setError(false);
        txtWeight.setError(false);
        txtLatitude.setError(false);
        txtLongitude.setError(false);
        txtLastDonationDate.setError(false);
        txtTotalDonations.setError(false);
    }
}
