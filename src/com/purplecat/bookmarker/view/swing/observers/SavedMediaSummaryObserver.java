package com.purplecat.bookmarker.view.swing.observers;

import java.util.List;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.view.swing.panels.SavedMediaSummaryPanel;
import com.purplecat.bookmarker.view.swing.panels.SummarySidebar;
import com.purplecat.commons.swing.IRowSelectionListener;

public class SavedMediaSummaryObserver implements IRowSelectionListener<Media>, IItemChangedObserver<Media>, IWebsiteLoadObserver {	
	@Inject SavedMediaSummaryPanel _mediaSummaryPanel;
	@Inject SummarySidebar _parentSummaryPanel;
	
	Media _currentMedia;

	@Override
	public void rowSelected(RowSelectionEvent<Media> e) {
		if ( _mediaSummaryPanel.getPanel() == null ) {
			_mediaSummaryPanel.create();
		}
		_currentMedia = e.getTable().getSelectedItem();
		_mediaSummaryPanel.update(_currentMedia);
		_parentSummaryPanel.setSummaryView(_mediaSummaryPanel.getPanel());
	}

	@Override
	public void notifyItemUpdated(Media item) {
		if ( _currentMedia._id == item._id ) {
			_currentMedia = item;
			_mediaSummaryPanel.update(_currentMedia);		
		}
	}

	@Override
	public void notifyLoadStarted() {}

	@Override
	public void notifySiteStarted(WebsiteInfo site) {}

	@Override
	public void notifySiteParsed(WebsiteInfo site, int itemsFound) {}

	@Override
	public void notifyItemParsed(OnlineMediaItem item, int itemsParsed,	int totalUpdateCount) {
		if ( _currentMedia._id == item._mediaId ) {
			_currentMedia.updateFrom(item);
			_mediaSummaryPanel.update(_currentMedia);		
		}
	}

	@Override
	public void notifySiteFinished(WebsiteInfo site) {}

	@Override
	public void notifyLoadFinished(List<OnlineMediaItem> list) {}
	
}