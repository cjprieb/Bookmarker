package com.purplecat.bookmarker.services.databases;

import java.util.List;

import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.ServiceException;

public interface IMangaDatabaseConnector extends IDatabaseConnector<Media> {

	public List<Media> querySavedMedia() throws ServiceException;

	public List<Media> queryNonSavedMedia() throws ServiceException;

	public List<Media> queryByTitle(String title);

}