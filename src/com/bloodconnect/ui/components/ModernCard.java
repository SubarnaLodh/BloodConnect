package com.bloodconnect.ui.components;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.LayoutManager;
import java.awt.RenderingHints;

/**
 * =========================================================================
 * Unit 3 Concept: Swing Layout Containers & Custom Panel Painting
 * A clean modern card container panel featuring rounded corners, subtle
 * borders, and optional colored accent stripes.
 * =========================================================================
 */
public class ModernCard extends JPanel {

    private int cornerRadius = 12;
    private Color accentColor = null;
    private int accentHeight = 4;

    public ModernCard() {
        super();
        initCard();
    }

    public ModernCard(LayoutManager layout) {
        super(layout);
        initCard();
    }

    public ModernCard(LayoutManager layout, Color accentColor) {
        super(layout);
        this.accentColor = accentColor;
        initCard();
    }

    private void initCard() {
        setOpaque(false);
        setBackground(UIConstants.COLOR_CARD_BG);
        setBorder(new EmptyBorder(16, 16, 16, 16));
    }

    public void setAccentColor(Color accentColor) {
        this.accentColor = accentColor;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        // Background
        g2.setColor(getBackground());
        g2.fillRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Border
        g2.setColor(UIConstants.COLOR_BORDER);
        g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Optional top accent line
        if (accentColor != null) {
            g2.setColor(accentColor);
            g2.fillRoundRect(0, 0, width - 1, accentHeight + 2, cornerRadius, cornerRadius);
            // Flatten bottom of accent inside card
            g2.fillRect(0, accentHeight, width - 1, 2);
        }

        g2.dispose();
        super.paintComponent(g);
    }
}

