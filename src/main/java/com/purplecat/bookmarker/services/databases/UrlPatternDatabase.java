package com.purplecat.bookmarker.services.databases;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import com.google.inject.Inject;
import com.purplecat.bookmarker.models.UrlPattern;
import com.purplecat.bookmarker.models.UrlPatternType;
import com.purplecat.bookmarker.sql.ConnectionManager;
import com.purplecat.bookmarker.sql.NamedResultSet;
import com.purplecat.bookmarker.sql.NamedStatement;
import com.purplecat.commons.logs.ILoggingService;

public class UrlPatternDatabase implements IUrlPatternDatabase {
	public static final String TAG = "UrlPatternDatabase";
	private static String SELECT_ITEMS = "SELECT _id, PatPattern _patternString, PatGroups, PatType _type FROM UrlPattern";
	
	public final ILoggingService _logging;
	public final ConnectionManager _connectionManager;
	 
	@Inject
	public UrlPatternDatabase(ILoggingService logger, ConnectionManager mgr) {
		_logging = logger;
		_connectionManager = mgr;
	}
	
	/**
	 * Assumes the SELECT_ITEMS query was used
	 * @param result
	 * @return
	 * @throws SQLException 
	 */
	private UrlPattern loadItemFromResultSet(NamedResultSet result) throws SQLException {
		UrlPattern item = new UrlPattern();
		item._id = result.getLong("_id");
		item._patternString = result.getString("_patternString");
		item._pattern = Pattern.compile(item._patternString);
		item._type = Enum.valueOf(UrlPatternType.class, result.getString("_type"));
		
		String mappings = result.getString("PatGroups");
		String[] groups = mappings.split("\\|");
		for ( int i = 0; i < groups.length; i++ ) {
			item._map.put(groups[i], i);
		}
		
		return item;
	}
	
	private String formatMaps(Map<String, Integer> maps) {
		int maxIndex = -1;
		for ( int value : maps.values() ) {
			if ( value > maxIndex ) { maxIndex = value; }
		}
		
		String[] tokens = new String[maxIndex+1];
		for ( String key : maps.keySet() ) {
			tokens[maps.get(key)] = key;
		}
		
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for ( String key : tokens ) {
			if ( i > 0 ) { builder.append("|"); }
			builder.append(key != null && key.length() > 0 ? key : "NONE");
			i++;
		}
		return builder.toString();
	}

	@Override
	public List<UrlPattern> query() throws DatabaseException {
		List<UrlPattern> list = new LinkedList<UrlPattern>();
		try {
			Connection conn = _connectionManager.getConnection();
			Statement stmt = conn.createStatement();
			NamedResultSet result = new NamedResultSet(stmt.executeQuery(SELECT_ITEMS));
			while ( result.next() ) {
				list.add(loadItemFromResultSet(result));
			}
		} catch (SQLException e) {
			throw new DatabaseException("query failed", SELECT_ITEMS, e);
		} 
		return list;
	}

	@Override
	public UrlPattern queryById(long id) throws DatabaseException {
		UrlPattern pattern = null;
		String sql = SELECT_ITEMS + " WHERE _id = @id";
		try {
			Connection conn = _connectionManager.getConnection();
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setLong("@id", id);
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				pattern = loadItemFromResultSet(result);
			}
		} catch (SQLException e) {
			throw new DatabaseException("queryById failed", sql, e);
		} 
		return pattern;
	}

	@Override
	public void insert(UrlPattern item) throws DatabaseException {
		if ( item._id <= 0 ) {
			String sql = "";
			try {
				Connection conn = _connectionManager.getConnection();
				sql = "INSERT INTO UrlPattern (PatPattern, PatGroups, PatType) VALUES (@pattern, @groups, @type)";
				NamedStatement stmt = new NamedStatement(conn, sql);
				stmt.setString("@pattern", item._patternString);
				stmt.setString("@groups", formatMaps(item._map));
				stmt.setString("@type", item._type.toString());
				item._id = stmt.executeInsert();
			} catch (SQLException e) {
				throw new DatabaseException("insert failed", sql, e);
			}
		}
		else {
			_logging.error(TAG, "Unable to insert: invalid id");
		}
	}

	@Override
	public void update(UrlPattern item) throws DatabaseException {
		if ( item._id > 0 ) {
			String sql = "";
			try {	
				Connection conn = _connectionManager.getConnection();			
				sql = "UPDATE UrlPattern SET PatPattern = @pattern, PatGroups = @groups, PatType = @type WHERE _id = @id";
				NamedStatement stmt = new NamedStatement(conn, sql);
				stmt.setLong("@id", item._id);
				stmt.setString("@pattern", item._patternString);
				stmt.setString("@groups", formatMaps(item._map));
				stmt.setString("@type", item._type.toString());
				stmt.executeUpdate();
			} catch (SQLException e) {
				throw new DatabaseException("update failed", sql, e);
			}
		}
		else {
			_logging.error(TAG, "Unable to update: invalid id");
		}
	}

	@Override
	public void delete(long id) throws DatabaseException {
		String sql = "DELETE FROM UrlPattern WHERE _id = @id";
		try {
			Connection conn = _connectionManager.getConnection();
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setLong("@id", id);
			stmt.execute();
		} catch (SQLException e) {
			throw new DatabaseException("delete failed", sql, e);
		}	
	}

}
