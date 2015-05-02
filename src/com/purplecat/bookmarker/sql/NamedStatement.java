package com.purplecat.bookmarker.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.purplecat.commons.extensions.DateTimeFormats;

public class NamedStatement {
	private static Pattern _pattern = Pattern.compile("([^a-zA-Z0-9])(@[A-Za-z0-9]+)\\b");
	
	private static final int INTEGER 	= 1; 
	private static final int LONG 		= 2;
	private static final int STRING 	= 3;
	private static final int BOOLEAN 	= 4;
	private static final int DATE 		= 5;
	//Remember to add update the SWITCH statement for preparing statements
	
	private Connection _connection;
	private String _preparedSql;
	private HashMap<String, NamedParameter> _namedParameters = new HashMap<String, NamedParameter>();
	
	public NamedStatement(Connection conn, String sql) {
		_connection = conn;
		parseSQL(sql);
	}
	
	public void setBoolean(String name, boolean value) {
		getParameter(name, BOOLEAN)._value = value;
	}
	
	public void setDate(String name, Calendar value) {
		getParameter(name, DATE)._value = value;
	}
	
	public void setInt(String name, int value) {
		getParameter(name, INTEGER)._value = value;
	}
	
	public void setLong(String name, long value) {
		getParameter(name, LONG)._value = value;
	}

	public void setString(String name, String value) {
		getParameter(name, STRING)._value = value;
	}
	
	public NamedResultSet executeQuery() throws SQLException {
		return new NamedResultSet(prepareStatement().executeQuery());
	}
	
	public long executeInsert() throws SQLException {
		long id = -1;
		PreparedStatement stmt = prepareStatement();
		stmt.executeUpdate();
		ResultSet result = stmt.getGeneratedKeys();
		while ( result.next() ) {
			id = result.getLong(1);
			break;
		}
		return id;
	}
	
	public void executeUpdate() throws SQLException {
		prepareStatement().executeUpdate();
	}
	
	public void execute() throws SQLException {
		prepareStatement().execute();
	}
	
	private NamedParameter getParameter(String name, int type) {
		NamedParameter param = _namedParameters.get(name); 
		if ( param == null ) {
			param = new NamedParameter();
			_namedParameters.put(name, new NamedParameter());
		}
		param._type = type;
		return param;
	}

	public String getFormattedString() {
		return _preparedSql;
	}
	
	private PreparedStatement prepareStatement() throws SQLException {
		PreparedStatement stmt = _connection.prepareStatement(_preparedSql);
		for ( String key : _namedParameters.keySet() ) {
			NamedParameter param = _namedParameters.get(key);
			for ( Integer index : param._indices ) {
				switch ( param._type ) {
				case INTEGER:
					stmt.setInt(index, (int)param._value);
					break;
				case LONG:
					stmt.setLong(index, (long)param._value);
					break;
				case STRING:
					stmt.setString(index, (String)param._value);
					break;
				case BOOLEAN:
					stmt.setBoolean(index, (boolean)param._value);
					break;
				case DATE:
					stmt.setString(index, DateTimeFormats.FORMAT_SQLITE_DATE.format((Calendar)param._value));
					break;
				}
			}
		}
		return stmt;
	}
	
	private void parseSQL(String sql) {
		Matcher matcher = _pattern.matcher(sql);
		int iIndex = 0;
		while ( matcher.find() ) {
			iIndex++;//starts at 1
			NamedParameter param = _namedParameters.get(matcher.group(2));
			if ( param == null ) {
				param = new NamedParameter();
				_namedParameters.put(matcher.group(2), param);
			}
			param._indices.add(iIndex);
		}
		_preparedSql = matcher.replaceAll("$1?");
	}
	
	private class NamedParameter {
		public List<Integer> _indices = new ArrayList<Integer>(1); //generally queries will have just one of a parameter
		public Object _value;
		public int _type;
	}
}
