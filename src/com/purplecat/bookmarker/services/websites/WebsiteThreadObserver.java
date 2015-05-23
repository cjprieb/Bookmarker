package com.purplecat.bookmarker.services.websites;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.tasks.OnlineUpdateTask;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.threads.IThreadPool;


/**
 * Worker thread class; calls UI Thread
 * @author Crystal
 *
 */
public class WebsiteThreadObserver implements IWebsiteLoadObserver, Runnable {
	
	private final IThreadPool _threadPool;
	private List<IWebsiteLoadObserver> _observers;
	private final IWebsiteList _websites;
	private final IOnlineMediaRepository _onlineRepository;
	
	private OnlineUpdateTask _task; 
	
	@Inject
	public WebsiteThreadObserver(IThreadPool threadPool, IWebsiteList websites, IOnlineMediaRepository onlineRepository, ILoggingService logging) {
		_threadPool = threadPool;
		_websites = websites;
		_onlineRepository = onlineRepository;
		_observers = new LinkedList<IWebsiteLoadObserver>();
		_task = new OnlineUpdateTask(_websites, this, _onlineRepository, logging);
	}
	
	public void addWebsiteLoadObserver(IWebsiteLoadObserver obs) {
		_observers.add(obs);
	}
	
	@Override
	public void run() {
		//On Worker Thread
		if ( !_task.isRunning() ) {
			_task.loadOnlineUpdates();
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
	public void notifySiteStarted(WebsiteInfo site) {
		_threadPool.runOnUIThread(new RunSiteStarted(site));
	}

	@Override
	public void notifySiteParsed(WebsiteInfo site) {
		_threadPool.runOnUIThread(new RunSiteParsed(site));
	}

	@Override
	public void notifyItemParsed(OnlineMediaItem item) {
		_threadPool.runOnUIThread(new RunItemParsed(item));
	}

	@Override
	public void notifySiteFinished(WebsiteInfo site) {
		_threadPool.runOnUIThread(new RunSiteFinished(site));
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
		WebsiteInfo _info;
		
		public RunSiteStarted(WebsiteInfo info) {
			super(_observers);
			_info = info;
		}
		
		@Override
		public void run(IWebsiteLoadObserver obs) {
			obs.notifySiteStarted(_info);
		}
	}

	public class RunSiteParsed extends IWebsiteThreadTask {
		WebsiteInfo _info;
		
		public RunSiteParsed(WebsiteInfo info) {
			super(_observers);
			_info = info;
		}
		
		@Override
		public void run(IWebsiteLoadObserver obs) {
			obs.notifySiteParsed(_info);
		}
	}

	public class RunItemParsed extends IWebsiteThreadTask {
		OnlineMediaItem _item;
		
		public RunItemParsed(OnlineMediaItem item) {
			super(_observers);
			_item = item;
		}
		
		@Override
		public void run(IWebsiteLoadObserver obs) {
			obs.notifyItemParsed(_item);
		}
	}

	public class RunSiteFinished extends IWebsiteThreadTask {
		WebsiteInfo _info;
		
		public RunSiteFinished(WebsiteInfo info) {
			super(_observers);
			_info = info;
		}
		
		@Override
		public void run(IWebsiteLoadObserver obs) {
			obs.notifySiteFinished(_info);
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
