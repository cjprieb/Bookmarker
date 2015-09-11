package com.purplecat.bookmarker.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.purplecat.bookmarker.controller.observers.IFoldersObserver;
import com.purplecat.bookmarker.models.Folder;

@Singleton
public class FolderCache implements IFoldersObserver {
	
	private Map<Long, Folder> _folders = new HashMap<Long, Folder>();
	
	@Inject
	public FolderCache(Controller controller) {
		if ( controller != null ) {
			controller.observerFolders(this);
		}
	}

	public void addAll(List<Folder> list) {
		_folders.clear();
		for ( Folder folder : list ) {
			_folders.put(folder._id, folder);
		}
	}

	public int size() {
		return _folders.size();
	}

	public Folder getById(long _id) {
		return _folders.get(_id);
	}

	public void update(Folder folder) {
		Folder foundItem = _folders.get(folder._id);
		if ( foundItem != null ) {
			foundItem.updateFrom(folder);
		}
		else {
			_folders.put(folder._id, folder);
		}
	}

	@Override
	public void notifyItemLoaded(Folder item, int index, int total) {
		update(item);
	}

	@Override
	public void notifyListLoaded(List<Folder> list) {
		addAll(list);
	}

	@Override
	public void notifyItemUpdated(Folder item) {
		update(item);
	}

}
