package com.purplecat.commons.logs;

public interface ILoggingService {
	public void log(int indent, String tag, String message);
	public void debug(int indent, String tag, String message);
	public void error(String tag, String message);
	public void error(String tag, String message, Exception e);
}
