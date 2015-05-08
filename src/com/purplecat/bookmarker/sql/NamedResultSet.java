package com.purplecat.bookmarker.sql;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import com.purplecat.commons.extensions.DateTimeFormats;

public class NamedResultSet {
	private ResultSet _result;
	private Map<String, Integer> _columns;
	private DateTimeFormatter _formatter = DateTimeFormat.forPattern(DateTimeFormats.SQLITE_DATE_FORMAT);
	
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
	
	private int getColumn(String name) throws SQLException {
		if ( _columns.get(name) == null ) {
			throw new SQLException("The column " + name + " was not found");
		}
		return _columns.get(name);
	}

	public double getDouble(String name) throws SQLException {
		return _result.getDouble(getColumn(name));
	}

	public boolean getBoolean(String name) throws SQLException {
		return _result.getBoolean(getColumn(name));
	}

	public Calendar getDateFromString(String name) throws SQLException {
		String dateString = _result.getString(getColumn(name));
		if ( dateString != null && dateString.length() > 0 ) {
			return DateTimeFormats.FORMAT_SQLITE_DATE.parseOrDefault(dateString, null);
		}
		return null;
	}

	public DateTime getDateTimeFromString(String name) throws SQLException {
		String dateString = _result.getString(getColumn(name));
		if ( dateString != null && dateString.length() > 0 ) {
			return _formatter.parseDateTime(dateString);
		}
		return null;
	}

	public int getInt(String name) throws SQLException {
		return _result.getInt(getColumn(name));
	}

	public long getLong(String name) throws SQLException {
		return _result.getLong(getColumn(name));
	}

	public String getString(String name) throws SQLException {
		return _result.getString(getColumn(name));
	}

	public boolean next() throws SQLException {
		return _result.next();
	}
	
	
	
}
