package com.purplecat.bookmarker.controller.tasks;

import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.SavedMediaService;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.threads.IThreadTask;

public class UpdateSavedFromOnlineTask implements IThreadTask {
	public static final String TAG = "UpdateSavedMediaTask";
	
	ILoggingService _logging;
	
	Media _result;
	ServiceException _error;
	Iterable<IItemChangedObserver<Media>> _observers;
	SavedMediaService _mediaService;
	OnlineMediaItem _onlineItem;
	
	public UpdateSavedFromOnlineTask(ILoggingService logging, SavedMediaService service, Iterable<IItemChangedObserver<Media>> obs, OnlineMediaItem onlineItem) {
		_logging = logging;
		_observers = obs;
		_mediaService = service;
		_onlineItem = onlineItem;
	}
	
	@Override
	public void uiTaskCompleted() {
		for ( IItemChangedObserver<Media> obs : _observers ) {
			obs.notifyItemUpdated(_result);
		}
	}

	@Override
	public void workerTaskStart() {		
		try {
			_result = _mediaService.updateFromOnlineItem(_onlineItem);
		} catch (ServiceException e) {
			_error = e;
			_logging.error(TAG, "Could not update from online item", e);
		}
	}
}

