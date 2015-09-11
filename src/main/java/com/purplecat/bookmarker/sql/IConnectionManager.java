package com.purplecat.bookmarker.sql;

import com.purplecat.bookmarker.services.databases.DatabaseException;

public interface IConnectionManager {

	public void open() throws DatabaseException;

	public void close();

}