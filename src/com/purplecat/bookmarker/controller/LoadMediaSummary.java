package com.purplecat.bookmarker.controller;

import java.util.List;

import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.SavedMediaService;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.threads.IThreadTask;

public class LoadMediaSummary implements IThreadTask {
	public static final String TAG = "LoadMediaSummary";
	
	private final ILoggingService _logging;
	private final SavedMediaService _service;
	private final Iterable<IWebsiteLoadObserver> _observers;
	private final long _mediaId;
	private final String _url;
	
	private OnlineMediaItem _result;
	private ServiceException _error;

	public LoadMediaSummary(ILoggingService logging, SavedMediaService service, Iterable<IWebsiteLoadObserver> observers, 
			long mediaId, String url) {
		_logging = logging;
		_service = service;
		_observers = observers;
		_mediaId = mediaId;
		_url = url;
	}
	
	@Override
	public void uiTaskCompleted() {
		if ( _result != null ) {
			for ( IWebsiteLoadObserver obs : _observers ) {
				obs.notifyItemParsed(_result, -1, -1);
			}
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
