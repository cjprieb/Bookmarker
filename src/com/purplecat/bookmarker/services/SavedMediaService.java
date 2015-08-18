package com.purplecat.bookmarker.services;

import java.util.List;
import java.util.Optional;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.UrlPatternResult;
import com.purplecat.bookmarker.services.databases.DatabaseException;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.bookmarker.services.databases.IMediaRepository;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteParser;
import com.purplecat.bookmarker.sql.IConnectionManager;
import com.purplecat.commons.logs.ILoggingService;

public class SavedMediaService {
	public final ILoggingService _logging;
	public final IConnectionManager _connectionManager;
	public final IMediaRepository _database;
	public final UrlPatternService _patterns;
	public final IWebsiteList _websites;
	public final IOnlineMediaRepository _onlineDatabase;
	public final IGenreRepository _genreDatabase;
	
	@Inject
	public SavedMediaService(ILoggingService logging, IConnectionManager mgr, IMediaRepository database, IOnlineMediaRepository onlineDatabase,
			IGenreRepository genreDatabase, UrlPatternService patterns, IWebsiteList websites) {
		_logging = logging;
		_connectionManager = mgr;
		_database = database;
		_patterns = patterns;
		_websites = websites;
		_onlineDatabase = onlineDatabase;
		_genreDatabase = genreDatabase;
	}

	public Media get(long id) throws ServiceException {
		if ( id <= 0 ) {
			throw new ServiceException(ServiceException.INVALID_ID);
		}
		Media media = null;
		try {
			media = _database.queryById(id);
		} 
		catch ( DatabaseException e ) {
			throw new ServiceException("Error loading media for id " + id, ServiceException.SQL_ERROR);
		}
		return media;
	}
	
	public Media updateFromUrl(String url) throws ServiceException {
		Media media = null;
		ServiceException serviceException = null;
		try {
			_connectionManager.open();
			
			UrlPatternResult patternResult = _patterns.match(url);
			
			if ( patternResult != null && patternResult._title != null && patternResult._title.length() > 0 ) {
				List<Media> list = _database.queryByTitle(patternResult._title);
				
				if ( list.size() == 0 ) {
					media = new Media();
					media.setDisplayTitle(patternResult._title);
				}
				else if ( list.size() == 1 ) {
					media = list.get(0);
				}
				else {
					//TODO: what to do if there is more than one match??
				}
							
				media._chapterUrl = url;
				media._lastReadDate = new DateTime();
				media._lastReadPlace._volume = patternResult._volume;
				media._lastReadPlace._chapter = patternResult._chapter;
				media._lastReadPlace._subChapter = patternResult._subChapter;
				media._lastReadPlace._page = patternResult._page;
				//media._lastReadPlace._extra = patternResult.;
				media._isSaved = true;
				
				if ( media._id > 0 ) {
					_database.update(media);
				}
				else {
					_database.insert(media);
				}
			}
		}
		catch (DatabaseException e) {
			_logging.error("Saved Media Service", "Exception update from url", e);
			serviceException = new ServiceException("Error updating media from url '" + url + "'", ServiceException.SQL_ERROR);
		}
		finally {
			_connectionManager.close();
		}
		
		if ( serviceException != null ) {
			throw serviceException;
		}
		else {
			return media;
		}
	}

	public List<Media> getSavedList(IListLoadedObserver<Media> observer) throws ServiceException {
		List<Media> list = null;
		ServiceException serviceException = null;
		try {
			_connectionManager.open();
			list = _database.querySavedMedia(observer);
		}
		catch (DatabaseException e) {
			_logging.error("Saved Media Service", "Exception getting saved list", e);
			serviceException = new ServiceException("Error loading saved media", ServiceException.SQL_ERROR);
		}
		finally {
			_connectionManager.close();
		}
		if ( serviceException != null ) {
			throw serviceException;
		}
		else {
			return list;
		}
	}

