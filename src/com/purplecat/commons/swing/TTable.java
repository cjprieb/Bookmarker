package com.purplecat.commons.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import com.purplecat.commons.Point;
import com.purplecat.commons.swing.IRowActionListener.ClickType;
import com.purplecat.commons.swing.IRowActionListener.RowClickedEvent;
import com.purplecat.commons.swing.IRowSelectionListener.RowSelectionEvent;

public class TTable<T> extends JTable {	
	protected TTableModel<T> 	mTemplateModel 	= null;
	protected int				mHighlightRow	= -1;
//	protected Color				mHighlightColor	= null;

	public TTable() {		
		new TableMouseListener(); 
		this.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
	}

	@Override
	public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
		Component c = super.prepareRenderer(renderer, row, column);
		if ( !this.isPaintingForPrint() ) {				
			if ( mHighlightRow == row ) {
				c.setBackground(Color.green);
			}
			else if ( !this.isRowSelected(row) ) {			
				String bgColorId = ( row % 2 == 0 ? AwtColor.TABLE_BACKGROUND : AwtColor.TABLE_BACKGROUND_GRID ); 
				c.setBackground(UIManager.getColor(bgColorId));	
			}
		}
		return(c);
	}	
	
	/*
	 * RowSelectionListener methods
	 */
	public void addRowSelectionListener(IRowSelectionListener<T> l) {
		listenerList.add(IRowSelectionListener.class, l);
	}

	public void removeRowSelectionListener(IRowSelectionListener<T> l) {
		listenerList.remove(IRowSelectionListener.class, l);
	}
	
	/*
	 * TableModelListener methods
	 */
	public void addTableModelListener(TableModelListener l) {
		listenerList.add(TableModelListener.class, l);
	}

	public void removeTableModelListener(TableModelListener l) {
		listenerList.remove(TableModelListener.class, l);
	}
	
	public void addRowActionListener(IRowActionListener<T> l) {
		listenerList.add(IRowActionListener.class, l);
	}

	public void removeRowActionListener(IRowActionListener<T> l) {
		listenerList.remove(IRowActionListener.class, l);
	}
	
	public void setHighlightRow(int row) {
		mHighlightRow = row;
		repaint();
	}
	
	public int getHighlightRow() {
		return(mHighlightRow);
	}
	
	@Override
	public void setModel(TableModel model) {
		if ( model instanceof TTableModel ) {
			throw new IllegalArgumentException("TTableModel's must be set with setTemplateModel");
		}
		super.setModel(model);
		mTemplateModel = null;
	}
	
	public void setTemplateModel(TTableModel<T> model) {
		super.setModel(model);
		mTemplateModel = model;
	}
	
	@Override
	public void tableChanged(TableModelEvent e) {
		super.tableChanged(e);
		for ( TableModelListener l : listenerList.getListeners(TableModelListener.class) ) {
			l.tableChanged(e);
		}
	}
	
	public T getSelectedItem() {
		int row = getSelectedRow();
		if ( row >= 0 ) {
			row = convertRowIndexToModel(row);
		}
		if ( row >= 0 && row < mTemplateModel.getRowCount() && mTemplateModel != null ) {
			return(mTemplateModel.getItemAt(row));
		}
		return(null);
	}
	
	public void fireSelectionEvent() {
		RowSelectionEvent<T> selectionEvent = new RowSelectionEvent<T>(this, true);
		for ( IRowSelectionListener<T> listener : listenerList.getListeners(IRowSelectionListener.class) ) {
			listener.rowSelected(selectionEvent);
		}
	}
	
	public void setSelectedRow(int viewRow) {
		if ( viewRow >= 0 && viewRow < getRowCount() ) {
			this.setRowSelectionInterval(viewRow, viewRow);
			Rectangle rect = this.getCellRect(viewRow, 0, false);
			this.scrollRectToVisible(rect);
		}
		else {
			this.clearSelection();
		}
	}
	
	public void setSelectedItem(T item) {
		int modelRow = -1;
		if ( item != null ) {
			modelRow = mTemplateModel.indexOf(item);
		}

		if ( modelRow >= 0 ) {
			int row = convertRowIndexToView(modelRow);
			if ( row >= 0 ) {
				setSelectedRow(row);
			}
		} 		
	}

	protected class TableMouseListener extends MouseAdapter implements ListSelectionListener  {
		private boolean 			mWaitForMouseEvent 	= false;
		
		public TableMouseListener() {
			addMouseListener(this);
			getSelectionModel().addListSelectionListener(this);
		}
		
		@Override
		public void valueChanged(ListSelectionEvent e) {
			//So far (Jan 7 2012), testing shows that e.getValueIsAdjusting is only true on a mouse-click
			//	so to avoid duplicating a mouse event on a list selection change event:
			if ( !mWaitForMouseEvent ) {
				mWaitForMouseEvent = e.getValueIsAdjusting();
			}
				
			if ( !e.getValueIsAdjusting() ) {
				fireSelectionEvent();
			}
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if ( mWaitForMouseEvent ) {
				mWaitForMouseEvent = false;
			}
			else {
				int row = getSelectedRow();
				int column = columnAtPoint(e.getPoint());
				
				if ( row >= 0 ) { row = convertRowIndexToModel(row); }
				if ( column >= 0 ) { column = convertColumnIndexToModel(column); }
				ClickType clickType = ClickType.NONE;
				
				if ( e.isPopupTrigger() ) 			{ clickType = ClickType.POPUP_CLICK;  }
				else if ( e.getClickCount() == 1 ) 	{ clickType = ClickType.SINGLE_CLICK; }
				else if ( e.getClickCount() == 2 ) 	{ clickType = ClickType.DOUBLE_CLICK; }
				
				Point cPoint = new Point(e.getPoint().x, e.getPoint().y);
				
//				if ( clickType == ClickType.DOUBLE_CLICK ) {
//					Log.logMessage(0, "Mouse Clicked in Table");
//					Log.logMessage(1, String.format("point: (%d, %d)", cPoint.x, cPoint.y));
//					Log.logMessage(1, String.format("click count: %d", e.getClickCount()));
//					Log.logMessage(1, String.format("is popup: %b", e.isPopupTrigger()));
//					Log.logMessage(1, String.format("button: %d", e.getButton()));
//					Log.logMessage(1, String.format("modifiers: %d", e.getModifiers()));
//				}
				
				RowClickedEvent<T> clickEvent = new RowClickedEvent<T>(TTable.this, row, column, clickType, cPoint); //isCurrView = true;
				for ( IRowActionListener<T> listener : listenerList.getListeners(IRowActionListener.class) ) {
					listener.rowClicked(clickEvent);
				}
			}
		}
	}
	
	public static interface TTableModel<T> extends TableModel {
		public T getItemAt(int row);
		public int indexOf(T item);
	}
	
	public static abstract class TAbstractTableModel<T> extends AbstractTableModel implements TTableModel<T> {

	}
}
