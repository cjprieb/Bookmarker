package com.purplecat.bookmarker.view.swing.ignored;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.purplecat.bookmarks.model.BookmarkFolder;

public class FolderComboBoxRenderer extends BasicComboBoxEditor implements ListCellRenderer<BookmarkFolder> {
	BasicComboBoxRenderer mRenderer = new BasicComboBoxRenderer();

	@Override
	public Component getListCellRendererComponent(JList<? extends BookmarkFolder> list, BookmarkFolder folder, int index, boolean isSelected, boolean hasFocus) {
		Object value = folder;
		if ( folder.getRowId() == BookmarkFolder.ALL_ITEMS_ID ||
				folder.getRowId() == BookmarkFolder.DEFAULT_ID ) {
			value = "";
		}
		return(mRenderer.getListCellRendererComponent(list, value, index, isSelected, hasFocus));
	}
//	
	@Override
	public void setItem(Object obj) {
		if ( obj instanceof BookmarkFolder ) {
			if ( ((BookmarkFolder)obj).getRowId() == BookmarkFolder.DEFAULT_ID ) { 
				obj = "";
			}
		}
		super.setItem(obj);
	}
}
