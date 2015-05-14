package com.purplecat.bookmarker.controller.tasks;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.services.websites.IThreadObserver;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteParser;

public class OnlineUpdateTask {
	
	public final IWebsiteList _websites;
	public final IThreadObserver _observer;
	public final IOnlineMediaRepository _repository;
	
	@Inject
	public OnlineUpdateTask(IWebsiteList websites, IThreadObserver obs, IOnlineMediaRepository repository) {
		_websites = websites;
		_observer = obs;
		_repository = repository;
	}

	public void loadOnlineUpdates() {
		List<OnlineMediaItem> list = new LinkedList<OnlineMediaItem>();
		_observer.notifyLoadStarted();
		
		for ( IWebsiteParser scraper : _websites.getList() ) {
			_observer.notifySiteStarted(scraper.getInfo());
			
			try {
				List<OnlineMediaItem> siteList = scraper.load();
				
				//NOTE: check for duplicates and such here?
				
				_observer.notifySiteParsed(scraper.getInfo());
				
				for ( OnlineMediaItem item : siteList ) {
					OnlineMediaItem found = _repository.findOrCreate(item);
					if ( found != null ) {
						list.add(found);
					}
				}
				
				//NOTE: reorder list here?
				
				for ( OnlineMediaItem item : list ) { //use found items, not parsed items.
					item = scraper.loadItem(item);
					_repository.update(item);
					_observer.notifyItemParsed(item);
				}
			} 
			catch (ServiceException e) {
				//TODO: how to handle service exception
			}
			_observer.notifySiteFinished(scraper.getInfo());
		}

		_observer.notifyLoadFinished(list);
	}

}
