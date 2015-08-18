package com.purplecat.bookmarker.controller.tasks;

import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.commons.threads.IThreadTask;

public class UpdateSavedMediaTask implements IThreadTask {
	public static final String TAG = "UpdateSavedMediaTask";
	
	Controller _controller;
	
	Media _result;
	Media _updateItem;

	ServiceException _error;
	
	public UpdateSavedMediaTask(Controller ctrl, Media item) {
		_controller = ctrl;
		_updateItem = item;
	}
	
	@Override
	public void uiTaskCompleted() {
		for ( IItemChangedObserver<Media> obs : _controller._mediaUpdateObservers ) {
			obs.notifyItemUpdated(_result);
		}
	}

	@Override
	public void workerTaskStart() {		
		try {
			_result = _controller._mediaService.update(_updateItem);
		} catch (ServiceException e) {
			_error = e;
			_controller._logging.error(TAG, "Could not update item", e);
		}
	}
}

