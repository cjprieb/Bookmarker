package com.purplecat.bookmarker.services.websites;

import java.util.List;
import java.util.stream.Collectors;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.tasks.OnlineUpdateTask;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.sql.IConnectionManager;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.threads.IThreadPool;


/**
 * Worker thread class; calls UI Thread
 * @author Crystal
 *
 */
public class WebsiteThreadObserver implements IWebsiteLoadObserver, Runnable {
	
	private final IThreadPool _threadPool;
	private final IWebsiteList _websites;
	private final IOnlineMediaRepository _onlineRepository;
	
	private OnlineUpdateTask _task; 
	private int _hoursAgo;
	private boolean _loadGenres;
	private List<IWebsiteParser> _selectedWebsites;
	private Iterable<IWebsiteLoadObserver> _observers;
	
	@Inject
	public WebsiteThreadObserver(IThreadPool threadPool, IWebsiteList websites, IOnlineMediaRepository onlineRepository, 
			ILoggingService logging, IConnectionManager mgr, IGenreRepository genreRepository) {
		_threadPool = threadPool;
		_websites = websites;
		_onlineRepository = onlineRepository;
		_task = new OnlineUpdateTask(this, _onlineRepository, logging, mgr, genreRepository, _websites);
	}
	
	public void setLoadParameters(int hoursAgo, boolean loadGenres, boolean loadAll, String name, Iterable<IWebsiteLoadObserver> observers) {
		_hoursAgo = hoursAgo;
		_loadGenres = loadGenres;
		_observers = observers;

		_selectedWebsites = _websites.getSortedList();
		if ( !loadAll ) {
			_selectedWebsites = _selectedWebsites.stream()
					.filter(site -> site.getName().equalsIgnoreCase(name))
					.collect(Collectors.toList());
		}
	}
	
	@Override
	public void run() {
		//On Worker Thread
		if ( !_task.isRunning() ) {
			System.out.println("loading online updates");
			_task.loadOnlineUpdates(_hoursAgo, _loadGenres, _selectedWebsites);
		}
	}
	
	public void stop() {
		_task.stop();
	}

	@Override
	public void notifyLoadStarted() {
		_threadPool.runOnUIThread(new RunLoadStarted());
	}

	@Override
	public void notifySiteStarted(String siteName, String siteUrl) {
		_threadPool.runOnUIThread(new RunSiteStarted(siteName, siteUrl));
	}

	@Override
	public void notifySiteParsed(String siteName, int itemsFound) {
		_threadPool.runOnUIThread(new RunSiteParsed(siteName, itemsFound));
	}

	@Override
	public void notifyItemParsed(OnlineMediaItem item, int itemsParsed, int itemsUpdated) {
		_threadPool.runOnUIThread(new RunItemParsed(item, itemsParsed, itemsUpdated, false));
	}
	
	@Override
	public void notifyItemRemoved(OnlineMediaItem item, int itemsParsed, int itemsUpdated) {
		_threadPool.runOnUIThread(new RunItemParsed(item, itemsParsed, itemsUpdated, true));
	}

	@Override
	public void notifySiteFinished(String siteName) {
		_threadPool.runOnUIThread(new RunSiteFinished(siteName));
	}

	@Override
	public void notifyLoadFinished(List<OnlineMediaItem> list) {
		_threadPool.runOnUIThread(new RunLoadFinished(list));
	}

	public class RunLoadStarted extends IWebsiteThreadTask {
		public RunLoadStarted() {
			super(_observers);
		}
		
		@Override
		public void run(IWebsiteLoadObserver obs) {
			obs.notifyLoadStarted();
		}
	}

	public class RunSiteStarted extends IWebsiteThreadTask {
		String _siteName;
		String _siteUrl;
		
		public RunSiteStarted(String siteName, String siteUrl) {
			super(_observers);
			_siteName = siteName;
			_siteUrl = siteUrl;
		}
		
		@Override
		public void run(IWebsiteLoadObserver obs) {
			obs.notifySiteStarted(_siteName, _siteUrl);
		}
	}

	public class RunSiteParsed extends IWebsiteThreadTask {
		String _siteName;
		int _itemsFound;
		
		public RunSiteParsed(String siteName, int itemsFound) {
			super(_observers);
			_siteName = siteName;
			_itemsFound = itemsFound;
		}
		
		@Override
		public void run(IWebsiteLoadObserver obs) {
			obs.notifySiteParsed(_siteName, _itemsFound);
		}
	}

	public class RunItemParsed extends IWebsiteThreadTask {
		OnlineMediaItem _item;
		int _itemsParsed;
		int _itemsUpdated;
		boolean _removeIt;
		
		public RunItemParsed(OnlineMediaItem item, int itemsParsed, int itemsUpdated, boolean removeIt) {
			super(_observers);
			_item = item;
			_itemsParsed = itemsParsed;
			_itemsUpdated = itemsUpdated;
			_removeIt = removeIt;
		}
		
		@Override
		public void run(IWebsiteLoadObserver obs) {
			if ( _removeIt ) {
				obs.notifyItemRemoved(_item, _itemsParsed, _itemsUpdated);
			}
			else {
				obs.notifyItemParsed(_item, _itemsParsed, _itemsUpdated);
			}
		}
	}

	public class RunSiteFinished extends IWebsiteThreadTask {
		String _siteName;
		
		public RunSiteFinished(String siteName) {
			super(_observers);
			_siteName = siteName;
		}
		
		@Override
		public void run(IWebsiteLoadObserver obs) {
			obs.notifySiteFinished(_siteName);
		}
	}

	public class RunLoadFinished extends IWebsiteThreadTask {
		List<OnlineMediaItem> _list;
		
		public RunLoadFinished(List<OnlineMediaItem> list) {
			super(_observers);
			_list = list;
		}
		
		@Override
		public void run(IWebsiteLoadObserver obs) {
			obs.notifyLoadFinished(_list);
		}
	}

}
