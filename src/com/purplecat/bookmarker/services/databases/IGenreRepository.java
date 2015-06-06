package com.purplecat.bookmarker.services.databases;

import java.util.List;

import com.purplecat.bookmarker.models.Genre;

public interface IGenreRepository {

	public List<Genre> query();
	
	public List<Genre> queryByMediaId(long id);
	
	public boolean updateGenreList(List<Genre> list, long mediaId);

	public Genre find(String text);
}
