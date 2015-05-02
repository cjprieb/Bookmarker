package com.purplecat.bookmarker.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.purplecat.bookmarker.models.BaseDatabaseItem;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.UrlPattern;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.databases.IDatabaseConnector;
import com.purplecat.bookmarker.services.databases.IMangaDatabaseConnector;
import com.purplecat.bookmarker.services.databases.IUrlPatternDatabase;
import com.purplecat.commons.tests.Utils;

public abstract class SampleDatabaseService<T extends BaseDatabaseItem> implements IDatabaseConnector<T> {
	
	Map<Long, T> _map = new HashMap<Long, T>();
	int _maxIndex = 0;

	@Override
	public List<T> query(long id) {
		List<T> list = new ArrayList<T>();
		if ( _map.containsKey(id) ) {
			list.add(copy(_map.get(id)));
		}
		return list;
	}

	@Override
	public List<T> query() {
		List<T> list = new ArrayList<T>();
		for ( T item : _map.values() ) {
			list.add(copy(item));
		}
		return list; 
	}

	@Override
	public void insert(T item) {
		if ( item._id <= 0 ){
			_maxIndex++;
			item._id = _maxIndex;
		}
		_map.put(item._id, item);
	}

	@Override
	public void update(T item) {
		_map.put(item._id, item);
	}

	@Override
	public void delete(long id) {
		_map.remove(id);
	}
	
	public abstract T copy(T item);
	
	public static class SamplePatternDatabase extends SampleDatabaseService<UrlPattern> implements IUrlPatternDatabase {
		public SamplePatternDatabase() {
			List<String> lines = Utils.getFile(getClass(), "/resources/SampleUrlPatterns.txt");
			for (String line : lines) {
				String[] tokens = line.split("\t");
				UrlPattern item = new UrlPattern();
				item._patternString = tokens[0];
				for (int i = 1; i < tokens.length; i++) {
					item._map.put(tokens[i], i-1);
				}
				item._pattern = Pattern.compile(item._patternString);
				insert(item);
			}
		}
		
		@Override
		public UrlPattern copy(UrlPattern item) {
			return item.copy();
		}
	}
	
	public static class SampleMangaDatabase extends SampleDatabaseService<Media> implements IMangaDatabaseConnector {
		
		public SampleMangaDatabase() {
			Media manga1 = new Media();
			insert(manga1);

			Media manga2 = new Media();
			insert(manga2);
		}
		
		@Override
		public Media copy(Media item) {
			return item.copy();
		}
		
		@Override
		public List<Media> querySavedMedia() throws ServiceException {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public List<Media> queryNonSavedMedia() throws ServiceException {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public List<Media> queryByTitle(String title) {
			// TODO Auto-generated method stub
			Media m = new Media();
			m._displayTitle = title;
			m._id = 10;
			insert(m);
			
			List<Media> list = new LinkedList<Media>();
			list.add(m);
			return list;
		}
	}

}
