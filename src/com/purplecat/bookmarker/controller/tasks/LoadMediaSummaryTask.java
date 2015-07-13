package com.purplecat.bookmarker.controller.tasks;

import com.purplecat.bookmarker.controller.observers.ISummaryLoadObserver;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.SavedMediaService;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.threads.IThreadTask;

public class LoadMediaSummaryTask implements IThreadTask {
	public static final String TAG = "LoadMediaSummary";
	
	private final ILoggingService _logging;
	private final SavedMediaService _service;
	private final Iterable<ISummaryLoadObserver> _observers;
	private final long _mediaId;
	private final String _url;
	
	private OnlineMediaItem _result;
	private ServiceException _error;

	public LoadMediaSummaryTask(ILoggingService logging, SavedMediaService service, Iterable<ISummaryLoadObserver> observers, 
			long mediaId, String url) {
		_logging = logging;
		_service = service;
		_observers = observers;
		_mediaId = mediaId;
		_url = url;
	}
	
	@Override
	public void uiTaskCompleted() {
		for ( ISummaryLoadObserver obs : _observers ) {
			obs.notifySummaryLoadFinished(_result);
		}
	}

	@Override
	public void workerTaskStart() {		
		try {
			_result = _service.loadMediaSummary(_mediaId, _url);
		} catch (ServiceException e) {
			_error = e;
			_logging.error(TAG, "Could not update from url: " + _url, e);
		}
	}

}
