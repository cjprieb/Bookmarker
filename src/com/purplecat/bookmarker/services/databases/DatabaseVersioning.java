package com.purplecat.bookmarker.services.databases;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import com.purplecat.bookmarker.extensions.PlaceExt;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.commons.extensions.DateTimeFormats;
import com.purplecat.commons.logs.ILoggingService;

public class DatabaseVersioning {
	public static final int VERSION_11 = 11;	//all prior databases should start here
	public static final int VERSION_12 = 12;	//moved recent history to media table.
	public static final int CURRENT_VERSION = VERSION_12;
	
	private int _currentVersion;
	private ILoggingService _log;
	private String _connectionPath;
	private String _filePath;
	
	public DatabaseVersioning(ILoggingService log, String databasePath, String filePath) {
		_log = log;
		_connectionPath = databasePath;
		_filePath = filePath;
	}
	
	public void setupDatabase() {
		File dbFile = new File(_filePath);
		if ( dbFile.exists() ) {
			_currentVersion = getVersion();
			if ( _currentVersion > -1 ) { 
				if ( _currentVersion <= 11 ) {
					//current version of program starts at version 11 database or no database
					_currentVersion = updateToVersion12();					
				}
			}
		} 
		else {
			_currentVersion = createDatabase();
		}
	}
	
	public int createDatabase() {
		//TODO: create new database
		return CURRENT_VERSION;
	}
	
	public int updateToVersion12() {
		
		List<Media> list = new LinkedList<Media>();
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			conn.setAutoCommit(false);
			
			String sqlStr = "SELECT Media._id, hist.svhstDate, hist.svhstPlace FROM MEDIA" + 
						" LEFT JOIN (SELECT svhstMedia_id, max(svhstDate) maxDate FROM savedhistory GROUP BY svhstMedia_id) " +
							" maxhist on maxhist.svhstMedia_id = media._id " +
						" LEFT JOIN savedhistory hist on hist.svhstMedia_id = media._id AND maxDate = hist.svhstDate";
			Statement stmt = conn.createStatement();
			ResultSet result = stmt.executeQuery(sqlStr);
			while ( result.next() ) {
				Media media = new Media();
				media._id = result.getLong(1);
				media._lastReadDate = DateTimeFormats.FORMAT_SQLITE_DATE.parseOrDefault(result.getString(2), null);
				media._lastReadPlace = PlaceExt.parse(result.getString(3));
				list.add(media);
			}
			
			String addCols = "";

			conn.commit();
		} catch (SQLException e) {
			_log.error("updateToVersion12", "Update failed", e);
		} 
		
		return VERSION_12;
	}
	
	//TODO: move to separate class for sqlite functions
	public int getVersion() {
		int version = -1;
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			ResultSet result = conn.createStatement().executeQuery("PRAGMA user_version");
			if ( result.next() ) {
				version = result.getInt(1);				
			}
		} catch (SQLException e) {
			_log.error("DatabaseVersioning", "Unable to find version", e);			
		}
		return version;
	}
	
	public void setVersion(int version) {
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			conn.createStatement().execute("PRAGMA user_version = " + version);			
		} catch (SQLException e) {
			_log.error("DatabaseVersioning", "Unable to set version to " + version, e);				
		}
	}

}
