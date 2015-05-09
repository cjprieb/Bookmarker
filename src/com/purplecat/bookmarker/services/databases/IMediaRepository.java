package com.purplecat.bookmarker.services.databases;

import java.util.List;

import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.ServiceException;

public interface IMediaRepository {

	public List<Media> queryByTitle(String title);

	public List<Media> querySavedMedia() throws ServiceException;

	//public List<Media> queryNonSavedMedia() throws ServiceException;

	public List<Media> query();

	public Media queryById(long id);

	public void insert(Media item) throws ServiceException;

	public void update(Media item) throws ServiceException;

	public void delete(long id);
	
	// throws DatabaseException
	
}