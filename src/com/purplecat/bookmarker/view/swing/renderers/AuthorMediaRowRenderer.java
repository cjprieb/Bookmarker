package com.purplecat.bookmarker.view.swing.renderers;

import java.awt.Component;

import com.purplecat.bookmarker.models.Media;
import com.purplecat.commons.swing.AwtColor;
import com.purplecat.commons.swing.TTable;
import com.purplecat.commons.swing.renderer.ITableRowRenderer;

public class AuthorMediaRowRenderer implements ITableRowRenderer<Media> {
	@Override
	public void renderRow(TTable<Media> table, Component component, Media view, int row, int column) {
		int iCount = 0;
		boolean isOdd = (row % 2 == 1);
//TODO: author renderer
//		if ( view.getAuthor() != null ) {
//			for ( int i = 0; i < mModel.getRowCount(); i++ ) {
//				Author author = mModel.getItemAt(i).getAuthor();
//				if ( author != null && author.getRowId() == view.getAuthor().getRowId() ) {
//					iCount++;
//				}
//			}
//		}
		if ( iCount > 1 ) {				
			component.setBackground(isOdd ? AwtColor.BLUE_ODD_COLOR : AwtColor.BLUE_EVEN_COLOR);
		}
		else if ( iCount == 0 ) {
			component.setBackground(isOdd ? AwtColor.RED_ODD_COLOR : AwtColor.RED_EVEN_COLOR);					
		}
	}
}
