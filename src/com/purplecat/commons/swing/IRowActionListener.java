package com.purplecat.commons.swing;

import java.util.EventListener;

import com.purplecat.commons.Point;

public interface IRowActionListener<T> extends EventListener {
	public void rowClicked(RowClickedEvent<T> e);
	
	public static class RowClickedEvent<T> {
		TTable<T> mTable = null;
		int mAdjustedColumn;
		int mAdjustedRow;
		ClickType mClickType;
		Point mPoint;
		
		public RowClickedEvent(TTable<T> table, int row, int column, ClickType clickType, Point clickAtPoint) {
			mAdjustedColumn = column;
			mAdjustedRow = row;
			mClickType = clickType;
			mPoint = clickAtPoint;
			mTable = table;
		}
		
		public TTable<T> getTable() {
			return(mTable);
		}
		
		public ClickType getClickType() {
			return(mClickType);
		}
		
		public Point getClickPoint() {
			return(mPoint);
		}
		
		public int getSelectedColumn() {
			return(mAdjustedColumn);
		}
		
		public int getSelectedRow() {
			return( mAdjustedRow );
		}
	}
	
	public enum ClickType { DOUBLE_CLICK, SINGLE_CLICK, POPUP_CLICK, NONE }

}
