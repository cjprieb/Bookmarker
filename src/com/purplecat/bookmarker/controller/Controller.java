package com.purplecat.bookmarker.controller;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.controller.observers.SampleTaskObserver;
import com.purplecat.bookmarker.controller.tasks.SampleTask;
import com.purplecat.bookmarker.controller.tasks.SavedMediaLoadTask;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.DatabaseMangaService;
import com.purplecat.commons.threads.IThreadPool;

@Singleton
public class Controller {
	public static final String TAG = "Controller";
	
	private final IThreadPool _threadPool;	
	private final DatabaseMangaService _mediaService;
	
	private final List<SampleTaskObserver> _sampleTaskObservers;
	private final List<IListLoadedObserver<Media>> _mediaLoadObservers;
	
	@Inject
	public Controller(IThreadPool threadPool, DatabaseMangaService mangaService) {
		_threadPool = threadPool;		
		_mediaService = mangaService;
		
		_sampleTaskObservers = new LinkedList<SampleTaskObserver>();
		_mediaLoadObservers = new LinkedList<IListLoadedObserver<Media>>();
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
