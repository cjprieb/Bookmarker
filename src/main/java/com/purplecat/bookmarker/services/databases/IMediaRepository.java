package com.purplecat.bookmarker.services.databases;

import java.util.List;

import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.ServiceException;

public interface IMediaRepository {

	public List<Media> queryByTitle(String title) throws DatabaseException;

	public List<Media> querySavedMedia(IListLoadedObserver<Media> observer) throws DatabaseException;

	public List<Media> query() throws DatabaseException;

	public Media queryById(long id) throws DatabaseException;

	public void insert(Media item) throws DatabaseException, ServiceException;

	public void update(Media item) throws DatabaseException;

	public void delete(long id) throws DatabaseException;	
}