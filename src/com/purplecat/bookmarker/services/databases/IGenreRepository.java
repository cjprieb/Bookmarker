package com.purplecat.bookmarker.services.databases;

import java.util.Collection;
import java.util.List;

import com.purplecat.bookmarker.models.Genre;

public interface IGenreRepository {

	public List<Genre> query() throws DatabaseException;
	
	public List<Genre> queryByMediaId(long id) throws DatabaseException;
	
	public boolean updateGenreList(Collection<Genre> list, long mediaId) throws DatabaseException;

	public Genre find(String text) ;
}
