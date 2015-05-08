package com.purplecat.bookmarker.services;

import java.util.Calendar;
import java.util.List;

import com.google.inject.Inject;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.UrlPatternResult;
import com.purplecat.bookmarker.services.databases.IMediaRepository;

public class DatabaseMangaService {
	
	public final IMediaRepository _database;
	public final UrlPatternService _patterns;
	
	@Inject
	public DatabaseMangaService(IMediaRepository database, UrlPatternService patterns) {
		_database = database;
		_patterns = patterns;
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
			media._lastReadDate = Calendar.getInstance();
			/*media._lastReadPlace._volume = patternResult._volume;
			media._lastReadPlace._chapter = patternResult._chapter;
			media._lastReadPlace._subChapter = patternResult._subChapter;
			media._lastReadPlace._page = patternResult._page;
			media._lastReadPlace._extra = patternResult._extra;*/
			media._isSaved = true;
			
			_database.update(media);
		}
		
		return media;
	}

	public List<Media> getSavedList() throws ServiceException {
		System.out.println("getting saved list: " + _database.getClass());
		return _database.querySavedMedia();
	}
}
