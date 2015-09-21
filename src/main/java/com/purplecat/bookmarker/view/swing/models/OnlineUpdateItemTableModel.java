package com.purplecat.bookmarker.view.swing.models;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.FolderCache;
import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.controller.observers.ISummaryLoadObserver;
import com.purplecat.bookmarker.models.Folder;
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
	FolderCache _folderCache;
	
	@Inject
	public OnlineUpdateItemTableModel(Controller ctrl, IResourceService resources, FolderCache folders) {
		_resources = resources;
		_folderCache = folders;
		
		OnlineMediaListObserver observer = new OnlineMediaListObserver();
		ctrl.observeOnlineThreadLoading(observer);
		ctrl.observeSavedMediaUpdate(observer);
		ctrl.observeSummaryLoading(observer);
	}
	
	public void setColumns(TTableColumn[] columns) {
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
	
	private void updateSite(String siteName, String siteUrl, boolean updateDate) {
		boolean bFound = false;
		for ( OnlineMediaItem item : _backingList ) {
			if ( item._displayTitle.equals(siteName) ) {
				bFound = true;
				item._updatedDate = updateDate ? new DateTime() : null;
				break;
			}				
		}
		if ( !bFound ) {
			OnlineMediaItem siteItem = new OnlineMediaItem();
			siteItem._displayTitle = siteName;
			siteItem._websiteName = siteName;
			if ( siteUrl != null ) {
				siteItem._titleUrl = siteUrl;
			}
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
				//don't break - there may be more than one match
			}
			iIndex++;
		}
	}
	
	private void removeItem(OnlineMediaItem updatedItem) {
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
			_backingList.remove(iIndex);
			OnlineUpdateItemTableModel.this.fireTableRowsDeleted(iIndex, iIndex);
		}
	}

	public void removeItemsOlderThan(int hoursAgo, String siteName) {
		DateTime oldestDate = DateTime.now().minusHours(hoursAgo);
		_backingList.removeIf(item -> {
			Folder folder = _folderCache.getById(item._folderId);
			if ( item._websiteName.equals(siteName) && item._id > 0 && ( item.isRead() || 
				!item._isSaved ||
				(folder != null && folder.ignoreUpdate()) ) ) {
				return item._updatedDate.isBefore(oldestDate);
			}
			return false;
		});
		fireTableDataChanged();
	}
	
	public class OnlineMediaListObserver implements IItemChangedObserver<Media>, IWebsiteLoadObserver, ISummaryLoadObserver {
		@Override
		public void notifyItemUpdated(Media item) {
//			System.out.println("notifying updates that media item has been updated: " + item);
			updateItem(item);
		}
		
		@Override
		public void notifyLoadStarted() {}

		@Override
		public void notifySiteStarted(String siteName, String siteUrl) {
			updateSite(siteName, siteUrl, false);
		}

		@Override
		public void notifySiteParsed(String siteName, int itemsFound) {
			updateSite(siteName, null, true);
		}

		@Override
		public void notifyItemParsed(OnlineMediaItem item, int itemsParsed, int updateCount) {
			updateItem(item);
		}
		
		@Override
		public void notifyItemRemoved(OnlineMediaItem item, int itemsParsed, int itemsUpdated) {
			removeItem(item);
		}

		@Override
		public void notifySiteFinished(String siteName) {}

		@Override
		public void notifyLoadFinished(List<OnlineMediaItem> list) {
		}		

		@Override
		public void notifySummaryLoadStarted(long mediaId) {}

		@Override
		public void notifySummaryLoadFinished(OnlineMediaItem item) {
			if ( item != null ) {
				updateItem(item);
			}
		}	
	}	
}