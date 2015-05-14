package com.purplecat.bookmarker.services;

public class ServiceException extends Exception {
	public final static int INVALID_ID = 100;
	public final static int INVALID_DATA = 150;
	public final static int SQL_ERROR = 200;
	public static final int WEBSITE_ERROR = 300;
	
	private int _errorCode;
	private String _internalMessage;
	
	public ServiceException(int errorCode) {
		_errorCode = errorCode;
	}
	
	public ServiceException(String msg, int errorCode) {
		_internalMessage = msg;
		_errorCode = errorCode;
	}
	
	public int getErrorCode() {
		return _errorCode;
	}
	
	@Override
	public String getMessage() {
		String msg = "Unknown";
		switch (_errorCode) {
		case INVALID_ID:
			msg = "The id is invalid";
			break;
		case INVALID_DATA:
			msg = "The data is invalid";
			break;
		case SQL_ERROR:
			msg = "An error occurred with the database.";
			break;
		default:
			break;
		}
		if ( _internalMessage != null && _internalMessage.length() > 0 ) {
			return String.format("%s: %s", msg, _internalMessage);
		}
		return msg;
	}

}
