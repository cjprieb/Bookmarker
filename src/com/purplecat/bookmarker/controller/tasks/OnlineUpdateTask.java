package com.purplecat.bookmarker.controller.tasks;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.purplecat.bookmarker.extensions.OnlineMediaItemExt.OnlineBookmarkComparator;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.databases.DatabaseException;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.services.websites.IWebsiteParser;
import com.purplecat.bookmarker.sql.IConnectionManager;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.utils.StringUtils;

public class OnlineUpdateTask {
	final String TAG = "OnlineUpdateTask";
	
	final IWebsiteLoadObserver _observer;
	final IOnlineMediaRepository _repository;
	final ILoggingService _logging;
	final IConnectionManager _connectionManager;
	
	private boolean _isRunning = false;
	private boolean _stopRunning = false;
	
	private OnlineBookmarkComparator _bookmarkComparer = new OnlineBookmarkComparator();
	
	@Inject
	public OnlineUpdateTask(IWebsiteLoadObserver obs, IOnlineMediaRepository repository, ILoggingService logging, IConnectionManager mgr) {
		_observer = obs;
		_repository = repository;
		_logging = logging;
		_connectionManager = mgr;
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

	public void loadOnlineUpdates(int hoursAgo, boolean _loadGenres, Iterable<IWebsiteParser> selectedWebsites) {
		started();
		
		List<OnlineMediaItem> retList = new LinkedList<OnlineMediaItem>();
		_observer.notifyLoadStarted();
		
		Set<Long> updatedMediaIds = new HashSet<Long>();
		DateTime minDateToLoad = DateTime.now().minusHours(hoursAgo);
		
		for ( IWebsiteParser scraper : selectedWebsites ) {
			if ( isStopped() ) {
				break;
			}
			
			_observer.notifySiteStarted(scraper.getInfo());
			try {
				List<OnlineMediaItem> siteList = scraper.load(minDateToLoad);
				
				//NOTE: check for duplicates and such here?
				
				_logging.debug(0, TAG, "Site parsed: " + scraper.getInfo()._name);
				_observer.notifySiteParsed(scraper.getInfo(), siteList.size());				
				
				updatedMediaIds.addAll(loadMatchingMedia(siteList, retList));

				_logging.debug(1, TAG, "All items retrieved from database: " + retList.size());
				
				//NOTE: reorder list here?
				retList.sort(_bookmarkComparer);
				
				if ( _loadGenres ) {
					loadItemInfo(scraper, retList, updatedMediaIds);
				}
			} 
			catch (ServiceException e) {
				_logging.error(TAG, "Error loading from: " + scraper.getInfo()._name, e);
				//TODO: how to handle service exception
			} 
			_observer.notifySiteFinished(scraper.getInfo());
		}

		_observer.notifyLoadFinished(retList);
		
		finished();
	}
	
	public Set<Long> loadMatchingMedia(List<OnlineMediaItem> siteList, List<OnlineMediaItem> retList) throws ServiceException {	
		Set<Long> updatedMediaIds = new HashSet<Long>();
		int iItemsParsed = 0;
		try {
			_connectionManager.open();
			for ( OnlineMediaItem item : siteList ) {
				_logging.debug(1, TAG, "Looking for item: " + item._displayTitle);
				OnlineMediaItem found = _repository.findOrCreate(item);
				iItemsParsed++;
				if ( found != null ) {
					_logging.debug(2, TAG, "DB Item found: " + found);
					retList.add(found);
					if ( found.isUpdated() ) { 
						updatedMediaIds.add(item._mediaId); 
					}
					_observer.notifyItemParsed(found, iItemsParsed, updatedMediaIds.size());
				}
				else {
					_logging.debug(2, TAG, "No match for: " + item);
				}
			}
		} catch (DatabaseException e) {
			_logging.error(TAG, "Database error in loadMatchingMedia", e);
			throw new ServiceException("Database error", ServiceException.SQL_ERROR);
		}
		finally {
			_connectionManager.close();
		}
		return updatedMediaIds;
	}
	
	public void loadItemInfo(IWebsiteParser scraper, List<OnlineMediaItem> retList, Collection<Long> updatedMediaIds)  throws ServiceException {
		int iItemsParsed = 0;
		for ( OnlineMediaItem item : retList ) { //use found items, not parsed items.
			if ( isStopped() ) {
				break;
			}
			iItemsParsed++;
			if ( !StringUtils.isNullOrEmpty(item._summary) && item._genres.size() > 0 ) {
				_logging.debug(2, TAG, "Not loading summary for " + item._displayTitle);
				_observer.notifyItemParsed(item, iItemsParsed, updatedMediaIds.size());
				continue;
			}
			OnlineMediaItem newItem = scraper.loadItem(item);
			if ( newItem != null ) {
				_logging.debug(2, TAG, "Item parsed: " + item);
				if ( item._summary != null ) {
					_logging.debug(3, TAG, "Summary: " + (item._summary.length() < 30 ? item._summary : item._summary.substring(0, 30)));
				}
				else {
					_logging.debug(3, TAG, "Summary: [null]");
				}
				try {
					_connectionManager.open();
					_repository.update(newItem);
				} catch (DatabaseException e) {
					_logging.error(TAG, "Database error in loadMatchingMedia", e);
					throw new ServiceException("Database error", ServiceException.SQL_ERROR);
				}
				finally {
					_connectionManager.close();
				}
				_observer.notifyItemParsed(newItem, iItemsParsed, updatedMediaIds.size());
			}
			else {
				_logging.debug(2, TAG, "Updated Item was null after load: " + item);
			}
		}		
	}
}
