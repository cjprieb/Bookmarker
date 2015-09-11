package com.purplecat.bookmarker.view.swing.renderers;

import java.awt.Component;

import org.joda.time.DateTime;
import org.joda.time.Duration;

import com.purplecat.bookmarker.models.Media;
import com.purplecat.commons.swing.AwtColor;
import com.purplecat.commons.swing.TTable;
import com.purplecat.commons.swing.renderer.ITableRowRenderer;

public class UpdatedMediaRowRenderer implements ITableRowRenderer<Media> {
	@Override
	public void renderRow(TTable<Media> table, Component component, Media view, int row, int column) {
		//RECENT UPDATES HIGHLIGHTING
		boolean isOdd = (row % 2 == 1);
		if ( view.isUpdated() && view._updatedDate != null ) {
			Duration duration = new Duration(view._updatedDate, DateTime.now());
			if ( duration.getStandardHours() < 24 ) {
				component.setBackground(isOdd ? AwtColor.GREEN_ODD_COLOR : AwtColor.GREEN_EVEN_COLOR);						
			}
			else if ( duration.getStandardHours() < 72 ) {
				component.setBackground(isOdd ? AwtColor.PALE_GREEN_ODD_COLOR : AwtColor.PALE_GREEN_EVEN_COLOR);								
			}
		}
	}
}
