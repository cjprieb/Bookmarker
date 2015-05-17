package com.purplecat.bookmarker.test.dummies;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.purplecat.bookmarker.models.BaseDatabaseItem;
import com.purplecat.bookmarker.models.UrlPattern;
import com.purplecat.bookmarker.services.databases.IItemRepository;
import com.purplecat.bookmarker.services.databases.IUrlPatternDatabase;
import com.purplecat.commons.tests.Utils;

public abstract class SampleDatabaseService<T extends BaseDatabaseItem> implements IItemRepository<T> {
	
	Map<Long, T> _map = new HashMap<Long, T>();
	int _maxIndex = 0;

	@Override
	public T queryById(long id) {
		if ( _map.containsKey(id) ) {
			return copy(_map.get(id));
		}
		return null;
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
				insert(item);
			}
		}
		
		@Override
		public UrlPattern copy(UrlPattern item) {
			return item.copy();
		}
	}

}
