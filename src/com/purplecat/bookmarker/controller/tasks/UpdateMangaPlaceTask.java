package com.purplecat.bookmarker.controller.tasks;

import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.commons.threads.IThreadTask;

public class UpdateMangaPlaceTask implements IThreadTask {
	public static final String TAG = "UpdateSavedMediaTask";
	
	private final Controller _controller;
	private final Media _media;
	private final Place _place;
	private final String _url;
	
	private Media _result;
	
	public UpdateMangaPlaceTask(Controller ctrl, Media media, Place place, String url) {
		_controller = ctrl;
		_media = media;
		_place = place;
		_url = url;
	}
	
	@Override
	public void uiTaskCompleted() {
		if ( _result != null ) {
			for ( IItemChangedObserver<Media> obs : _controller._mediaUpdateObservers ) {
//				_controller._logging.debug(1, TAG, "notify observers that manga has been updated from url: " + obs.getClass());
				obs.notifyItemUpdated(_result);
			}
		}
	}

	@Override
	public void workerTaskStart() {		
		try {
			_result = _controller._mediaService.updatePlace(_media, _place, _url);
		} catch (ServiceException e) {
			_controller._logging.error(TAG, "Could not update from url: " + _url, e);
		}
	}
}

