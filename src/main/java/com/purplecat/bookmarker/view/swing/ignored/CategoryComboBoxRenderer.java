package com.purplecat.bookmarker.view.swing.ignored;

import java.awt.Component;

import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.plaf.basic.BasicComboBoxEditor;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import com.purplecat.bookmarks.model.Category;

public class CategoryComboBoxRenderer extends BasicComboBoxEditor.UIResource implements ListCellRenderer<Category> {
	BasicComboBoxRenderer mRenderer = new BasicComboBoxRenderer();
	Category mSelected = null;

	@Override
	public Component getListCellRendererComponent(JList<? extends Category> list, Category category, int index, boolean isSelected, boolean hasFocus) {
		Object value = category.getName();
		return(mRenderer.getListCellRendererComponent(list, value, index, isSelected, hasFocus));
	}
	
	@Override
	public Object getItem() {
		Object obj = super.getItem();
		if ( obj instanceof String && mSelected != null ) {
			if ( mSelected.getName().equals(obj.toString()) ) {
				obj = mSelected;
			}
		}
		return(obj);
	}
	
	@Override
	public void setItem(Object obj) {
		if ( obj instanceof Category ) {
			mSelected = (Category)obj;
			if ( ((Category)obj).getRowId() == Category.DEFAULT_KEY ) { 
				obj = "";
			}
			else {
				obj = ((Category) obj).getName();
			}
		}
		super.setItem(obj);
	}
}
