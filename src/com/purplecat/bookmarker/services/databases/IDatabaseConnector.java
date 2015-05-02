package com.purplecat.bookmarker.services.databases;

import java.util.List;

public interface IDatabaseConnector<T> {

	public List<T> query();

	public List<T> query(long id);

	public void insert(T item);

	public void update(T item);

	public void delete(long id);
}
