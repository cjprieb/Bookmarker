package com.purplecat.bookmarker.controller.tasks;

import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.commons.threads.IThreadTask;

public class UpdateMangaFromUrlTask implements IThreadTask {
	public static final String TAG = "UpdateSavedMediaTask";
	
	private final Controller _controller;
	private final String _url;
	
	private Media _result;
	
	public UpdateMangaFromUrlTask(Controller ctrl, String url) {
		_controller = ctrl;
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
			_result = _controller._mediaService.updateFromUrl(_url);
		} catch (ServiceException e) {
			_controller._logging.error(TAG, "Could not update from url: " + _url, e);
		}
	}
}

