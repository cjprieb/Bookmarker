package com.purplecat.bookmarker.view.swing.html;

import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JSeparator;

import com.purplecat.bookmarker.view.swing.DefaultColors;

public class SummaryRow {
	public final JLabel 		_label;
	public final JSeparator 	_separator;
	
	public SummaryRow(String label) {
		_label = new JLabel(label);
		_label.setForeground(DefaultColors.LABEL_COLOR);
		_separator = new JSeparator();
	}
	
	public Component getDataComponent() {
		return null;
	}
	
	public void setVisible(boolean b) {
		_label.setVisible(b);
		_separator.setVisible(b);
		if ( getDataComponent()  != null ) {
			getDataComponent().setVisible(b);
		}
	}
}
