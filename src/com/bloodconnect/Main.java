package com.bloodconnect;

import com.bloodconnect.model.BloodRequest;
import com.bloodconnect.model.Donor;
import com.bloodconnect.service.BloodRequestService;
import com.bloodconnect.service.DonorService;
import com.bloodconnect.service.MatchingEngine;
import com.bloodconnect.service.ReportService;
import com.bloodconnect.storage.DataSeeder;
import com.bloodconnect.storage.FileStorageService;
import com.bloodconnect.storage.Storage;
import com.bloodconnect.ui.MainFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * =========================================================================
 * BloodConnect – Smart Blood Donor Matching & Emergency Response System
 *
 * Core Java Desktop Application showcasing:
 * - Unit 1: OOP, Encapsulation, Classes & Methods, Regular Expressions.
 * - Unit 2: Inheritance, Polymorphism, Abstract Classes, Interfaces,
 *           Custom Exceptions, Collections Framework (Lists, Sets, Maps),
 *           and Comparable/Comparator Algorithmic Sorting.
 * - Unit 3: Java Swing GUI, Event Handling, File I/O Streams, Binary
 *           Serialization, and Clean Layered Desktop Architecture.
 *
 * Entry Point: Initializes binary persistent storage, seeds demonstration data,
 * sets modern native UI Look & Feel, and starts the Event Dispatch Thread (EDT).
 * =========================================================================
 */
public class Main {

    public static final String DONORS_FILE_PATH = "data/donors.dat";
    public static final String REQUESTS_FILE_PATH = "data/requests.dat";

    public static void main(String[] args) {
        // Enable high-DPI font anti-aliasing hints for crisp text rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        SwingUtilities.invokeLater(() -> {
            try {
                // Set Native System Look & Feel
                try {
                    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
                } catch (Exception ignored) {
                    // Fall back to cross-platform Look and Feel if system theme is unavailable
                }

                // 1. Initialize Binary File Storage (Requirement 4)
                Storage<Donor, String> donorStorage =
                        new FileStorageService<>(DONORS_FILE_PATH, Donor::getPersonId);

                Storage<BloodRequest, String> requestStorage =
                        new FileStorageService<>(REQUESTS_FILE_PATH, BloodRequest::getRequestId);

                // 2. Pre-populate realistic demonstration data if storage files are empty
                DataSeeder.seedIfEmpty(donorStorage, requestStorage);

                // 3. Initialize Domain Services (Service Layer Architecture)
                DonorService donorService = new DonorService(donorStorage);
                BloodRequestService requestService = new BloodRequestService(requestStorage);
                MatchingEngine matchingEngine = new MatchingEngine();
                ReportService reportService = new ReportService(donorService, requestService);

                // 4. Instantiate and launch Main Application Frame
                MainFrame mainFrame = new MainFrame(donorService, requestService, matchingEngine, reportService);
                mainFrame.setVisible(true);

            } catch (Exception ex) {
                System.err.println("Fatal Application Startup Error: " + ex.getMessage());
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                        "Failed to initialize BloodConnect Application:\n" + ex.getMessage(),
                        "Startup Failure",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
