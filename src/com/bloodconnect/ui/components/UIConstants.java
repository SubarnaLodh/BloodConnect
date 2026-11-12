package com.bloodconnect.ui.components;

import java.awt.Color;
import java.awt.Font;

/**
 * =========================================================================
 * Unit 3 Concept: Swing GUI Styling, Theme Consistency & Design System
 * Central design tokens, color palette, typography, and layout constants
 * for the BloodConnect Swing user interface.
 * =========================================================================
 */
public final class UIConstants {

    private UIConstants() {
        // Utility class
    }

    // --- Brand & Primary Colors ---
    public static final Color COLOR_PRIMARY = new Color(211, 47, 47);        // Crimson Red #D32F2F
    public static final Color COLOR_PRIMARY_HOVER = new Color(183, 28, 28);  // Deep Red #B71C1C
    public static final Color COLOR_PRIMARY_LIGHT = new Color(255, 235, 238);// Tint #FFEBEE

    // --- Neutrals & Structure ---
    public static final Color COLOR_SIDEBAR_BG = new Color(15, 23, 42);      // Slate 900 #0F172A
    public static final Color COLOR_SIDEBAR_HOVER = new Color(30, 41, 59);   // Slate 800 #1E293B
    public static final Color COLOR_SIDEBAR_ACTIVE = new Color(51, 65, 85);  // Slate 700 #334155
    public static final Color COLOR_SIDEBAR_TEXT = new Color(241, 245, 249); // Slate 100 #F1F5F9

    public static final Color COLOR_BACKGROUND = new Color(248, 250, 252);   // Slate 50 #F8FAFC
    public static final Color COLOR_CARD_BG = Color.WHITE;
    public static final Color COLOR_BORDER = new Color(226, 232, 240);       // Slate 200 #E2E8F0
    public static final Color COLOR_BORDER_FOCUS = new Color(211, 47, 47);

    // --- Typography Colors ---
    public static final Color COLOR_TEXT_PRIMARY = new Color(15, 23, 42);    // Slate 900
    public static final Color COLOR_TEXT_SECONDARY = new Color(100, 116, 139);// Slate 500
    public static final Color COLOR_TEXT_MUTED = new Color(148, 163, 184);   // Slate 400

    // --- Status & Semantic Colors ---
    public static final Color COLOR_SUCCESS = new Color(16, 185, 129);       // Emerald #10B981
    public static final Color COLOR_SUCCESS_BG = new Color(209, 250, 229);   // #D1FAE5
    public static final Color COLOR_SUCCESS_TEXT = new Color(6, 95, 70);

    public static final Color COLOR_WARNING = new Color(245, 158, 11);       // Amber #F59E0B
    public static final Color COLOR_WARNING_BG = new Color(254, 243, 199);   // #FEF3C7
    public static final Color COLOR_WARNING_TEXT = new Color(146, 64, 14);

    public static final Color COLOR_DANGER = new Color(239, 68, 68);         // Rose/Red #EF4444
    public static final Color COLOR_DANGER_BG = new Color(254, 226, 226);    // #FEE2E2
    public static final Color COLOR_DANGER_TEXT = new Color(153, 27, 27);

    public static final Color COLOR_INFO = new Color(59, 130, 246);          // Blue #3B82F6
    public static final Color COLOR_INFO_BG = new Color(219, 234, 254);      // #DBEAFE
    public static final Color COLOR_INFO_TEXT = new Color(30, 64, 175);

    // --- Typography Fonts ---
    public static final Font FONT_APP_TITLE = new Font("Segoe UI", Font.BOLD, 20);
    public static final Font FONT_PAGE_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_SECTION_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_HEADER = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_KPI_NUMBER = new Font("Segoe UI", Font.BOLD, 30);
    public static final Font FONT_BADGE = new Font("Segoe UI", Font.BOLD, 11);
}

