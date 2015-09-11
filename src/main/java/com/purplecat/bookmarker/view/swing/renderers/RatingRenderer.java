package com.purplecat.bookmarker.view.swing.renderers;
import com.purplecat.commons.swing.EnablableTableCellRenderer;

public class RatingRenderer extends EnablableTableCellRenderer {
	
	public RatingRenderer() { 
		super(); 
	}
	
	@Override
	public void setValue(Object value) {
		StringBuilder buf = new StringBuilder(16);
		if ( value instanceof Double ) {
			double d = (Double)value;
			if ( d >= 0 ) {
				d = d * 10.0;
				buf.append(String.format("%.2f", d));
				buf.append("/10.00");
			}
		}
		setText(buf.toString());
	}
}
