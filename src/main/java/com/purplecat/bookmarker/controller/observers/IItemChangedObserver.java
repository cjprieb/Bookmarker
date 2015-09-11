package com.purplecat.bookmarker.controller.observers;

public interface IItemChangedObserver<T> {	
	public void notifyItemUpdated(T item);
}
