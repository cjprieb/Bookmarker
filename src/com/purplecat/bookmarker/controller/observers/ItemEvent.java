package com.purplecat.bookmarker.controller.observers;

public class ItemEvent<T> {
	public final T _item;
	public final int _index;
	public final int _total;
	
	public ItemEvent(T item, int index, int total) {
		_item = item;
		_index = index;
		_total = total;
	}	

}
