package com.purplecat.bookmarker.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.purplecat.bookmarker.controller.FolderCache;
import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.EStoryState;
import com.purplecat.bookmarker.models.Folder;
import com.purplecat.commons.tests.GetRandom;

public class FolderCacheTests {
	static int MAX_SIZE = 5;
	
	FolderCache _cache;
	IListLoadedObserver<Folder> _listObserver;
	IItemChangedObserver<Folder> _itemObserver;
	List<Folder> _folderList;
	
	@Before
	public void setup() {
		_cache = new FolderCache(null);
		_folderList = new LinkedList<Folder>();
		for ( int i = 0; i < MAX_SIZE; i++ ) {
			_folderList.add(createFolder());
		}
		_listObserver = _cache;
		_itemObserver = _cache;
		
		_listObserver.notifyListLoaded(_folderList);
		assertEquals(_folderList.size(), _cache.size());
	}
	
	private Folder createFolder() {
		Folder folder = new Folder();
		folder._id = GetRandom.getInteger();
		folder._name = GetRandom.getString(10);
		folder._storyState = GetRandom.getItem(EStoryState.values());
		return folder;
	}

	@Test
	public void listLoadedTwice() {
		_listObserver.notifyListLoaded(_folderList);
		assertEquals(_folderList.size(), _cache.size());
	}

	@Test
	public void getFolderById() {
		Folder expected = GetRandom.getItem(_folderList).copy();
		Folder actual = _cache.getById(expected._id);
		assertNotNull(actual);
		assertEquals(expected._name, actual._name);
		assertEquals(expected._storyState, actual._storyState);
	}

	@Test
	public void updateFolder() {
		Folder expected = GetRandom.getItem(_folderList).copy();
		expected._name = GetRandom.getString(12);
		_itemObserver.notifyItemUpdated(expected);
		Folder actual = _cache.getById(expected._id);
		assertNotNull(actual);
		assertEquals(expected._name, actual._name);
		assertEquals(expected._storyState, actual._storyState);
	}

	@Test
	public void addFolder() {
		Folder expected = createFolder();
		_itemObserver.notifyItemUpdated(expected);
		Folder actual = _cache.getById(expected._id);
		assertNotNull(actual);
		assertEquals(expected._name, actual._name);
		assertEquals(expected._storyState, actual._storyState);
	}

}