	public Media updateFromOnlineItem(OnlineMediaItem onlineItem) throws ServiceException {	
		Media media = null;
		ServiceException serviceException = null;
		try {
			_connectionManager.open();	
			if ( onlineItem != null && onlineItem._mediaId > 0 ) {
			//find media item corresponding to the online item
			media = _database.queryById(onlineItem._mediaId); 

			//update media item from online item
			media._chapterUrl = onlineItem._chapterUrl;
			media._lastReadDate = new DateTime();
			media._lastReadPlace._volume = onlineItem._updatedPlace._volume;
			media._lastReadPlace._chapter = onlineItem._updatedPlace._chapter;
			media._lastReadPlace._subChapter = onlineItem._updatedPlace._subChapter;
			media._lastReadPlace._page = onlineItem._updatedPlace._page;
			media._lastReadPlace._extra = onlineItem._updatedPlace._extra;
			media._isSaved = true;
			
			_database.update(media);
			}
			else {
				serviceException = new ServiceException(ServiceException.INVALID_ID);
			}	
		}
		catch (DatabaseException e) {
			_logging.error("Saved Media Service", "Exception update from online item", e);
			serviceException = new ServiceException("Error updating media from online item '" + onlineItem._displayTitle + "'", ServiceException.SQL_ERROR);
		}
		finally {
			_connectionManager.close();
		}	
		if ( serviceException != null ) {
			throw serviceException;
		}
		else {
			return media;
		}
	}

	public OnlineMediaItem loadMediaSummary(long mediaId, String url) throws ServiceException {	
		OnlineMediaItem onlineItem = null;
		
		Optional<IWebsiteParser> matchingParser = _websites.getList().stream()
				.filter(parser -> parser.urlMatches(url))
				.findFirst();
		
		if ( matchingParser.isPresent() ) {
			ServiceException serviceException = null;
			
			try {
				_connectionManager.open();
				Optional<OnlineMediaItem> optOnlineItem = _onlineDatabase.queryByMediaId(mediaId).stream()
					.filter(item -> item._websiteName.equals(matchingParser.get().getInfo()._name))
					.findFirst();	
				if ( optOnlineItem.isPresent() ) {
					onlineItem = optOnlineItem.get();
				}	
			}
			catch (DatabaseException e) {
				_logging.error("Saved Media Service", "Exception update from online item", e);
				serviceException = new ServiceException("Error loading summary for media item " + mediaId + " from " + url, ServiceException.SQL_ERROR);
			}
			finally {
				_connectionManager.close();
			}
			
	
			if ( onlineItem == null ) {
				onlineItem = new OnlineMediaItem();
				onlineItem._mediaId = mediaId;
			}
			onlineItem._titleUrl = url;
			matchingParser.get().loadItem(onlineItem);
			
			try {
				_connectionManager.open();
				
				if ( onlineItem._id > 0 ) {
					_onlineDatabase.update(onlineItem);	
				}
				else {					
					_genreDatabase.updateGenreList(onlineItem._genres, mediaId);		
				}	
			}
			catch (DatabaseException e) {
				_logging.error("Saved Media Service", "Exception update from online item", e);
				serviceException = new ServiceException("Error loading summary for media item " + mediaId + " from " + url, ServiceException.SQL_ERROR);
			}
			finally {
				_connectionManager.close();
			}	
			if ( serviceException != null ) {
				throw serviceException;
			}
		}
		return onlineItem;
	}

	public Media update(Media item) throws ServiceException {
		ServiceException serviceException = null;
		try {
			_connectionManager.open();
			_database.update(item);	
		}
		catch (DatabaseException e) {
			_logging.error("Saved Media Service", "Exception updating item", e);
			serviceException = new ServiceException("Error saving update for media item " + item._id, ServiceException.SQL_ERROR);
		}
		finally {
			_connectionManager.close();
		}
		if ( serviceException != null ) {
			throw serviceException;
		}
		return item;
	}
}
