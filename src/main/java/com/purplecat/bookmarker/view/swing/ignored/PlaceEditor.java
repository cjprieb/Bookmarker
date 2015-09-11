package com.purplecat.bookmarker.view.swing.ignored;

import java.awt.Component;
import java.text.ParseException;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;

import com.purplecat.bookmarks.model.Place;
import com.purplecat.bookmarks.model.styles.PlaceFormatter;

public class PlaceEditor extends DefaultCellEditor {
	private PlaceFormatter mFormatter = new PlaceFormatter(true);
	
	public PlaceEditor() {
		super(new JTextField());
	}
	@Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		if ( value == null ) {
			value = mFormatter.valueToString(new Place());			
		}
		else if ( value instanceof Place ) {
			value = mFormatter.valueToString((Place)value);
		}
		return(super.getTableCellEditorComponent(table, value, isSelected, row, column));
	}	
	
	@Override
	public Object getCellEditorValue() {
		String value = super.getCellEditorValue().toString();
		Place place = null;
		try {
			place = mFormatter.stringToValue(value);
		} catch (ParseException e) {}
		return(place);
	}
}
