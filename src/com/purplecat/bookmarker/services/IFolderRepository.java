package com.purplecat.bookmarker.services;

import java.util.List;

import com.purplecat.bookmarker.models.Folder;
import com.purplecat.bookmarker.services.databases.DatabaseException;

public interface IFolderRepository {
	public List<Folder> query() throws DatabaseException;
}
