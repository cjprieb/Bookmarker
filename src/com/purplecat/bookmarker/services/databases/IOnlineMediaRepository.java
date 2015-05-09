package com.purplecat.bookmarker.services.databases;

import java.util.List;

import com.purplecat.bookmarker.models.OnlineMediaItem;

public interface IOnlineMediaRepository {
	
	public List<OnlineMediaItem> query();
	
	public OnlineMediaItem queryById(long id);

	public OnlineMediaItem findOrCreate(OnlineMediaItem item);

	public void insert(OnlineMediaItem item);
	
	public void update(OnlineMediaItem item);
	
	public void delete(long id);

}
