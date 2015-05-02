package com.purplecat.bookmarker.view.swing;

import java.util.LinkedList;
import java.util.List;

import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.commons.swing.TTable.TAbstractTableModel;

public class MediaTableModel extends TAbstractTableModel<Media> {
	List<Media> _backingList = new LinkedList<Media>();

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
		String columnName = "";
		switch ( columnIndex ) {
		case 0: columnName = "id";	break;
		case 1: columnName = "displayTitle"; break;
		case 2: columnName = "lastReadDate"; break;
		case 3: columnName = "lastReadPlace"; break;
		}
		return columnName;
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Media item = _backingList.get(rowIndex);
		Object obj = "";
		switch ( columnIndex ) {
		case 0: obj = item._id;	break;
		case 1: obj = item._displayTitle; break;
		case 2: obj = item._lastReadDate; break;
		case 3: obj = item._lastReadPlace; break;
		}
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