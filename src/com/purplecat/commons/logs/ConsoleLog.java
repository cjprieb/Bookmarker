package com.purplecat.commons.logs;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ConsoleLog implements ILoggingService {
	
	DateFormat _dateFormat;
	
	public ConsoleLog() {
		_dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	}
	
	@Override
	public void debug(int indent, String tag, String message) {
		
	}

	@Override
	public void log(int indent, String tag, String message) {
		System.out.print(String.format("%-40s", tag));	
		System.out.print(_dateFormat.format(Calendar.getInstance().getTime()));
		for (int i = 0; i <= indent; i++) {
			System.out.print("    ");
		}
		System.out.println(message);
	}

	@Override
	public void error(String tag, String message) {
		System.err.println(tag + " - " + message);	
	}

	@Override
	public void error(String tag, String message, Exception e) {	
		System.err.println(tag + " - " + message);	
		e.printStackTrace();
	}
}
