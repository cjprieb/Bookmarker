package com.purplecat.bookmarker.view.swing.renderers;

import java.awt.Component;

import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.view.swing.DefaultColors;
import com.purplecat.commons.TTableColumn;
import com.purplecat.commons.swing.AwtColor;
import com.purplecat.commons.swing.TTable;
import com.purplecat.commons.swing.renderer.ITableRowRenderer;
import com.purplecat.commons.utils.StringUtils;

public class OnlineLoadedRowRenderer implements ITableRowRenderer<OnlineMediaItem> {
	private TTableColumn[] _columns;
	
	public OnlineLoadedRowRenderer(TTableColumn[] columns) {
		_columns = columns;
	}
	
	
	@Override
	public void renderRow(TTable<OnlineMediaItem> table, Component component, OnlineMediaItem view, int row, int columnIndex) {
		//ITEM LOADED COLORS
		boolean isOdd = (row % 2 == 1);
		TTableColumn column = _columns.length > columnIndex ? _columns[columnIndex] : null;
		
		if ( !StringUtils.isNullOrEmpty(view._summary) && column == DataFields.PLACE_COL ) {
			component.setBackground(isOdd ? AwtColor.BLUE_ODD_COLOR : AwtColor.BLUE_EVEN_COLOR);
		}
		else if ( view._newlyAdded && column == DataFields.ONLINE_STATE_COL ) {
			component.setBackground(DefaultColors.HIGHTLIGHT_COLOR);			
		}
	}
}
