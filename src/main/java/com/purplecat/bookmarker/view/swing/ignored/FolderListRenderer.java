package com.purplecat.bookmarker.view.swing.ignored;

import java.awt.Font;

import com.purplecat.commons.swing.EnablableTableCellRenderer;
import com.purplecat.bookmarks.model.BookmarkFolder;

public class FolderListRenderer extends EnablableTableCellRenderer {
	@Override
	public void setValue(Object value) {
		String text = "";
		
		if ( value != null && value instanceof BookmarkFolder ) {
			BookmarkFolder folder = (BookmarkFolder)value;
			text = folder.getName();
			if ( folder.getUpdatedCount() > 0 ) {
				text += " (" + folder.getUpdatedCount() + ")";
				setFont(getFont().deriveFont(Font.BOLD));
			}
		}
		
		setText(text);
	}
}
