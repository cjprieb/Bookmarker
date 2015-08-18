package com.purplecat.bookmarker.view.swing.observers;

import java.util.List;

import javax.swing.JPanel;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.controller.observers.ISummaryLoadObserver;
import com.purplecat.bookmarker.models.EFavoriteState;
import com.purplecat.bookmarker.models.EStoryState;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.view.swing.panels.EditMediaSummaryPanel;
import com.purplecat.bookmarker.view.swing.panels.SavedMediaSummaryPanel;
import com.purplecat.bookmarker.view.swing.panels.SummarySidebar;
import com.purplecat.commons.swing.IRowSelectionListener;

public class SavedMediaSummaryObserver implements IRowSelectionListener<Media>, IItemChangedObserver<Media>, 
	IWebsiteLoadObserver, ISummaryLoadObserver, ISummaryPanelListener {	
	
	@Inject Controller _controller;
	@Inject SavedMediaSummaryPanel _mediaSummaryPanel;
	@Inject EditMediaSummaryPanel _editMediaSummaryPanel;
	@Inject SummarySidebar _parentSummaryPanel;
	
	Media _currentMedia;
	
	public JPanel getSummaryPanel() {
		return _mediaSummaryPanel.getPanel();
	}
	
	public void updateEditorPanel() {
		_editMediaSummaryPanel.setFavoriteState(_currentMedia._rating);
		_editMediaSummaryPanel.setStoryState(_currentMedia._storyState);
		_editMediaSummaryPanel.setPlace(_currentMedia._lastReadPlace);		
	}

	@Override
	public void rowSelected(RowSelectionEvent<Media> e) {
		if ( _mediaSummaryPanel.getPanel() == null ) {
			_mediaSummaryPanel.create();
		}
		if ( _editMediaSummaryPanel.getPanel() == null ) {
			_editMediaSummaryPanel.create();
		}
		_currentMedia = e.getTable().getSelectedItem();
		_mediaSummaryPanel.update(_currentMedia);
		_parentSummaryPanel.setSummaryView(_mediaSummaryPanel.getPanel());
		_parentSummaryPanel.setEditorView(_editMediaSummaryPanel.getPanel());
		updateEditorPanel();
	}

	@Override
	public void notifyItemUpdated(Media item) {
		if ( _currentMedia._id == item._id ) {
			_currentMedia = item;
			_mediaSummaryPanel.update(_currentMedia);
			updateEditorPanel();
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
			updateEditorPanel();	
		}
	}

	@Override
	public void notifySiteFinished(WebsiteInfo site) {}

	@Override
	public void notifyLoadFinished(List<OnlineMediaItem> list) {}	

	@Override
	public void notifySummaryLoadStarted(long mediaId) {
		_mediaSummaryPanel.showLoadingBar(true);
	}

	@Override
	public void notifySummaryLoadFinished(OnlineMediaItem item) {
		if ( item != null && _currentMedia != null && _currentMedia._id == item._mediaId ) {
			_currentMedia.updateFrom(item);
			_mediaSummaryPanel.update(_currentMedia);			
		}
		_mediaSummaryPanel.showLoadingBar(false);
	}

	@Override
	public void notifyCategoryChanged(String categoryName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyPlaceChanged(Place place) {
		if ( _currentMedia != null ) {
			_currentMedia._lastReadPlace = place;
			_controller.updateMedia(_currentMedia);
		}
	}

	@Override
	public void notifyStoryStateChanged(EStoryState state) {
//		if ( _currentMedia != null ) {
//			_currentMedia._storyState = state;
//			_controller.updateMedia(_currentMedia);
//		}
	}

	@Override
	public void notifyFavoriteStateChanged(EFavoriteState state) {
		if ( _currentMedia != null ) {
			_currentMedia._rating = state;
			_controller.updateMedia(_currentMedia);
		}
	}
	
}