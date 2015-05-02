package com.purplecat.bookmarker.controller.observers;

import java.util.List;

public interface IListLoadedObserver<T> {
	public void notifyListLoaded(List<T> list);
}
