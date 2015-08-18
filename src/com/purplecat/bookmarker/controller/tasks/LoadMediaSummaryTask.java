package com.purplecat.bookmarker.controller.tasks;

import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.observers.ISummaryLoadObserver;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.commons.threads.IThreadTask;

public class LoadMediaSummaryTask implements IThreadTask {
	public static final String TAG = "LoadMediaSummary";
	
	final Controller _controller;
	final long _mediaId;
	final String _url;
	
	OnlineMediaItem _result;
	ServiceException _error;

	public LoadMediaSummaryTask(Controller ctrl, long mediaId, String url) {
		_controller = ctrl;
		_mediaId = mediaId;
		_url = url;
	}
	
	@Override
	public void uiTaskCompleted() {
		for ( ISummaryLoadObserver obs : _controller._summaryLoadObservers ) {
			obs.notifySummaryLoadFinished(_result);
		}
	}

	@Override
	public void workerTaskStart() {		
		try {
			_result = _controller._mediaService.loadMediaSummary(_mediaId, _url);
		} catch (ServiceException e) {
			_error = e;
			_controller._logging.error(TAG, "Could not update from url: " + _url, e);
		}
	}

}
