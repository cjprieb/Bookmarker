package com.purplecat.bookmarker.view.swing.renderers;

import java.awt.Component;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

public class CalendarEditor extends DefaultCellEditor {
	private DateFormat mFormatter;
	
	public CalendarEditor() {
		super(new JTextField());		
		mFormatter = DateFormat.getDateInstance();
	}
	
	public CalendarEditor(String format) {
		super(new JTextField());		
		mFormatter = new SimpleDateFormat(format);
	}

	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {		
		if ( value instanceof Calendar ) {
			value = mFormatter.format(((Calendar)value).getTime());
		}
		return(super.getTableCellEditorComponent(table, value, isSelected, row, column));
	}	
	
	@Override
	public Object getCellEditorValue() {
		String value = super.getCellEditorValue().toString();
		Calendar c = null;
		try {
			Date d = mFormatter.parse(value);
			c = Calendar.getInstance();
			c.setTime(d);
		} catch (ParseException e) {}
		return(c);
	}
}
