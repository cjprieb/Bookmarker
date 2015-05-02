package com.purplecat.bookmarker.services;

public class ServiceException extends Exception {
	public final static int INVALID_ID = 100;
	
	private int _errorCode;
	
	public ServiceException(int errorCode) {
		_errorCode = errorCode;
	}
	
	public int getErrorCode() {
		return _errorCode;
	}
	
	public String getMessage() {
		String msg = "Unknown";
		switch (_errorCode) {
		case INVALID_ID:
			msg = "Id is invalid";
			break;
		default:
			break;
		}
		return msg;
	}

}
