package com.purplecat.bookmarker.view.swing;

import java.util.LinkedList;
import java.util.List;

import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.view.swing.renderers.DataFields;
import com.purplecat.commons.TTableColumn;
import com.purplecat.commons.swing.TTable.TAbstractTableModel;

public class MediaTableModel extends TAbstractTableModel<Media> {
	List<Media> _backingList = new LinkedList<Media>();
	TTableColumn[] _columns;
	
	public MediaTableModel(TTableColumn[] columns) {
		_columns = columns;
	}
	
	@Override
	public TTableColumn[] getColumns() {
		return _columns;
	}

	@Override
	public Media getItemAt(int row) {
		return _backingList.get(row);
	}

	@Override
	public int indexOf(Media item) {
		int iCount = 0;
		for( Media existing : _backingList ) {
			if ( existing._id == item._id ) {
				return iCount;
			}
			iCount++;
		}
		return -1;
	}

	@Override
	public int getRowCount() {
		return _backingList.size();
	}

	@Override
	public String getColumnName(int columnIndex) {
		return _columns[columnIndex].getName();
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		return _columns[columnIndex].getClassType();
	}

	@Override
	public int getColumnCount() {
		return _columns.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Media item = _backingList.get(rowIndex);
		Object obj = "";
		
		TTableColumn column = _columns[columnIndex];
		if ( column.getField().equals("_id") ) { obj = item._id; }
		else if ( column == DataFields.TITLE_COL ) { obj = item._displayTitle; }
		else if ( column == DataFields.DATE_COL ) { obj = item._lastReadDate; }
		else if ( column == DataFields.PLACE_COL ) { obj = item._lastReadPlace; }
		else if ( column == DataFields.FAVORITE_COL ) { obj = item._rating; }
		//else if ( column == DataFields.FLAG_COL ) { obj = item._isFlagged; }
		else if ( column == DataFields.MEDIA_STATE_COL ) { obj = item; }
		
		return obj;
	}
	
	public MediaListObserver getObserver() {
		return new MediaListObserver();
	}
	
	public class MediaListObserver implements IListLoadedObserver<Media> {
		@Override
		public void notifyListLoaded(List<Media> list) {
			_backingList.clear();
			_backingList.addAll(list);
			MediaTableModel.this.fireTableDataChanged();
		}		
	}
	
}