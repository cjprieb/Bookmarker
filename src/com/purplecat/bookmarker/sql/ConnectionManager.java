package com.purplecat.bookmarker.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.purplecat.bookmarker.services.databases.DatabaseException;
import com.purplecat.commons.logs.ILoggingService;

public class ConnectionManager {	
	private static String TAG = "ConnectionManager";
	
	private final String _connectionPath;
	private final ILoggingService _logger;

	private Connection _connection = null;
	private boolean _inUse;
	
	@Inject
	public ConnectionManager(ILoggingService logger, @Named("JDBC URL") String dbPath) {
		_logger = logger;
		_connectionPath = dbPath;
	}
	
	public synchronized void open() throws DatabaseException {
			try {
				while ( _inUse ) {
					this.wait(100);
				}
			} catch (InterruptedException e) {
				_logger.error(TAG, "open() wait interrupted", e);
			}
		try {
			_connection = DriverManager.getConnection(_connectionPath);
			_inUse = true;
		} catch (SQLException e) {
			throw new DatabaseException("Error opening connection for " + _connectionPath, e);
		}
	}
	
	public Connection getConnection() throws DatabaseException {
		if ( _connection != null ) {
			return _connection;
		}
		else {
			throw new DatabaseException("Connection has not been opened yet. Call open() first.");
		}
	}
	
	public synchronized void close() {
		if ( _connection != null ) {
			try {
				_connection.close();
			} catch (SQLException e) {
				_logger.error(TAG, "Error closing connection for " + _connectionPath, e);
			}
		}
		_inUse = false;
		_connection = null;
	}

}
