package com.bloodconnect.ui;

import com.bloodconnect.service.BloodRequestService;
import com.bloodconnect.service.DonorService;
import com.bloodconnect.service.MatchingEngine;
import com.bloodconnect.service.ReportService;
import com.bloodconnect.ui.components.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

/**
 * =========================================================================
 * Unit 3 Concept: Swing Main Application Frame, CardLayout & Navigation
 * Root desktop window of the BloodConnect application.
 * Manages modular navigation between Dashboard, Registration, Donors Directory,
 * Blood Requests, Smart Matching Engine, and Reports views.
 * =========================================================================
 */
public class MainFrame extends JFrame {

    public static final String VIEW_DASHBOARD = "DASHBOARD";
    public static final String VIEW_DONOR_REGISTER = "DONOR_REGISTER";
    public static final String VIEW_DONORS = "DONORS";
    public static final String VIEW_REQUESTS = "REQUESTS";
    public static final String VIEW_MATCHING = "MATCHING";
    public static final String VIEW_REPORTS = "REPORTS";

    private final DonorService donorService;
    private final BloodRequestService requestService;
    private final MatchingEngine matchingEngine;
    private final ReportService reportService;

    // View panels
    private CardLayout cardLayout;
    private JPanel contentContainer;

    private DashboardPanel dashboardPanel;
    private DonorRegistrationPanel registrationPanel;
    private DonorListPanel donorListPanel;
    private BloodRequestPanel requestPanel;
    private SmartMatchingPanel matchingPanel;
    private ReportPanel reportPanel;

    private List<NavButton> navButtons;
    private String currentView = VIEW_DASHBOARD;

    public MainFrame(DonorService donorService, BloodRequestService requestService,
                     MatchingEngine matchingEngine, ReportService reportService) {
        super("BloodConnect – Smart Blood Donor Matching & Emergency Response System");
        this.donorService = donorService;
        this.requestService = requestService;
        this.matchingEngine = matchingEngine;
        this.reportService = reportService;
        this.navButtons = new ArrayList<>();

        initFrame();
    }

