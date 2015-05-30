package com.purplecat.bookmarker.services;

import java.util.List;

import org.joda.time.DateTime;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.UrlPatternResult;
import com.purplecat.bookmarker.services.databases.IMediaRepository;

public class SavedMediaService {
	
	public final IMediaRepository _database;
	public final UrlPatternService _patterns;
	
	@Inject
	public SavedMediaService(IMediaRepository database, UrlPatternService patterns) {
		_database = database;
		_patterns = patterns;
	}
	
	public IMediaRepository getRepository() {
		return _database;
	}

	public void add(Media item) throws ServiceException {
		_database.insert(item);
	}

	public void edit(Media item) throws ServiceException {
		if ( item._id <= 0 ) {
			throw new ServiceException(ServiceException.INVALID_ID);
		}
		
		_database.update(item);
	}

	public void remove(long id) throws ServiceException {
		_database.delete(id);
	}

	public Media get(long id) throws ServiceException {
		if ( id <= 0 ) {
			throw new ServiceException(ServiceException.INVALID_ID);
		}
		return _database.queryById(id);
	}
	
	public Media updateFromUrl(String url) throws ServiceException {
		UrlPatternResult patternResult = _patterns.match(url);
		
		Media media = null;
		if ( patternResult != null && patternResult._title != null && patternResult._title.length() > 0 ) {
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
		
		return media;
	}

	public List<Media> getSavedList(IListLoadedObserver<Media> observer) throws ServiceException {
		return _database.querySavedMedia(observer);
	}

	public Media updateFromOnlineItem(OnlineMediaItem onlineItem) throws ServiceException  {		
		Media media = null;
		if ( onlineItem != null && onlineItem._mediaId > 0 ) {
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
		else {
			throw new ServiceException(ServiceException.INVALID_ID);
		}
		
		return media;
	}
}
