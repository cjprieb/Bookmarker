package com.purplecat.commons.logs;

public class LoggingService {
	
	public static boolean ENABLE_EDT_DEBUG = false;
	
	public static ILoggingService create() {
		//TODO: handle default loggers another way? Using ConsoleLog for now
		return new ConsoleLog();
	}

}
