
package com.bloodconnect.ui.components;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;

public class ModernTable extends JTable {

    public ModernTable(TableModel model) {
        super(model);
        initModernTable();
    }

    public ModernTable() {
        super();
        initModernTable();
    }

    private void initModernTable() {

        setRowHeight(38);
        setFont(UIConstants.FONT_REGULAR);
        setShowGrid(true);
        setGridColor(UIConstants.COLOR_BORDER);
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setSelectionBackground(UIConstants.COLOR_PRIMARY_LIGHT);
        setSelectionForeground(UIConstants.COLOR_TEXT_PRIMARY);
        setFillsViewportHeight(true);

        // Configure table header
        JTableHeader header = getTableHeader();

        header.setFont(UIConstants.FONT_HEADER);
        header.setBackground(UIConstants.COLOR_SIDEBAR_BG);
        header.setForeground(Color.WHITE);
        header.setOpaque(true);
        header.setPreferredSize(new Dimension(0, 42));
        header.setReorderingAllowed(false);

        // Explicit custom header renderer
        DefaultTableCellRenderer headerRenderer =
                new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {

                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                label.setOpaque(true);
                label.setBackground(UIConstants.COLOR_SIDEBAR_BG);
                label.setForeground(Color.WHITE);
                label.setFont(UIConstants.FONT_HEADER);
                label.setHorizontalAlignment(JLabel.CENTER);

                label.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(
                                0, 0, 1, 1,
                                new Color(75, 90, 110)),
                        BorderFactory.createEmptyBorder(8, 6, 8, 6)
                ));

                return label;
            }
        };

        header.setDefaultRenderer(headerRenderer);

        // Alternating row renderer
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {

            @Override
            public Component getTableCellRendererComponent(
                    JTable table,
                    Object value,
                    boolean isSelected,
                    boolean hasFocus,
                    int row,
                    int column) {

                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    if (row % 2 == 0) {
                        c.setBackground(Color.WHITE);
                    } else {
                        c.setBackground(UIConstants.COLOR_BACKGROUND);
                    }

                    c.setForeground(UIConstants.COLOR_TEXT_PRIMARY);

                } else {
                    c.setBackground(UIConstants.COLOR_PRIMARY_LIGHT);
                    c.setForeground(UIConstants.COLOR_PRIMARY_HOVER);
                }

                if (c instanceof JLabel) {
                    ((JLabel) c).setBorder(
                            BorderFactory.createEmptyBorder(0, 10, 0, 10)
                    );
                }

                return c;
            }
        });
    }
}
