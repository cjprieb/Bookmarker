package com.purplecat.bookmarker.services;

import java.util.List;

import com.purplecat.bookmarker.models.BaseDatabaseItem;
import com.purplecat.bookmarker.services.databases.IDatabaseConnector;

public class BaseDatabaseItemService<T extends BaseDatabaseItem> implements IItemService<T> {
	
	IDatabaseConnector<T> _database;
	
	public BaseDatabaseItemService(IDatabaseConnector<T> database) {
		_database = database;
	}

	@Override
	public List<T> list() throws ServiceException {
		return _database.query();
	}

	@Override
	public void add(T item) throws ServiceException {
		_database.insert(item);
	}

	@Override
	public void edit(T item) throws ServiceException {
		if ( item._id <= 0 ) {
			throw new ServiceException(ServiceException.INVALID_ID);
		}
		
		_database.update(item);
	}

	@Override
	public void remove(long id) throws ServiceException {
		_database.delete(id);
	}

	@Override
	public T get(long id) throws ServiceException {
		if ( id <= 0 ) {
			throw new ServiceException(ServiceException.INVALID_ID);
		}
		
		List<T> list = _database.query(id);
		if ( list.size() > 0 ) {
			return list.get(0);
		}
		return null;
	}
}
