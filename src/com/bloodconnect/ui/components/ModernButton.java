package com.bloodconnect.ui.components;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * =========================================================================
 * Unit 3 Concept: Custom Swing Component Rendering & Mouse Event Handling
 * A modern styled button with rounded corners, hover transitions, and
 * semantic color palettes (Primary, Secondary, Success, Danger).
 * =========================================================================
 */
public class ModernButton extends JButton {

    public enum Style {
        PRIMARY, SECONDARY, SUCCESS, DANGER, OUTLINE
    }

    private final Style style;
    private boolean isHovered = false;
    private boolean isPressed = false;
    private int cornerRadius = 8;

    public ModernButton(String text) {
        this(text, Style.PRIMARY);
    }

    public ModernButton(String text, Style style) {
        super(text);
        this.style = style;
        initButton();
    }

    private void initButton() {
        setFont(UIConstants.FONT_BOLD);
        setFocusPainted(false);
        setBorderPainted(false);
        setContentAreaFilled(false);
        setOpaque(false);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(140, 38));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                isPressed = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                isPressed = false;
                repaint();
            }
        });
    }

    public void setCornerRadius(int radius) {
        this.cornerRadius = radius;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int width = getWidth();
        int height = getHeight();

        Color bg;
        Color fg;
        Color border = null;

        switch (style) {
            case PRIMARY:
                bg = isPressed ? UIConstants.COLOR_PRIMARY_HOVER.darker() :
                        (isHovered ? UIConstants.COLOR_PRIMARY_HOVER : UIConstants.COLOR_PRIMARY);
                fg = Color.WHITE;
                break;
            case SECONDARY:
                bg = isPressed ? UIConstants.COLOR_SIDEBAR_ACTIVE :
                        (isHovered ? UIConstants.COLOR_SIDEBAR_HOVER : UIConstants.COLOR_SIDEBAR_BG);
                fg = Color.WHITE;
                break;
            case SUCCESS:
                bg = isPressed ? UIConstants.COLOR_SUCCESS.darker() :
                        (isHovered ? new Color(13, 148, 136) : UIConstants.COLOR_SUCCESS);
                fg = Color.WHITE;
                break;
            case DANGER:
                bg = isPressed ? UIConstants.COLOR_DANGER.darker() :
                        (isHovered ? new Color(220, 38, 38) : UIConstants.COLOR_DANGER);
                fg = Color.WHITE;
                break;
            case OUTLINE:
            default:
                bg = isPressed ? new Color(241, 245, 249) :
                        (isHovered ? new Color(248, 250, 252) : Color.WHITE);
                fg = UIConstants.COLOR_TEXT_PRIMARY;
                border = UIConstants.COLOR_BORDER;
                break;
        }

        if (!isEnabled()) {
            bg = new Color(226, 232, 240);
            fg = new Color(148, 163, 184);
            border = null;
        }

        // Draw background
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, width, height, cornerRadius, cornerRadius);

        // Draw border if outline
        if (border != null) {
            g2.setColor(border);
            g2.drawRoundRect(0, 0, width - 1, height - 1, cornerRadius, cornerRadius);
        }

        // Draw text
        g2.setFont(getFont());
        g2.setColor(fg);
        java.awt.FontMetrics fm = g2.getFontMetrics();
        int textX = (width - fm.stringWidth(getText())) / 2;
        int textY = (height + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(getText(), textX, textY);

        g2.dispose();
    }
}

