package com.purplecat.bookmarker.controller.tasks;

import java.util.List;

import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.DatabaseMangaService;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.logs.LoggingService;
import com.purplecat.commons.threads.IThreadTask;

public class SavedMediaLoadTask implements IThreadTask {
	List<Media> _resultList;
	ServiceException _error;
	Iterable<IListLoadedObserver<Media>> _observers;
	DatabaseMangaService _mediaService;
	ILoggingService _logging = LoggingService.create();
	
	public SavedMediaLoadTask(DatabaseMangaService service, Iterable<IListLoadedObserver<Media>> obs) {
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
			_logging.error("SavedMediaLoadTask", "Could not load saved list", e);
		}
	}
}

