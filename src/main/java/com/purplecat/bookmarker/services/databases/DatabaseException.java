package com.purplecat.bookmarker.services.databases;

public class DatabaseException extends Exception {
	String mSqlString;
	Object[] mValues;
	
	public DatabaseException(String desc, String sql, String[] values) {
		super(desc);
		mSqlString = sql;
		mValues = values;
	}
	
	public DatabaseException(String desc, String sql) {
		super(desc);
		mSqlString = sql;
		mValues = null;
	}
	
	public DatabaseException(String desc) {
		super(desc);
		mSqlString = "";
		mValues = null;
	}
	
	public DatabaseException(String desc, Exception e) {
		super(desc + " \n(" + e.getMessage() + ")", e);
		mSqlString = "";
		mValues = null;
	}
	
	public DatabaseException(String desc, String sql, Exception e, Object ... values) {
		super(desc + " \n(" + e.getMessage() + ")", e);
		mSqlString = sql;
		mValues = values;
	}
	
	/**
	 * 
	 * @return raw sql string used by database
	 */
	public String getSqlString() { return(mSqlString); }
	public Object[] getValues() { return(mValues); }

	/**
	 * 
	 * @return sql string with values appended
	 */
	public String getSqlStatement() {
		StringBuilder buf = new StringBuilder();
		buf.append(mSqlString);
		if ( mValues != null ) {
			buf.append("\n\t with values: ");
			int i = 0;
			for( Object value : mValues ) {
				if ( i > 0 ) { buf.append(","); }
				buf.append(value);
				i++;
			}
		}
		
		return(buf.toString());
	}
	
	@Override
	public StackTraceElement[] getStackTrace() {
		if ( getCause() != null ) {
			return(getCause().getStackTrace());
		}
		else {
			return(super.getStackTrace());
		}
	}
}
