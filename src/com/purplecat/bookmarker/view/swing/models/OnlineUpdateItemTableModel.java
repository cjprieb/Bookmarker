package com.purplecat.bookmarker.view.swing.models;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.view.swing.renderers.DataFields;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.TTableColumn;
import com.purplecat.commons.swing.TTable.TAbstractTableModel;

public class OnlineUpdateItemTableModel extends TAbstractTableModel<OnlineMediaItem> {
	List<OnlineMediaItem> _backingList = new LinkedList<OnlineMediaItem>();
	TTableColumn[] _columns;
	IResourceService _resources;
	
	public OnlineUpdateItemTableModel(TTableColumn[] columns, IResourceService resources) {
		_columns = columns;
		_resources = resources;
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
		return _resources.getString(_columns[columnIndex].getNameId());
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
		if ( column == DataFields.TITLE_COL ) { obj = item._displayTitle; }
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
			OnlineUpdateItemTableModel.this.fireTableRowsInserted(size, size);
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
			OnlineUpdateItemTableModel.this.fireTableRowsUpdated(iIndex, iIndex);
		}
		else {
			iIndex = _backingList.size();
			_backingList.add(updatedItem);
			OnlineUpdateItemTableModel.this.fireTableRowsInserted(iIndex, iIndex);
		}
	}
	
	private void updateItem(Media item) {
		int iIndex = 0;
		for ( OnlineMediaItem onlineItem : _backingList ) {
			if ( onlineItem._mediaId == item._id ) {
				onlineItem.updateFrom(item);
				OnlineUpdateItemTableModel.this.fireTableRowsUpdated(iIndex, iIndex);
				break;
			}
			iIndex++;
		}
	}
	
	public OnlineMediaListObserver getObserver() {
		return new OnlineMediaListObserver();
	}
	
	public class OnlineMediaListObserver implements IItemChangedObserver<Media>, IWebsiteLoadObserver {
		@Override
		public void notifyItemUpdated(Media item) {
			updateItem(item);
		}
		
		@Override
		public void notifyLoadStarted() {}

		@Override
		public void notifySiteStarted(WebsiteInfo site) {
			updateSite(site, false);
		}

		@Override
		public void notifySiteParsed(WebsiteInfo site, int itemsFound) {
			updateSite(site, true);
		}

		@Override
		public void notifyItemParsed(OnlineMediaItem item, int itemsParsed, int updateCount) {
			updateItem(item);
		}

		@Override
		public void notifySiteFinished(WebsiteInfo site) {}

		@Override
		public void notifyLoadFinished(List<OnlineMediaItem> list) {
		}		
	}
	
}