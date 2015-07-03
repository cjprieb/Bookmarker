package com.purplecat.bookmarker.test.dummies;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.purplecat.bookmarker.models.BaseDatabaseItem;
import com.purplecat.bookmarker.models.Genre;
import com.purplecat.bookmarker.models.UrlPattern;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.bookmarker.services.databases.IUrlPatternDatabase;
import com.purplecat.commons.tests.Utils;

public abstract class SampleDatabaseService<T extends BaseDatabaseItem> {
	
	Map<Long, T> _map = new HashMap<Long, T>();
	int _maxIndex = 0;

	public T queryById(long id) {
		if ( _map.containsKey(id) ) {
			return copy(_map.get(id));
		}
		return null;
	}

	public List<T> query() {
		List<T> list = new ArrayList<T>();
		for ( T item : _map.values() ) {
			list.add(copy(item));
		}
		return list; 
	}

	public void insert(T item) {
		if ( item._id <= 0 ){
			_maxIndex++;
			item._id = _maxIndex;
		}
		_map.put(item._id, item);
	}

	public void update(T item) {
		if ( item._id > 0 ) {
			_map.put(item._id, item);
		}
		else {
			throw new NullPointerException("Invalid id; cannot update");
		}
	}

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
				insert(item);
			}
		}
		
		@Override
		public UrlPattern copy(UrlPattern item) {
			return item.copy();
		}
	}
	
	public static class SampleGenreDatabase implements IGenreRepository {
		Map<Long, Genre> _genres = new HashMap<Long, Genre>();
		
		public SampleGenreDatabase() {
//			List<String> lines = Utils.getFile(getClass(), "/resources/SampleGenres.txt");
//			for (String line : lines) {
//				String[] tokens = line.split("\t");
//				UrlPattern item = new UrlPattern();
//				item._patternString = tokens[0];
//				for (int i = 1; i < tokens.length; i++) {
//					item._map.put(tokens[i], i-1);
//				}
//				insert(item);
//			}
		}

		@Override
		public List<Genre> query() {
			List<Genre> list = new ArrayList<Genre>();
			list.addAll(_genres.values());
			return list;
		}

		@Override
		public List<Genre> queryByMediaId(long id) {
			return new ArrayList<Genre>();
		}

		@Override
		public boolean updateGenreList(Collection<Genre> list, long mediaId) {
			return false;
		}

		@Override
		public Genre find(String text) {
			for ( Genre genre : _genres.values() ) {
				if ( genre._name.equalsIgnoreCase(text) ) {
					return genre;
				}
			}
			Genre genre = new Genre();
			genre._id = 0;
			genre._name = text;
			return genre;
		}
	}

}
