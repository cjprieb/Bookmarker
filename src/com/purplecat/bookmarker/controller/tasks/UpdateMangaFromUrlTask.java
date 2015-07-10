package com.purplecat.bookmarker.controller.tasks;

import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.SavedMediaService;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.threads.IThreadTask;

public class UpdateMangaFromUrlTask implements IThreadTask {
	public static final String TAG = "UpdateSavedMediaTask";
	
	private final ILoggingService _logging;
	private final SavedMediaService _mediaService;
	private final Iterable<IItemChangedObserver<Media>> _observers;
	private final String _url;
	
	private Media _result;
	private ServiceException _error;
	
	public UpdateMangaFromUrlTask(ILoggingService logging, SavedMediaService service, Iterable<IItemChangedObserver<Media>> obs, String url) {
		_logging = logging;
		_observers = obs;
		_mediaService = service;
		_url = url;
	}
	
	@Override
	public void uiTaskCompleted() {
		if ( _result != null ) {
			for ( IItemChangedObserver<Media> obs : _observers ) {
				obs.notifyItemUpdated(_result);
			}
		}
	}

	@Override
	public void workerTaskStart() {		
		try {
			_result = _mediaService.updateFromUrl(_url);
		} catch (ServiceException e) {
			_error = e;
			_logging.error(TAG, "Could not update from url: " + _url, e);
		}
	}
}

