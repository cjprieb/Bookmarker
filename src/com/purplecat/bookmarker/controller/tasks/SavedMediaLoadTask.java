package com.purplecat.bookmarker.controller.tasks;

import java.util.List;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.SavedMediaService;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.threads.IThreadTask;

public class SavedMediaLoadTask implements IThreadTask {
	public static final String TAG = "SavedMediaLoadTask";
	
	@Inject public ILoggingService _logging;
	
	List<Media> _resultList;
	ServiceException _error;
	Iterable<IListLoadedObserver<Media>> _observers;
	SavedMediaService _mediaService;
	
	public SavedMediaLoadTask(SavedMediaService service, Iterable<IListLoadedObserver<Media>> obs) {
		_observers = obs;
		_mediaService = service;
	}
	
	@Override
	public void uiTaskCompleted() {
		for ( IListLoadedObserver<Media> obs : _observers ) {
			obs.notifyListLoaded(_resultList);
		}
	}

	@Override
	public void workerTaskStart() {		
		try {
			_resultList = _mediaService.getSavedList();
		} catch (ServiceException e) {
			_error = e;
			_logging.error(TAG, "Could not load saved list", e);
		}
	}
}

