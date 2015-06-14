package com.purplecat.bookmarker.services;

import java.util.List;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.UrlPatternResult;
import com.purplecat.bookmarker.services.databases.DatabaseException;
import com.purplecat.bookmarker.services.databases.IMediaRepository;
import com.purplecat.bookmarker.sql.IConnectionManager;
import com.purplecat.commons.logs.ILoggingService;

public class SavedMediaService {
	public final ILoggingService _logging;
	public final IConnectionManager _connectionManager;
	public final IMediaRepository _database;
	public final UrlPatternService _patterns;
	
	@Inject
	public SavedMediaService(ILoggingService logging, IConnectionManager mgr, IMediaRepository database, UrlPatternService patterns) {
		_logging = logging;
		_connectionManager = mgr;
		_database = database;
		_patterns = patterns;
	}
	
//	public IMediaRepository getRepository() {
//		return _database;
//	}

//	public void add(Media item) throws ServiceException {
//		throw new UnsupportedOperationException();
		//_database.insert(item);
//	}

//	public void edit(Media item) throws ServiceException {
//		throw new UnsupportedOperationException();
//		if ( item._id <= 0 ) {
//			throw new ServiceException(ServiceException.INVALID_ID);
//		}
//		
//		_database.update(item);
//	}

//	public void remove(long id) throws ServiceException {
//		throw new UnsupportedOperationException();
//		_database.delete(id);
//	}

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
		UrlPatternResult patternResult = _patterns.match(url);
		
		Media media = null;
		if ( patternResult != null && patternResult._title != null && patternResult._title.length() > 0 ) {
			try {
				List<Media> list = _database.queryByTitle(patternResult._title);
				
				if ( list.size() == 0 ) {
					media = new Media();
					media._displayTitle = patternResult._title;
				}
				else if ( list.size() == 1 ) {
					media = list.get(0);
				}
				else {
					//TODO: what to do if there is more than one match??
				}
							
				media._chapterURL = url;
				media._lastReadDate = new DateTime();
				media._lastReadPlace._volume = patternResult._volume;
				media._lastReadPlace._chapter = patternResult._chapter;
				media._lastReadPlace._subChapter = patternResult._subChapter;
				media._lastReadPlace._page = patternResult._page;
				//media._lastReadPlace._extra = patternResult.;
				media._isSaved = true;
				
				_database.update(media);
			}
			catch (DatabaseException e) {
				throw new ServiceException("Error updating media from url '" + url + "'", ServiceException.SQL_ERROR);
			}
		}
		
		return media;
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

	public Media updateFromOnlineItem(OnlineMediaItem onlineItem) throws ServiceException  {		
		Media media = null;
		if ( onlineItem != null && onlineItem._mediaId > 0 ) {
			try {
				//find media item corresponding to the online item
				media = _database.queryById(onlineItem._mediaId); 
	
				//update media item from online item
				media._chapterURL = onlineItem._chapterUrl;
				media._lastReadDate = new DateTime();
				media._lastReadPlace._volume = onlineItem._updatedPlace._volume;
				media._lastReadPlace._chapter = onlineItem._updatedPlace._chapter;
				media._lastReadPlace._subChapter = onlineItem._updatedPlace._subChapter;
				media._lastReadPlace._page = onlineItem._updatedPlace._page;
				media._lastReadPlace._extra = onlineItem._updatedPlace._extra;
				media._isSaved = true;
				
				_database.update(media);
			}
			catch (DatabaseException e) {
				throw new ServiceException("Error updating media from online item '" + onlineItem._displayTitle + "'", ServiceException.SQL_ERROR);
			}
		}
		else {
			throw new ServiceException(ServiceException.INVALID_ID);
		}
		
		return media;
	}
}
