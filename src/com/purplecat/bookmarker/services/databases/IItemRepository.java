package com.purplecat.bookmarker.services.databases;

import java.util.List;

public interface IItemRepository<T> {

	public List<T> query();

	public T queryById(long id);

	public void insert(T item);

	public void update(T item);

	public void delete(long id);
}
