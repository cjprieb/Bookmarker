package com.purplecat.bookmarker.controller.tasks;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.inject.Inject;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.services.websites.IWebsiteParser;
import com.purplecat.commons.logs.ILoggingService;

public class OnlineUpdateTask {
	final String TAG = "OnlineUpdateTask";
	
	public final IWebsiteList _websites;
	public final IWebsiteLoadObserver _observer;
	public final IOnlineMediaRepository _repository;
	public final ILoggingService _logging;
	
	private boolean _isRunning = false;
	private boolean _stopRunning = false;
	
	@Inject
	public OnlineUpdateTask(IWebsiteList websites, IWebsiteLoadObserver obs, IOnlineMediaRepository repository, ILoggingService logging) {
		_websites = websites;
		_observer = obs;
		_repository = repository;
		_logging = logging;
	}

	public synchronized boolean isRunning() {
		return _isRunning;
	}

	public synchronized void stop() {
		_stopRunning = true;
	}
	
	private synchronized boolean isStopped() {
		return _stopRunning;
	}
	
	private synchronized void started() {
		_isRunning = true;
		_stopRunning = false;
	}
	
	private synchronized void finished() {
		_isRunning = false;
		_stopRunning = false;
	}

	public void loadOnlineUpdates() {
		started();
		
		List<OnlineMediaItem> list = new LinkedList<OnlineMediaItem>();
		Set<Long> updatedMediaIds = new HashSet<Long>();
		_observer.notifyLoadStarted();
		
		for ( IWebsiteParser scraper : _websites.getList() ) {
			if ( isStopped() ) {
				break;
			}
			
			_observer.notifySiteStarted(scraper.getInfo());
			
			try {
				List<OnlineMediaItem> siteList = scraper.load();
				
				//NOTE: check for duplicates and such here?
				
				_observer.notifySiteParsed(scraper.getInfo(), siteList.size());
				_logging.debug(0, TAG, "Site parsed: " + scraper.getInfo()._name);
				
				int iItemsParsed = 0;
				for ( OnlineMediaItem item : siteList ) {
					OnlineMediaItem found = _repository.findOrCreate(item);
					iItemsParsed++;
					if ( found != null ) {
						_logging.debug(2, TAG, "DB Item found: " + found);
						list.add(found);
						if ( found.isUpdated() ) { 
							updatedMediaIds.add(item._mediaId); 
						}
						_observer.notifyItemParsed(found, iItemsParsed, updatedMediaIds.size());
					}
					else {
						_logging.debug(2, TAG, "No match for: " + item);
					}
				}
								
				//NOTE: reorder list here?

				_logging.debug(1, TAG, "All items retrieved from databased: " + list.size());
				iItemsParsed = 0;
				for ( OnlineMediaItem item : list ) { //use found items, not parsed items.
					if ( isStopped() ) {
						break;
					}
					OnlineMediaItem newItem = scraper.loadItem(item);
					iItemsParsed++;
					if ( newItem != null ) {
						_logging.debug(2, TAG, "Item parsed: " + item);
						_repository.update(newItem);
						_observer.notifyItemParsed(newItem, iItemsParsed, updatedMediaIds.size());
					}
					else {
						_logging.debug(2, TAG, "Updated Item was null after load: " + item);
					}
				}
			} 
			catch (ServiceException e) {
				_logging.error(TAG, "Error loading from: " + scraper.getInfo()._name, e);
				//TODO: how to handle service exception
			}
			_observer.notifySiteFinished(scraper.getInfo());
		}

		_observer.notifyLoadFinished(list);
		
		finished();
	}

}
