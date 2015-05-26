package com.purplecat.commons.swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPopupMenu;
import javax.swing.JTable;

public class TablePopupCreator extends MouseAdapter {
	private JTable mTable;
	private JPopupMenu mMenu;
	
	public TablePopupCreator(JTable table, JPopupMenu menu) {
		mTable = table;
		mMenu = menu;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		showPopup(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		showPopup(e);
	}
	
	private void showPopup(MouseEvent e) {
		if ( mMenu != null && e.isPopupTrigger() ) {
			int rowSelected = mTable.getSelectedRow(); 
			int rowUnderPoint = mTable.rowAtPoint(e.getPoint());
			if ( rowSelected != rowUnderPoint ) {
				if ( rowUnderPoint >= 0 && rowUnderPoint < mTable.getRowCount() ) {
					mTable.setRowSelectionInterval(rowUnderPoint, rowUnderPoint);
				}
				else {
					mTable.clearSelection();
				}
			}
			
			mMenu.show(mTable, e.getPoint().x, e.getPoint().y);
		}		
	}	
}