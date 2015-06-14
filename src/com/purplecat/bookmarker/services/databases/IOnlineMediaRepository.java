package com.purplecat.bookmarker.services.databases;

import java.util.List;

import com.purplecat.bookmarker.models.OnlineMediaItem;

public interface IOnlineMediaRepository {
	
	public List<OnlineMediaItem> query() throws DatabaseException;
	
	public OnlineMediaItem queryById(long id) throws DatabaseException;

	public OnlineMediaItem findOrCreate(OnlineMediaItem item) throws DatabaseException;

	public void insert(OnlineMediaItem item) throws DatabaseException;
	
	public void update(OnlineMediaItem item) throws DatabaseException;
	
	public void delete(long id) throws DatabaseException;

}
