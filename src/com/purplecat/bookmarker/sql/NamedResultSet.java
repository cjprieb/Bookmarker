package com.purplecat.bookmarker.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import com.purplecat.commons.extensions.DateTimeFormats;

public class NamedResultSet {
	private ResultSet _result;
	private Map<String, Integer> _columns;
	
	public NamedResultSet(ResultSet result) throws SQLException {
		_result = result;
		_columns = new HashMap<String, Integer>();
		
		setupColumns();
	}
	
	private void setupColumns() throws SQLException {
		ResultSetMetaData metadata = _result.getMetaData();
		int columnCount = metadata.getColumnCount();
		
		//Column count starts at one
		for ( int i = 1; i <= columnCount; i++ ) {
			_columns.put(metadata.getColumnLabel(i), i);
		}
	}

	public boolean getBoolean(String name) throws SQLException {
		return _result.getBoolean(_columns.get(name));
	}

	public Calendar getDateFromString(String name) throws SQLException {
		return DateTimeFormats.FORMAT_SQLITE_DATE.parseOrDefault(_result.getString(_columns.get(name)), null);
	}

	public int getInt(String name) throws SQLException {
		return _result.getInt(_columns.get(name));
	}

	public long getLong(String name) throws SQLException {
		return _result.getLong(_columns.get(name));
	}

	public String getString(String name) throws SQLException {
		return _result.getString(_columns.get(name));
	}

	public boolean next() throws SQLException {
		return _result.next();
	}
	
	
	
}
