package com.purplecat.bookmarker.view.swing;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.view.swing.renderers.DataFields;
import com.purplecat.commons.TTableColumn;
import com.purplecat.commons.swing.TTable.TAbstractTableModel;

public class UpdateMediaTableModel extends TAbstractTableModel<OnlineMediaItem> {
	List<OnlineMediaItem> _backingList = new LinkedList<OnlineMediaItem>();
	TTableColumn[] _columns;
	
	public UpdateMediaTableModel(TTableColumn[] columns) {
		_columns = columns;
	}
	
	@Override
	public TTableColumn[] getColumns() {
		return _columns;
	}

	@Override
	public OnlineMediaItem getItemAt(int row) {
		return _backingList.get(row);
	}

	@Override
	public int indexOf(OnlineMediaItem item) {
		int iCount = 0;
		for( OnlineMediaItem existing : _backingList ) {
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
		OnlineMediaItem item = _backingList.get(rowIndex);
		Object obj = "";
		
		TTableColumn column = _columns[columnIndex];
		if ( column.getField().equals("_id") ) { obj = item._id; }
		else if ( column == DataFields.TITLE_COL ) { obj = item._displayTitle; }
		else if ( column == DataFields.TIME_COL ) { obj = item._updatedDate; }
		else if ( column == DataFields.PLACE_COL ) { obj = item._id > 0 ? item._updatedPlace : null; }
		else if ( column == DataFields.RATING_COL ) { obj = item._rating; }
		//else if ( column == DataFields.FLAG_COL ) { obj = item._isFlagged; }
		else if ( column == DataFields.ONLINE_STATE_COL ) { obj = item; }
		//else if ( column == DataFields.GENRES_COL ) { obj = item._genres; }
		
		return obj;
	}
	
	private void updateSite(WebsiteInfo site, boolean updateDate) {
		boolean bFound = false;
		for ( OnlineMediaItem item : _backingList ) {
			if ( item._displayTitle.equals(site._name) ) {					
				bFound = true;
				item._updatedDate = updateDate ? new DateTime() : null;
				break;
			}				
		}
		if ( !bFound ) {
			OnlineMediaItem siteItem = new OnlineMediaItem();
			siteItem._displayTitle = site._name;
			siteItem._websiteName = site._name;
			siteItem._titleUrl = site._website;
			int size = _backingList.size();
			_backingList.add(siteItem);
			UpdateMediaTableModel.this.fireTableRowsInserted(size, size);
		}
	}
	
	private void updateItem(OnlineMediaItem updatedItem) {
		boolean bFound = false;
		int iIndex = 0;
		for ( OnlineMediaItem item : _backingList ) {
			if ( item._id == updatedItem._id ) {					
				bFound = true;				
				break;
			}
			iIndex++;
		}
		if ( bFound ) {
			_backingList.set(iIndex, updatedItem);
		}
		else {
			iIndex = _backingList.size();
			_backingList.add(updatedItem);
		}
		UpdateMediaTableModel.this.fireTableRowsInserted(iIndex, iIndex);
	}
	
	public OnlineMediaListObserver getObserver() {
		return new OnlineMediaListObserver();
	}
	
	public class OnlineMediaListObserver implements IListLoadedObserver<OnlineMediaItem>, IWebsiteLoadObserver {
		@Override
		public void notifyListLoaded(List<OnlineMediaItem> list) {
			_backingList.clear();
			_backingList.addAll(list);
			UpdateMediaTableModel.this.fireTableDataChanged();
		}

		@Override
		public void notifyLoadStarted() {}

		@Override
		public void notifySiteStarted(WebsiteInfo site) {
			updateSite(site, false);
		}

		@Override
		public void notifySiteParsed(WebsiteInfo site) {
			updateSite(site, true);
		}

		@Override
		public void notifyItemParsed(OnlineMediaItem item) {
			updateItem(item);
		}

		@Override
		public void notifySiteFinished(WebsiteInfo site) {}

		@Override
		public void notifyLoadFinished(List<OnlineMediaItem> list) {
			// TODO Sort?			
		}		
	}
	
}