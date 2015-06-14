package com.purplecat.bookmarker.services.databases;

import java.util.List;

public interface IItemRepository<T> {

	public List<T> query() throws DatabaseException;

	public T queryById(long id) throws DatabaseException;

	public void insert(T item) throws DatabaseException;

	public void update(T item) throws DatabaseException;

	public void delete(long id) throws DatabaseException;
}
