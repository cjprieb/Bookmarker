package com.purplecat.bookmarker.view.swing.renderers;

import com.purplecat.commons.swing.EnablableTableCellRenderer;
import com.purplecat.bookmarker.extensions.PlaceExt;
import com.purplecat.bookmarker.models.Place;

public class PlaceRenderer extends EnablableTableCellRenderer {
	
	public PlaceRenderer() { 
		super(); 
	}
	
	@Override
	public void setValue(Object value) {
		if ( value == null ) {
			setText("");			
		}
		else {
			setText(PlaceExt.render((Place)value));
		}
	}
}
