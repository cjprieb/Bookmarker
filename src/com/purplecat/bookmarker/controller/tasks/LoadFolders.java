package com.purplecat.bookmarker.controller.tasks;

import java.util.List;

import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.observers.IFoldersObserver;
import com.purplecat.bookmarker.models.Folder;
import com.purplecat.bookmarker.services.databases.DatabaseException;
import com.purplecat.commons.threads.IThreadTask;

public class LoadFolders implements IThreadTask {
	private static String TAG = "LoadFolders";
	
	private Controller _controller;
	private List<Folder> _list;	
	
	public LoadFolders(Controller controller) {
		_controller = controller;
	}

	@Override
	public void uiTaskCompleted() {
		for ( IFoldersObserver obs : _controller._folderObservers ) {
			obs.notifyListLoaded(_list);
		}
	}

	@Override
	public void workerTaskStart() {
		try {
			_list = _controller._folderRepository.query();
		} catch (DatabaseException e) {
			_controller._logging.error(TAG, "Could not update item", e);
		}
	}

}
