package com.purplecat.bookmarker.controller;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.purplecat.bookmarker.controller.observers.IFoldersObserver;
import com.purplecat.bookmarker.controller.observers.IItemChangedObserver;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.controller.observers.ISummaryLoadObserver;
import com.purplecat.bookmarker.controller.observers.SampleTaskObserver;
import com.purplecat.bookmarker.controller.tasks.LoadFolders;
import com.purplecat.bookmarker.controller.tasks.LoadMediaSummaryTask;
import com.purplecat.bookmarker.controller.tasks.SampleTask;
import com.purplecat.bookmarker.controller.tasks.SavedMediaLoadTask;
import com.purplecat.bookmarker.controller.tasks.UpdateMangaFromUrlTask;
import com.purplecat.bookmarker.controller.tasks.UpdateSavedFromOnlineTask;
import com.purplecat.bookmarker.controller.tasks.UpdateSavedMediaTask;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.IFolderRepository;
import com.purplecat.bookmarker.services.SavedMediaService;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.services.websites.WebsiteThreadObserver;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.threads.IThreadPool;

@Singleton
public class Controller {
	public static final String TAG = "Controller";

	public final ILoggingService _logging;	
	public final IThreadPool _threadPool;	
	public final SavedMediaService _mediaService;
	public final IFolderRepository _folderRepository;
	public final WebsiteThreadObserver _observer;
	
	public final List<SampleTaskObserver> _sampleTaskObservers;
	public final List<IListLoadedObserver<Media>> _mediaLoadObservers;
	public final List<IItemChangedObserver<Media>> _mediaUpdateObservers;	
	public final List<IWebsiteLoadObserver> _websiteLoadObservers;
	public final List<ISummaryLoadObserver> _summaryLoadObservers;
	public final List<IFoldersObserver> _folderObservers;
	
	@Inject
	public Controller(ILoggingService logging, IThreadPool threadPool, SavedMediaService mangaService, WebsiteThreadObserver observer, IFolderRepository folders) {
		_logging = logging;
		_threadPool = threadPool;		
		_mediaService = mangaService;
		_observer = observer;
		_folderRepository = folders;
		
		_sampleTaskObservers = new LinkedList<SampleTaskObserver>();
		_mediaLoadObservers = new LinkedList<IListLoadedObserver<Media>>();
		_mediaUpdateObservers = new LinkedList<IItemChangedObserver<Media>>();
		_websiteLoadObservers = new LinkedList<IWebsiteLoadObserver>();
		_summaryLoadObservers = new LinkedList<ISummaryLoadObserver>();
		_folderObservers = new LinkedList<IFoldersObserver>();
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
		_threadPool.runOnWorkerThread(new SavedMediaLoadTask(_logging, _threadPool, _mediaService, _mediaLoadObservers));
	}

	/*------Run/Stop Update Thread action-------*/	
	public void observeOnlineThreadLoading(IWebsiteLoadObserver obs) {
		_websiteLoadObservers.add(obs);
	}
	
	public void loadUpdateMedia(int hoursAgo, boolean loadGenres, boolean loadAll, WebsiteInfo selectedWebsite) {
		_observer.setLoadParameters(hoursAgo, loadGenres, loadAll, selectedWebsite, _websiteLoadObservers);
		_threadPool.runOnWorkerThread(_observer);
	}
	
	public void stopUpdates() {
		_observer.stop();
	}

	/*------Update Media actions--------*/
	public void observeSavedMediaUpdate(IItemChangedObserver<Media> obs) {
		_mediaUpdateObservers.add(obs);
	}
	
	public void updateMediaFrom(OnlineMediaItem selectedItem) {
		_threadPool.runOnWorkerThread(new UpdateSavedFromOnlineTask(_logging, _mediaService, _mediaUpdateObservers, selectedItem));
	}

	public void updateMangaFromUrl(String url) {
		_threadPool.runOnWorkerThread(new UpdateMangaFromUrlTask(_logging, _mediaService, _mediaUpdateObservers, url));
	}

	public void updateMedia(Media media) {
		_threadPool.runOnWorkerThread(new UpdateSavedMediaTask(this, media));
	}
	
	/*------Load Media Summary actions--------*/
	public void observeSummaryLoading(ISummaryLoadObserver obs) {
		_summaryLoadObservers.add(obs);
	}	
	
	public void loadMediaSummary(long mediaId, String url) {
		for ( ISummaryLoadObserver obs : _summaryLoadObservers ) {
			obs.notifySummaryLoadStarted(mediaId);
		}
		_threadPool.runOnWorkerThread(new LoadMediaSummaryTask(this, mediaId, url));		
	}
	
	/*------Load Folders actions--------*/
	public void observerFolders(IFoldersObserver observer) {
		_folderObservers.add(observer);
	}
	
	public void loadFolders() {
		_threadPool.runOnWorkerThread(new LoadFolders(this));		
	}
}
