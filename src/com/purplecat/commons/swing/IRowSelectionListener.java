package com.purplecat.commons.swing;

import java.util.EventListener;

public interface IRowSelectionListener<T> extends EventListener {
	public void rowSelected(RowSelectionEvent<T> e);
	
	public static class RowSelectionEvent<T> {
		TTable<T> mTable;
		int mAdjustedColumn;
		int mAdjustedRow;
		boolean mIsCurrentView;
		
		public RowSelectionEvent(TTable<T> table, boolean isCurrentView) {
			mTable = table;
			mIsCurrentView = isCurrentView;
		}
		
		public int getSelectedColumn() {
			return(mAdjustedColumn);
		}
		
		public int getSelectedRow() {
			return( mAdjustedRow );
		}
		
		public boolean isCurrentView() {
			return(mIsCurrentView);
		}
		
		public TTable<T> getTable() {
			return(mTable);
		}
	}
}
