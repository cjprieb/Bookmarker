package com.purplecat.bookmarker.controller;

import java.util.LinkedList;
import java.util.List;

import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.controller.observers.SampleTaskObserver;
import com.purplecat.bookmarker.controller.tasks.SampleTask;
import com.purplecat.bookmarker.controller.tasks.SavedMediaLoadTask;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.DatabaseMangaService;
import com.purplecat.bookmarker.services.UrlPatternService;
import com.purplecat.bookmarker.services.databases.MangaDatabaseConnector;
import com.purplecat.commons.threads.IThreadPool;

/**
 * Should be SINGLETON!
 * @author Crystal
 *
 */
public class Controller {
	private static boolean bCreated = false;
	
	List<SampleTaskObserver> _sampleTaskObservers = new LinkedList<SampleTaskObserver>();
	List<IListLoadedObserver<Media>> _mediaLoadObservers = new LinkedList<IListLoadedObserver<Media>>();
	IThreadPool _threadPool;

	UrlPatternService _urlPatternService;
	DatabaseMangaService _mediaService;
	
	public Controller(IThreadPool threadPool, String dbPath) {
		if ( bCreated ) { throw new IllegalStateException("Cannot create more than 1 controller!"); }
		_sampleTaskObservers = new LinkedList<SampleTaskObserver>();
		_threadPool = threadPool;
		
		MangaDatabaseConnector dbConnector = new MangaDatabaseConnector(dbPath);
		_mediaService = new DatabaseMangaService(dbConnector, _urlPatternService);
		
		bCreated = true;
	}
	
	/*------Sample Task action-------*/
	public void observeSampleTask(SampleTaskObserver obs) {
		_sampleTaskObservers.add(obs);
	}
	
	public void runSampleTask(Object data) {
		_threadPool.runOnWorkerThread(new SampleTask(data, _sampleTaskObservers));
	}

	/*------Load Media action-------*/
	public void observeSavedMediaLoading(IListLoadedObserver<Media> obs) {
		_mediaLoadObservers.add(obs);
	}
	
	public void loadSavedMedia() {
		_threadPool.runOnWorkerThread(new SavedMediaLoadTask(_mediaService, _mediaLoadObservers));
	}
}