    private void initFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1320, 840);
        setMinimumSize(new Dimension(1080, 700));
        setLocationRelativeTo(null);

        // Main layout container
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(UIConstants.COLOR_BACKGROUND);

        // Sidebar Navigation
        JPanel sidebar = createSidebar();

        // Central Card View
        cardLayout = new CardLayout();
        contentContainer = new JPanel(cardLayout);
        contentContainer.setBackground(UIConstants.COLOR_BACKGROUND);

        // Instantiate Child Views
        dashboardPanel = new DashboardPanel(donorService, requestService, reportService, this);
        registrationPanel = new DonorRegistrationPanel(donorService, this);
        donorListPanel = new DonorListPanel(donorService, this);
        requestPanel = new BloodRequestPanel(requestService, this);
        matchingPanel = new SmartMatchingPanel(donorService, requestService, matchingEngine, this);
        reportPanel = new ReportPanel(reportService, this);

        contentContainer.add(dashboardPanel, VIEW_DASHBOARD);
        contentContainer.add(registrationPanel, VIEW_DONOR_REGISTER);
        contentContainer.add(donorListPanel, VIEW_DONORS);
        contentContainer.add(requestPanel, VIEW_REQUESTS);
        contentContainer.add(matchingPanel, VIEW_MATCHING);
        contentContainer.add(reportPanel, VIEW_REPORTS);

        // Bottom Disclaimer Banner
        JPanel footer = createFooter();

        rootPanel.add(sidebar, BorderLayout.WEST);
        rootPanel.add(contentContainer, BorderLayout.CENTER);
        rootPanel.add(footer, BorderLayout.SOUTH);

        setContentPane(rootPanel);
        showView(VIEW_DASHBOARD);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(UIConstants.COLOR_SIDEBAR_BG);
        sidebar.setPreferredSize(new Dimension(260, 0));
        sidebar.setBorder(BorderFactory.createEmptyBorder(20, 16, 20, 16));

        // Brand Logo & Title Header
        JPanel brandPanel = new JPanel();
        brandPanel.setLayout(new BoxLayout(brandPanel, BoxLayout.Y_AXIS));
        brandPanel.setOpaque(false);
        brandPanel.setAlignmentX(LEFT_ALIGNMENT);

        JLabel brandIcon = new JLabel("♥ BloodConnect");
        brandIcon.setFont(new Font("Segoe UI", Font.BOLD, 22));
        brandIcon.setForeground(UIConstants.COLOR_PRIMARY);

        JLabel brandSubtitle = new JLabel("Smart Donor Matching System");
        brandSubtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        brandSubtitle.setForeground(UIConstants.COLOR_TEXT_MUTED);

        brandPanel.add(brandIcon);
        brandPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        brandPanel.add(brandSubtitle);

        sidebar.add(brandPanel);
        sidebar.add(Box.createRigidArea(new Dimension(0, 28)));

        // Navigation Menu Buttons
        addNavButton(sidebar, "Dashboard", VIEW_DASHBOARD);
        addNavButton(sidebar, "Register Donor", VIEW_DONOR_REGISTER);
        addNavButton(sidebar, "Donors Directory", VIEW_DONORS);
        addNavButton(sidebar, "Blood Requests", VIEW_REQUESTS);
        addNavButton(sidebar, "Smart Matching", VIEW_MATCHING);
        addNavButton(sidebar, "System Reports", VIEW_REPORTS);

        sidebar.add(Box.createVerticalGlue());

        // Sidebar Footer Card
        JPanel infoCard = new JPanel();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBackground(UIConstants.COLOR_SIDEBAR_HOVER);
        infoCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        infoCard.setAlignmentX(LEFT_ALIGNMENT);
        infoCard.setMaximumSize(new Dimension(240, 75));

        JLabel infoTitle = new JLabel("Region: Punjab (PB)");
        infoTitle.setFont(UIConstants.FONT_BADGE);
        infoTitle.setForeground(UIConstants.COLOR_SIDEBAR_TEXT);

        JLabel infoSub = new JLabel("Ludhiana • Jalandhar • CHD");
        infoSub.setFont(UIConstants.FONT_SMALL);
        infoSub.setForeground(UIConstants.COLOR_TEXT_MUTED);

        infoCard.add(infoTitle);
        infoCard.add(Box.createRigidArea(new Dimension(0, 4)));
        infoCard.add(infoSub);

        sidebar.add(infoCard);

        return sidebar;
    }

    private void addNavButton(JPanel sidebar, String text, String viewKey) {
        NavButton btn = new NavButton(text, viewKey);
        btn.setAlignmentX(LEFT_ALIGNMENT);
        btn.addActionListener(e -> showView(viewKey));
        navButtons.add(btn);
        sidebar.add(btn);
        sidebar.add(Box.createRigidArea(new Dimension(0, 6)));
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 6));
        footer.setBackground(UIConstants.COLOR_SIDEBAR_BG);
        footer.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, UIConstants.COLOR_SIDEBAR_ACTIVE));

        JLabel disclaimer = new JLabel(
                "EDUCATIONAL DEMONSTRATION PLATFORM: Proximity and RBC compatibility scores are algorithmic heuristics. Clinical crossmatch required before transfusion."
        );
        disclaimer.setFont(UIConstants.FONT_SMALL);
        disclaimer.setForeground(UIConstants.COLOR_TEXT_MUTED);

        footer.add(disclaimer);
        return footer;
    }

    public void showView(String viewName) {
        this.currentView = viewName;
        cardLayout.show(contentContainer, viewName);

        for (NavButton btn : navButtons) {
            btn.setActive(btn.getViewKey().equals(viewName));
        }

        // Automatic data refresh on view transition
        if (VIEW_DASHBOARD.equals(viewName)) {
            dashboardPanel.refreshData();
        } else if (VIEW_DONORS.equals(viewName)) {
            donorListPanel.loadDonors();
        } else if (VIEW_REQUESTS.equals(viewName)) {
            requestPanel.loadRequests();
        } else if (VIEW_MATCHING.equals(viewName)) {
            matchingPanel.loadActiveRequests();
        } else if (VIEW_REPORTS.equals(viewName)) {
            reportPanel.refreshReport();
        }
    }

    public void launchMatchingForRequest(String requestId) {
        showView(VIEW_MATCHING);
        matchingPanel.setSelectedRequest(requestId);
    }

    public void refreshAllViews() {
        dashboardPanel.refreshData();
        donorListPanel.loadDonors();
        requestPanel.loadRequests();
        matchingPanel.loadActiveRequests();
        reportPanel.refreshReport();
    }

    public void refreshDashboard() {
        dashboardPanel.refreshData();
    }

    /**
     * Inner class for sidebar navigation items with hover and active states.
     */
    private class NavButton extends JButton {
        private final String viewKey;
        private boolean active = false;
        private boolean hovered = false;

        public NavButton(String text, String viewKey) {
            super(text);
            this.viewKey = viewKey;
            setFont(UIConstants.FONT_REGULAR);
            setForeground(UIConstants.COLOR_SIDEBAR_TEXT);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setHorizontalAlignment(SwingConstants.LEFT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setMaximumSize(new Dimension(230, 42));
            setPreferredSize(new Dimension(230, 42));
            setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));

            addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    hovered = true;
                    repaint();
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    hovered = false;
                    repaint();
                }
            });
        }

        public String getViewKey() {
            return viewKey;
        }

        public void setActive(boolean active) {
            this.active = active;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (active) {
                g2.setColor(UIConstants.COLOR_SIDEBAR_ACTIVE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                // Red active indicator strip on left
                g2.setColor(UIConstants.COLOR_PRIMARY);
                g2.fillRoundRect(0, 4, 4, getHeight() - 8, 2, 2);
                setForeground(Color.WHITE);
                setFont(UIConstants.FONT_BOLD);
            } else if (hovered) {
                g2.setColor(UIConstants.COLOR_SIDEBAR_HOVER);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                setForeground(Color.WHITE);
                setFont(UIConstants.FONT_REGULAR);
            } else {
                setForeground(UIConstants.COLOR_SIDEBAR_TEXT);
                setFont(UIConstants.FONT_REGULAR);
            }

            g2.dispose();
            super.paintComponent(g);
        }
    }
}

