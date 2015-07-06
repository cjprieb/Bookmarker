package com.purplecat.bookmarker.view.swing.renderers;

import java.awt.Component;

import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.commons.swing.AwtColor;
import com.purplecat.commons.swing.TTable;
import com.purplecat.commons.swing.renderer.ITableRowRenderer;
import com.purplecat.commons.utils.StringUtils;

public class OnlineLoadedRowRenderer implements ITableRowRenderer<OnlineMediaItem> {
	@Override
	public void renderRow(TTable<OnlineMediaItem> table, Component component, OnlineMediaItem view, boolean isOdd) {
		//ITEM LOADED COLORS
		if ( !StringUtils.isNullOrEmpty(view._summary) ) {
			component.setBackground(isOdd ? AwtColor.BLUE_ODD_COLOR : AwtColor.BLUE_EVEN_COLOR);
		}
	}
}
