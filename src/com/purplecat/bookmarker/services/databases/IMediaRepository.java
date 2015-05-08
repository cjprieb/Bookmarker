package com.purplecat.bookmarker.services.databases;

import java.util.List;

import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.ServiceException;

public interface IMediaRepository extends IItemRepository<Media> {

	public List<Media> queryByTitle(String title);

	public List<Media> querySavedMedia() throws ServiceException;

	public List<Media> queryNonSavedMedia() throws ServiceException;
	
}