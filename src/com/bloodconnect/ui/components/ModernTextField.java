package com.bloodconnect.ui.components;

import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 * =========================================================================
 * Unit 3 Concept: Swing Text Input Components & Focus Listeners
 * A modern text field with placeholder text, focus state borders,
 * and error highlighting for form validation.
 * =========================================================================
 */
public class ModernTextField extends JTextField implements FocusListener {

    private String placeholder = "";
    private boolean isFocused = false;
    private boolean hasError = false;
    private int cornerRadius = 8;

    public ModernTextField() {
        this("", 15);
    }

    public ModernTextField(String placeholder) {
        this(placeholder, 15);
    }

    public ModernTextField(String placeholder, int columns) {
        super(columns);
        this.placeholder = placeholder;
        initField();
    }

    private void initField() {
        setFont(UIConstants.FONT_REGULAR);
        setForeground(UIConstants.COLOR_TEXT_PRIMARY);
        setBackground(Color.WHITE);
        setCaretColor(UIConstants.COLOR_PRIMARY);
        setOpaque(false);
        setBorder(new EmptyBorder(8, 12, 8, 12));
        setPreferredSize(new Dimension(200, 38));
        addFocusListener(this);
    }

    public void setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        repaint();
    }

    public void setError(boolean error) {
        this.hasError = error;
        repaint();
    }

    @Override
    public void focusGained(FocusEvent e) {
        isFocused = true;
        hasError = false;
        repaint();
    }

    @Override
    public void focusLost(FocusEvent e) {
        isFocused = false;
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

        // Border color based on state
        Color borderColor = UIConstants.COLOR_BORDER;
        if (hasError) {
            borderColor = UIConstants.COLOR_DANGER;
        } else if (isFocused) {
            borderColor = UIConstants.COLOR_BORDER_FOCUS;
        }

        g2.setColor(borderColor);
        g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);

        // Draw inner component text
        super.paintComponent(g);

        // Draw placeholder if empty and not focused
        if (getText().isEmpty() && !isFocused && placeholder != null && !placeholder.isEmpty()) {
            g2.setFont(getFont());
            g2.setColor(UIConstants.COLOR_TEXT_MUTED);
            java.awt.FontMetrics fm = g2.getFontMetrics();
            int x = getInsets().left;
            int y = (height + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(placeholder, x, y);
        }

        g2.dispose();
    }
}

