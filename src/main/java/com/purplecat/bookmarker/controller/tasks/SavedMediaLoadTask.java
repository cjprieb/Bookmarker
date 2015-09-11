package com.purplecat.bookmarker.controller.tasks;

import java.util.List;

import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.SavedMediaService;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.threads.IThreadPool;
import com.purplecat.commons.threads.IThreadTask;

public class SavedMediaLoadTask implements IThreadTask, IListLoadedObserver<Media> {
	public static final String TAG = "SavedMediaLoadTask";
	
	ILoggingService _logging;	
	List<Media> _resultList;
	ServiceException _error;
	Iterable<IListLoadedObserver<Media>> _observers;
	SavedMediaService _mediaService;
	IThreadPool _threadPool;
	
	public SavedMediaLoadTask(ILoggingService logging, IThreadPool thread, SavedMediaService service, Iterable<IListLoadedObserver<Media>> obs) {
		_logging = logging;
		_threadPool = thread;
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
			_resultList = _mediaService.getSavedList(this);
		} catch (ServiceException e) {
			_error = e;
			_logging.error(TAG, "Could not load saved list", e);
		}
	}

	@Override
	public void notifyItemLoaded(Media item, int index, int total) {
		_threadPool.runOnUIThread(new NotifyItemLoaded(item, index, total));
	}

	@Override
	public void notifyListLoaded(List<Media> list) {}
	
	private class NotifyItemLoaded implements Runnable {
		final Media _item;
		final int _index;
		final int _total;
		
		public NotifyItemLoaded(Media item, int index, int total) {
			_item =item;
			_index = index;
			_total= total;
		}
		
		@Override
		public void run() {
			for ( IListLoadedObserver<Media> obs : _observers ) {
				obs.notifyItemLoaded(_item, _index, _total);
			}			
		}
	}
}

