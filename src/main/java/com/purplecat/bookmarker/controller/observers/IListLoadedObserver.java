package com.purplecat.bookmarker.controller.observers;

import java.util.List;

public interface IListLoadedObserver<T> {
	public void notifyItemLoaded(T item, int index, int total);
	public void notifyListLoaded(List<T> list);
}
