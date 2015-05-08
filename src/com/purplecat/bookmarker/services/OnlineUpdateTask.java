package com.purplecat.bookmarker.services;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.services.websites.IThreadObserver;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteScraper;

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
		
		for ( IWebsiteScraper scraper : _websites.getList() ) {
			_observer.notifySiteStarted(scraper.getInfo());
			List<OnlineMediaItem> siteList = scraper.load();
			
			//NOTE: check for duplicates and such here?
			
			_observer.notifySiteParsed(scraper.getInfo());
			
			for ( OnlineMediaItem item : siteList ) {
				item = scraper.loadItem(item);
				OnlineMediaItem found = _repository.find(item);
				if ( found != null ) {
					list.add(found);
				}
				else {
					_repository.insert(item);
					list.add(item);
				}
				_observer.notifyItemParsed(item);
			}
			_observer.notifySiteFinished(scraper.getInfo());
		}

		_observer.notifyLoadFinished(list);
	}

}
