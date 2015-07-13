package com.purplecat.bookmarker.view.swing.models;

import java.util.LinkedList;
import java.util.List;

import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.controller.observers.ISummaryLoadObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.view.swing.renderers.DataFields;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.TTableColumn;
import com.purplecat.commons.swing.TTable.TAbstractTableModel;

public class SavedMediaTableModel extends TAbstractTableModel<Media> {
	List<Media> _backingList = new LinkedList<Media>();
	TTableColumn[] _columns;
	IResourceService _resources;
	
	public SavedMediaTableModel(TTableColumn[] columns, Controller ctrl, IResourceService resources) {
		_columns = columns;
		_resources = resources;

		MediaListObserver observer = new MediaListObserver();
		ctrl.observeSavedMediaLoading(observer);
		ctrl.observeOnlineThreadLoading(observer);
		ctrl.observeSavedMediaUpdate(observer);
		ctrl.observeSummaryLoading(observer);
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
		Media item = _backingList.get(rowIndex);
		Object obj = "";
		
		TTableColumn column = _columns[columnIndex];
		if ( column == DataFields.TITLE_COL ) { obj = item.getDisplayTitle(); }
		else if ( column == DataFields.DATE_COL ) { obj = item._lastReadDate; }
		else if ( column == DataFields.PLACE_COL ) { obj = item._lastReadPlace; }
		else if ( column == DataFields.FAVORITE_COL ) { obj = item._rating; }
		//else if ( column == DataFields.FLAG_COL ) { obj = item._isFlagged; }
		else if ( column == DataFields.MEDIA_STATE_COL ) { obj = item; }
		
		return obj;
	}
	
	protected void updateItem(Media item) {
		int iIndex = 0;
		for ( Media existingItem : _backingList ) {
			if ( existingItem._id == item._id ) {
				_backingList.set(iIndex, item);
				SavedMediaTableModel.this.fireTableRowsUpdated(iIndex, iIndex);
				break;
			}
			iIndex++;
		}		
	}
	
	protected void updateItem(OnlineMediaItem item) {
		int iIndex = 0;
		for ( Media existingItem : _backingList ) {
			if ( existingItem._id == item._mediaId ) {
				existingItem.updateFrom(item);
				SavedMediaTableModel.this.fireTableRowsUpdated(iIndex, iIndex);
				break;
			}
			iIndex++;
		}
	}
	
	public class MediaListObserver implements IItemChangedObserver<Media>, IListLoadedObserver<Media>, IWebsiteLoadObserver, ISummaryLoadObserver {
		@Override
		public void notifyListLoaded(List<Media> list) {
			_backingList.clear();
			_backingList.addAll(list);
			SavedMediaTableModel.this.fireTableDataChanged();
		}

		@Override
		public void notifyItemLoaded(Media item, int index, int total) {
			updateItem(item);
		}
		
		@Override
		public void notifyItemUpdated(Media item) {
			updateItem(item);
		}
		
		@Override
		public void notifyLoadStarted() {}

		@Override
		public void notifySiteStarted(WebsiteInfo site) {}

		@Override
		public void notifySiteParsed(WebsiteInfo site, int itemsFound) {}

		@Override
		public void notifyItemParsed(OnlineMediaItem item, int itemsParsed, int updateCount) {
			updateItem(item);
		}

		@Override
		public void notifySiteFinished(WebsiteInfo site) {}

		@Override
		public void notifyLoadFinished(List<OnlineMediaItem> list) {
			//Filter by Updated?
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