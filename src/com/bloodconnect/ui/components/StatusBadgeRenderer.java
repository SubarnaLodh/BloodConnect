package com.bloodconnect.ui.components;

import com.bloodconnect.model.BloodGroup;
import com.bloodconnect.model.RequestStatus;
import com.bloodconnect.model.UrgencyLevel;

import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.RenderingHints;

/**
 * =========================================================================
 * Unit 3 Concept: Custom TableCellRenderer & Graphical Badge Painting
 * Renders status badges, urgency levels, blood groups, and eligibility tags
 * as rounded pill indicators within Swing JTables.
 * =========================================================================
 */
public class StatusBadgeRenderer extends JPanel implements TableCellRenderer {

    private String text = "";
    private Color badgeBg = UIConstants.COLOR_INFO_BG;
    private Color badgeFg = UIConstants.COLOR_INFO_TEXT;

    public StatusBadgeRenderer() {
        setLayout(new GridBagLayout());
        setOpaque(true);
        setFont(UIConstants.FONT_BADGE);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
                                                   boolean isSelected, boolean hasFocus,
                                                   int row, int column) {
        if (isSelected) {
            setBackground(UIConstants.COLOR_PRIMARY_LIGHT);
        } else {
            setBackground(row % 2 == 0 ? Color.WHITE : UIConstants.COLOR_BACKGROUND);
        }

        if (value == null) {
            text = "-";
            badgeBg = UIConstants.COLOR_BORDER;
            badgeFg = UIConstants.COLOR_TEXT_MUTED;
            return this;
        }

        if (value instanceof UrgencyLevel) {
            UrgencyLevel level = (UrgencyLevel) value;
            text = level.getDisplayName().toUpperCase();
            switch (level) {
                case CRITICAL:
                    badgeBg = UIConstants.COLOR_DANGER_BG;
                    badgeFg = UIConstants.COLOR_DANGER_TEXT;
                    break;
                case URGENT:
                    badgeBg = UIConstants.COLOR_WARNING_BG;
                    badgeFg = UIConstants.COLOR_WARNING_TEXT;
                    break;
                case ROUTINE:
                default:
                    badgeBg = UIConstants.COLOR_INFO_BG;
                    badgeFg = UIConstants.COLOR_INFO_TEXT;
                    break;
            }
        } else if (value instanceof RequestStatus) {
            RequestStatus status = (RequestStatus) value;
            text = status.getDisplayName().toUpperCase();
            switch (status) {
                case FULFILLED:
                    badgeBg = UIConstants.COLOR_SUCCESS_BG;
                    badgeFg = UIConstants.COLOR_SUCCESS_TEXT;
                    break;
                case MATCHED:
                    badgeBg = UIConstants.COLOR_INFO_BG;
                    badgeFg = UIConstants.COLOR_INFO_TEXT;
                    break;
                case PENDING:
                    badgeBg = UIConstants.COLOR_WARNING_BG;
                    badgeFg = UIConstants.COLOR_WARNING_TEXT;
                    break;
                case CANCELLED:
                default:
                    badgeBg = new Color(241, 245, 249);
                    badgeFg = UIConstants.COLOR_TEXT_SECONDARY;
                    break;
            }
        } else if (value instanceof BloodGroup) {
            BloodGroup bg = (BloodGroup) value;
            text = bg.getSymbol();
            badgeBg = UIConstants.COLOR_PRIMARY_LIGHT;
            badgeFg = UIConstants.COLOR_PRIMARY_HOVER;
        } else if (value instanceof Boolean) {
            boolean bool = (Boolean) value;
            text = bool ? "YES" : "NO";
            badgeBg = bool ? UIConstants.COLOR_SUCCESS_BG : UIConstants.COLOR_DANGER_BG;
            badgeFg = bool ? UIConstants.COLOR_SUCCESS_TEXT : UIConstants.COLOR_DANGER_TEXT;
        } else {
            String str = value.toString().trim();
            text = str.toUpperCase();
            if (str.equalsIgnoreCase("Available") || str.equalsIgnoreCase("Eligible") || str.equalsIgnoreCase("Exact Match")) {
                badgeBg = UIConstants.COLOR_SUCCESS_BG;
                badgeFg = UIConstants.COLOR_SUCCESS_TEXT;
            } else if (str.equalsIgnoreCase("Unavailable") || str.equalsIgnoreCase("Ineligible")) {
                badgeBg = UIConstants.COLOR_DANGER_BG;
                badgeFg = UIConstants.COLOR_DANGER_TEXT;
            } else if (str.equalsIgnoreCase("Compatible")) {
                badgeBg = UIConstants.COLOR_INFO_BG;
                badgeFg = UIConstants.COLOR_INFO_TEXT;
            } else {
                badgeBg = new Color(241, 245, 249);
                badgeFg = UIConstants.COLOR_TEXT_SECONDARY;
            }
        }

        return this;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setFont(getFont());
        FontMetrics fm = g2.getFontMetrics();
        int strWidth = fm.stringWidth(text);
        int badgeWidth = Math.max(strWidth + 20, 65);
        int badgeHeight = 22;

        int x = (getWidth() - badgeWidth) / 2;
        int y = (getHeight() - badgeHeight) / 2;

        // Draw pill background
        g2.setColor(badgeBg);
        g2.fillRoundRect(x, y, badgeWidth, badgeHeight, 12, 12);

        // Draw text
        g2.setColor(badgeFg);
        int textX = x + (badgeWidth - strWidth) / 2;
        int textY = y + (badgeHeight + fm.getAscent() - fm.getDescent()) / 2;
        g2.drawString(text, textX, textY);

        g2.dispose();
    }
}

