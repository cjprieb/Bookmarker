package com.purplecat.bookmarker;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.FolderCache;
import com.purplecat.bookmarker.modules.ThreadTestingModule;

public class FoldersControllerThreads {
	
	Controller _controller;
	FolderCache _folderCache;
	
	@Before
	public void setup() {
		Injector injector = Guice.createInjector(new ThreadTestingModule());		
		_controller = injector.getInstance(Controller.class);
		_folderCache = injector.getInstance(FolderCache.class);
		_controller.observerFolders(_folderCache);
	}
	
	@Test
	public void testLoadList() {
		_controller.loadFolders();
		assertTrue(_folderCache.size() > 0);
	}

}
