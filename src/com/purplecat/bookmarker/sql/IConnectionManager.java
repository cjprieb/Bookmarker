package com.purplecat.bookmarker.sql;

import java.sql.Connection;

import com.purplecat.bookmarker.services.databases.DatabaseException;

public interface IConnectionManager {

	public void open() throws DatabaseException;

	public Connection getConnection() throws DatabaseException;

	public void close();

}