package com.purplecat.bookmarker.view.swing.observers;

import java.util.List;

import javax.swing.JPanel;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.controller.observers.ISummaryLoadObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.view.swing.panels.OnlineMediaSummaryPanel;
import com.purplecat.bookmarker.view.swing.panels.SummarySidebar;
import com.purplecat.commons.swing.IRowSelectionListener;

public class OnlineMediaSummaryObserver implements IRowSelectionListener<OnlineMediaItem>, IItemChangedObserver<OnlineMediaItem>, 
	IWebsiteLoadObserver, ISummaryLoadObserver {
	
	Controller _controller;
	OnlineMediaSummaryPanel _mediaSummaryPanel;
	SummarySidebar _parentSummaryPanel;
	
	OnlineMediaItem _currentMedia;
	
	@Inject
	public OnlineMediaSummaryObserver(Controller controller, OnlineMediaSummaryPanel mediaSummaryPanel, SummarySidebar parentSummaryPanel) {
		_controller = controller;
		_mediaSummaryPanel = mediaSummaryPanel;
		_parentSummaryPanel = parentSummaryPanel;
		
		_controller.observeSavedMediaUpdate(new IItemChangedObserver<Media>() {
			@Override
			public void notifyItemUpdated(Media item) {
				if ( _currentMedia != null && _currentMedia._mediaId == item._id ) {
					_currentMedia.updateFrom(item);
					_mediaSummaryPanel.update(_currentMedia);
				}
			}			
		});	
	}
	
	public JPanel getSummaryPanel() {
		return _mediaSummaryPanel.getPanel();
	}
	
	protected void updateItem(OnlineMediaItem item) {
		if ( _currentMedia._id == item._id ) {
			_currentMedia = item;
			_mediaSummaryPanel.update(_currentMedia);		
		}		
	}

	@Override
	public void rowSelected(RowSelectionEvent<OnlineMediaItem> e) {
		if ( _mediaSummaryPanel.getPanel() == null ) {
			_mediaSummaryPanel.create();
		}
		_currentMedia = e.getTable().getSelectedItem();
		if ( _currentMedia != null ) {
			_mediaSummaryPanel.update(_currentMedia);
		}
		_parentSummaryPanel.setSummaryView(_mediaSummaryPanel.getPanel());
		_parentSummaryPanel.setEditorView(null);
	}

	@Override
	public void notifyItemUpdated(OnlineMediaItem item) {
		updateItem(item);
	}

	@Override
	public void notifyLoadStarted() {}

	@Override
	public void notifySiteStarted(String siteName, String siteUrl) {}

	@Override
	public void notifySiteParsed(String siteName, int itemsFound) {}

	@Override
	public void notifyItemParsed(OnlineMediaItem item, int itemsParsed,	int totalUpdateCount) {
		updateItem(item);
	}
	
	@Override
	public void notifyItemRemoved(OnlineMediaItem newItem, int iItemsParsed, int size) {
		if ( _currentMedia != null && _currentMedia._id == newItem._id ) {
			_currentMedia = null;
			_mediaSummaryPanel.clear();
		}
	}

	@Override
	public void notifySiteFinished(String siteName) {}

	@Override
	public void notifyLoadFinished(List<OnlineMediaItem> list) {}		

	@Override
	public void notifySummaryLoadStarted(long mediaId) {
		_mediaSummaryPanel.showLoadingBar(true);
	}

	@Override
	public void notifySummaryLoadFinished(OnlineMediaItem item) {
		if ( item != null ) {
			updateItem(item);
		}
		_mediaSummaryPanel.showLoadingBar(false);
	}
	
}