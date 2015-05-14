package com.purplecat.commons.swing;

import java.awt.Component;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class EnablableTableCellRenderer extends DefaultTableCellRenderer {
	@Override
    public Component getTableCellRendererComponent(JTable tab, Object val, boolean isSelected, boolean hasFocus, int row, int col)  
    {  
		setEnabled( tab.isEnabled() );
		return super.getTableCellRendererComponent(tab, val, isSelected, hasFocus, row, col);  
    }  
}  
