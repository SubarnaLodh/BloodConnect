package com.bloodconnect.ui;

import com.bloodconnect.exception.StorageException;
import com.bloodconnect.model.BloodRequest;
import com.bloodconnect.service.BloodRequestService;
import com.bloodconnect.service.DonorService;
import com.bloodconnect.service.ReportService;
import com.bloodconnect.ui.components.ModernButton;
import com.bloodconnect.ui.components.ModernCard;
import com.bloodconnect.ui.components.ModernTable;
import com.bloodconnect.ui.components.StatusBadgeRenderer;
import com.bloodconnect.ui.components.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
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
import java.util.List;

/**
 * =========================================================================
 * Unit 3 Concept: Swing Dashboard Layout, KPI Metric Cards & Data Refreshing
 * Central dashboard providing real-time system metrics, emergency alerts,
 * quick navigation actions, and high-priority request tracking.
 * =========================================================================
 */
public class DashboardPanel extends JPanel {

    private final DonorService donorService;
    private final BloodRequestService requestService;
    private final ReportService reportService;
    private final MainFrame mainFrame;

    private JLabel lblTotalDonors;
    private JLabel lblAvailableDonors;
    private JLabel lblActiveRequests;
    private JLabel lblEmergencyRequests;

    private DefaultTableModel urgentRequestsModel;
    private ModernTable urgentRequestsTable;

    public DashboardPanel(DonorService donorService, BloodRequestService requestService,
                          ReportService reportService, MainFrame mainFrame) {
        this.donorService = donorService;
        this.requestService = requestService;
        this.reportService = reportService;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(20, 20));
        setBackground(UIConstants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        initComponents();
        refreshData();
    }

    private void initComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JPanel titleBlock = new JPanel();
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.setOpaque(false);

