package com.purplecat.bookmarker.services;

import java.util.List;

/**
 * Do not run on UI thread. Tasks may query the database or 
 * otherwise take time.
 * @author cprieb
 *
 * @param <T>
 */
public interface IItemService<T> {
	
	public List<T> list() throws ServiceException;
	
	public void add(T item) throws ServiceException;
	
	public void edit(T item) throws ServiceException;
	
	public void remove(long id) throws ServiceException;
	
	public T get(long id) throws ServiceException;
	
}
