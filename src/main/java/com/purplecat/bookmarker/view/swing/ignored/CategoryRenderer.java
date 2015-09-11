package com.purplecat.bookmarker.view.swing.ignored;

import java.awt.Font;

import com.purplecat.commons.swing.EnablableTableCellRenderer;
import com.purplecat.bookmarks.model.Category;

public class CategoryRenderer extends EnablableTableCellRenderer {
	@Override
	public void setValue(Object value) {
		String text = "";
		
		if ( value != null && value instanceof Category ) {
			Category cat = (Category)value;
			if ( cat.getCount() > 0 ) {
				text = String.format("(%d) %s", cat.getCount(), cat.getName());
			}
			else {
				text = cat.getName();
			}
			if ( cat.getUpdatedCount() > 0 ) {
				setFont(getFont().deriveFont(Font.BOLD));
			}
		}
		else if ( value != null ) {
			text = value.toString();
		}
		
		setText(text);
	}
}
