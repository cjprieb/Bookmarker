package com.purplecat.bookmarker.controller.tasks;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.SavedMediaService;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.threads.IThreadTask;

public class UpdateMangaFromUrlTask implements IThreadTask {
	public static final String TAG = "UpdateSavedMediaTask";
	
	@Inject public ILoggingService _logging;
	
	Media _result;
	ServiceException _error;
	Iterable<IItemChangedObserver<Media>> _observers;
	SavedMediaService _mediaService;
	String _url;
	
	public UpdateMangaFromUrlTask(SavedMediaService service, Iterable<IItemChangedObserver<Media>> obs, String url) {
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