        JLabel title = new JLabel("System Dashboard & Live Operations");
        title.setFont(UIConstants.FONT_PAGE_TITLE);
        title.setForeground(UIConstants.COLOR_TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Punjab Regional Emergency Blood Donation Coordination Center");
        subtitle.setFont(UIConstants.FONT_REGULAR);
        subtitle.setForeground(UIConstants.COLOR_TEXT_SECONDARY);

        titleBlock.add(title);
        titleBlock.add(Box.createRigidArea(new Dimension(0, 4)));
        titleBlock.add(subtitle);

        ModernButton btnRefresh = new ModernButton("Refresh Stats", ModernButton.Style.OUTLINE);
        btnRefresh.setPreferredSize(new Dimension(130, 36));
        btnRefresh.addActionListener(e -> refreshData());

        headerPanel.add(titleBlock, BorderLayout.WEST);
        headerPanel.add(btnRefresh, BorderLayout.EAST);

        // --- KPI Metrics Row ---
        JPanel kpiGrid = new JPanel(new GridLayout(1, 4, 16, 0));
        kpiGrid.setOpaque(false);
        kpiGrid.setPreferredSize(new Dimension(0, 115));

        lblTotalDonors = new JLabel("0");
        lblAvailableDonors = new JLabel("0");
        lblActiveRequests = new JLabel("0");
        lblEmergencyRequests = new JLabel("0");

        kpiGrid.add(createKpiCard("Registered Donors", lblTotalDonors, "In database roster", UIConstants.COLOR_PRIMARY));
        kpiGrid.add(createKpiCard("Available Donors", lblAvailableDonors, "Ready for matching", UIConstants.COLOR_SUCCESS));
        kpiGrid.add(createKpiCard("Active Requests", lblActiveRequests, "Pending or matched", UIConstants.COLOR_WARNING));
        kpiGrid.add(createKpiCard("Critical SOS Alerts", lblEmergencyRequests, "Immediate response required", UIConstants.COLOR_DANGER));

        // --- Quick Actions Bar ---
        ModernCard actionsCard = new ModernCard(new FlowLayout(FlowLayout.LEFT, 12, 10));
        JLabel actLabel = new JLabel("Quick Actions: ");
        actLabel.setFont(UIConstants.FONT_HEADER);
        actLabel.setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        actionsCard.add(actLabel);

        ModernButton btnNewDonor = new ModernButton("+ Register Donor", ModernButton.Style.PRIMARY);
        btnNewDonor.setPreferredSize(new Dimension(150, 36));
        btnNewDonor.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_DONOR_REGISTER));
        actionsCard.add(btnNewDonor);

        ModernButton btnNewReq = new ModernButton("+ Create Request", ModernButton.Style.SECONDARY);
        btnNewReq.setPreferredSize(new Dimension(150, 36));
        btnNewReq.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_REQUESTS));
        actionsCard.add(btnNewReq);

        ModernButton btnMatch = new ModernButton("Run Smart Matching", ModernButton.Style.SUCCESS);
        btnMatch.setPreferredSize(new Dimension(170, 36));
        btnMatch.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_MATCHING));
        actionsCard.add(btnMatch);

        ModernButton btnReport = new ModernButton("View Full Report", ModernButton.Style.OUTLINE);
        btnReport.setPreferredSize(new Dimension(150, 36));
        btnReport.addActionListener(e -> mainFrame.showView(MainFrame.VIEW_REPORTS));
        actionsCard.add(btnReport);

        // --- Center Content: Emergency Requests Table ---
        ModernCard tableCard = new ModernCard(new BorderLayout(0, 12));
        tableCard.setAccentColor(UIConstants.COLOR_DANGER);

        JLabel tableTitle = new JLabel("Active Emergency & Critical Requests");
        tableTitle.setFont(UIConstants.FONT_SECTION_TITLE);
        tableTitle.setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        tableCard.add(tableTitle, BorderLayout.NORTH);

        String[] columns = {"Request ID", "Patient Name", "Hospital", "Blood Group", "Units Req.", "Units Done", "Urgency", "Status"};
        urgentRequestsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        urgentRequestsTable = new ModernTable(urgentRequestsModel);
        urgentRequestsTable.getColumnModel().getColumn(3).setCellRenderer(new StatusBadgeRenderer());
        urgentRequestsTable.getColumnModel().getColumn(6).setCellRenderer(new StatusBadgeRenderer());
        urgentRequestsTable.getColumnModel().getColumn(7).setCellRenderer(new StatusBadgeRenderer());

        JScrollPane scrollPane = new JScrollPane(urgentRequestsTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER));
        tableCard.add(scrollPane, BorderLayout.CENTER);

        // Assemble Dashboard Layout
        JPanel topContainer = new JPanel();
        topContainer.setLayout(new BoxLayout(topContainer, BoxLayout.Y_AXIS));
        topContainer.setOpaque(false);
        topContainer.add(headerPanel);
        topContainer.add(Box.createRigidArea(new Dimension(0, 16)));
        topContainer.add(kpiGrid);
        topContainer.add(Box.createRigidArea(new Dimension(0, 16)));
        topContainer.add(actionsCard);

        add(topContainer, BorderLayout.NORTH);
        add(tableCard, BorderLayout.CENTER);
    }

    private ModernCard createKpiCard(String label, JLabel valueLabel, String subtitle, Color accentColor) {
        ModernCard card = new ModernCard(new BorderLayout(), accentColor);
        card.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));

        JLabel lblTitle = new JLabel(label);
        lblTitle.setFont(UIConstants.FONT_HEADER);
        lblTitle.setForeground(UIConstants.COLOR_TEXT_SECONDARY);

        valueLabel.setFont(UIConstants.FONT_KPI_NUMBER);
        valueLabel.setForeground(UIConstants.COLOR_TEXT_PRIMARY);

        JLabel lblSub = new JLabel(subtitle);
        lblSub.setFont(UIConstants.FONT_SMALL);
        lblSub.setForeground(UIConstants.COLOR_TEXT_MUTED);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);

        return card;
    }

    public void refreshData() {
        try {
            lblTotalDonors.setText(String.valueOf(reportService.getTotalDonorsCount()));
            lblAvailableDonors.setText(String.valueOf(reportService.getAvailableDonorsCount()));
            lblActiveRequests.setText(String.valueOf(reportService.getActiveRequestsCount()));
            lblEmergencyRequests.setText(String.valueOf(reportService.getEmergencyRequestsCount()));

            // Refresh emergency table
            urgentRequestsModel.setRowCount(0);
            List<BloodRequest> emergency = requestService.getEmergencyRequests();
            for (BloodRequest req : emergency) {
                urgentRequestsModel.addRow(new Object[]{
                        req.getRequestId(),
                        req.getRecipient() != null ? req.getRecipient().getFullName() : "-",
                        req.getRecipient() != null ? req.getRecipient().getHospitalName() : "-",
                        req.getRequiredBloodGroup(),
                        req.getUnitsRequired(),
                        req.getUnitsFulfilled(),
                        req.getUrgencyLevel(),
                        req.getStatus()
                });
            }
        } catch (StorageException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to refresh dashboard data: " + e.getMessage(),
                    "Storage Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}

