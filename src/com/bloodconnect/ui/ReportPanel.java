package com.bloodconnect.ui;

import com.bloodconnect.exception.BloodConnectException;
import com.bloodconnect.service.ReportService;
import com.bloodconnect.ui.components.ModernButton;
import com.bloodconnect.ui.components.ModernCard;
import com.bloodconnect.ui.components.UIConstants;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * =========================================================================
 * Unit 3 Concept: Reporting, File Exporting & Monospaced Text Presentation
 * Generates aggregated system analytics, inventory distribution summaries,
 * and handles export of audit reports to the local file system.
 *
 * Implements:
 * - Requirement 6: Reporting and analytics.
 * =========================================================================
 */
public class ReportPanel extends JPanel {

    private final ReportService reportService;
    private final MainFrame mainFrame;

    private JTextArea txtReportArea;

    public ReportPanel(ReportService reportService, MainFrame mainFrame) {
        this.reportService = reportService;
        this.mainFrame = mainFrame;

        setLayout(new BorderLayout(20, 16));
        setBackground(UIConstants.COLOR_BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(24, 28, 24, 28));

        initComponents();
        refreshReport();
    }

    private void initComponents() {
        // --- Header Section ---
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);

        JLabel title = new JLabel("System Reports & Operational Analytics");
        title.setFont(UIConstants.FONT_PAGE_TITLE);
        title.setForeground(UIConstants.COLOR_TEXT_PRIMARY);

        JLabel subtitle = new JLabel("Aggregate statistics, blood group distributions, and exportable operational summaries.");
        subtitle.setFont(UIConstants.FONT_REGULAR);
        subtitle.setForeground(UIConstants.COLOR_TEXT_SECONDARY);

        headerPanel.add(title);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 4)));
        headerPanel.add(subtitle);

        // --- Center: Monospace Report View Card ---
        ModernCard reportCard = new ModernCard(new BorderLayout(0, 12));
        reportCard.setAccentColor(UIConstants.COLOR_PRIMARY);

        JLabel reportHeader = new JLabel("Generated Audit & Transfusion Distribution Report");
        reportHeader.setFont(UIConstants.FONT_SECTION_TITLE);
        reportHeader.setForeground(UIConstants.COLOR_TEXT_PRIMARY);

        txtReportArea = new JTextArea();
        txtReportArea.setFont(new Font("Consolas", Font.PLAIN, 13));
        txtReportArea.setEditable(false);
        txtReportArea.setBackground(UIConstants.COLOR_BACKGROUND);
        txtReportArea.setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        txtReportArea.setBorder(BorderFactory.createEmptyBorder(12, 14, 12, 14));

        JScrollPane scrollPane = new JScrollPane(txtReportArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(UIConstants.COLOR_BORDER));

        reportCard.add(reportHeader, BorderLayout.NORTH);
        reportCard.add(scrollPane, BorderLayout.CENTER);

        // --- Bottom Actions Bar ---
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 10));
        actionPanel.setOpaque(false);

        ModernButton btnRefresh = new ModernButton("Refresh Analytics", ModernButton.Style.OUTLINE);
        btnRefresh.setPreferredSize(new Dimension(160, 38));
        btnRefresh.addActionListener(e -> refreshReport());

        ModernButton btnCopy = new ModernButton("Copy to Clipboard", ModernButton.Style.SECONDARY);
        btnCopy.setPreferredSize(new Dimension(160, 38));
        btnCopy.addActionListener(e -> handleCopyToClipboard());

        ModernButton btnExport = new ModernButton("Export Report (.txt)", ModernButton.Style.PRIMARY);
        btnExport.setPreferredSize(new Dimension(170, 38));
        btnExport.addActionListener(e -> handleExportToFile());

        actionPanel.add(btnRefresh);
        actionPanel.add(btnCopy);
        actionPanel.add(btnExport);

        add(headerPanel, BorderLayout.NORTH);
        add(reportCard, BorderLayout.CENTER);
        add(actionPanel, BorderLayout.SOUTH);
    }

    public void refreshReport() {
        try {
            String reportText = reportService.generateSummaryTextReport();
            txtReportArea.setText(reportText);
            txtReportArea.setCaretPosition(0);
        } catch (BloodConnectException e) {
            JOptionPane.showMessageDialog(this,
                    "Failed to generate report: " + e.getMessage(),
                    "Reporting Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void handleCopyToClipboard() {
        String text = txtReportArea.getText();
        if (text != null && !text.isEmpty()) {
            StringSelection selection = new StringSelection(text);
            Toolkit.getDefaultToolkit().getSystemClipboard().setContents(selection, selection);
            JOptionPane.showMessageDialog(this,
                    "Report copied to system clipboard successfully.",
                    "Copied",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleExportToFile() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String defaultFileName = "BloodConnect_Report_" + timestamp + ".txt";

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save BloodConnect Report");
        fileChooser.setSelectedFile(new File(defaultFileName));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            try (FileWriter writer = new FileWriter(fileToSave)) {
                writer.write(txtReportArea.getText());
                JOptionPane.showMessageDialog(this,
                        "Report successfully exported to:\n" + fileToSave.getAbsolutePath(),
                        "Export Successful",
                        JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                        "Error saving file: " + ex.getMessage(),
                        "Export Failed",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

