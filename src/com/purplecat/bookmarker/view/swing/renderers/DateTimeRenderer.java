package com.purplecat.bookmarker.view.swing.renderers;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.purplecat.commons.swing.EnablableTableCellRenderer;


public class DateTimeRenderer extends EnablableTableCellRenderer {
	private final DateTimeFormatter _formatter;
	
	public DateTimeRenderer(String format) { 
		_formatter = DateTimeFormat.forPattern(format);
	}
	
	@Override
	public void setValue(Object value) {
		if ( value != null && value instanceof DateTime ) {
			setText(((DateTime)value).toString(_formatter));			
		}
		else {
			setText("");
		}
	}

}
