package com.purplecat.commons.logs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class FileLog implements ILoggingService {
	
	private boolean _logToConsole = false;
	private File _file;
	DateFormat _dateFormat;
	
	public FileLog() {
		this(true);
	}
	
	public FileLog(boolean logToConsole) {
		_file = new File("logs.txt");
		_dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		
		initLog();
	}

	@Override
	public void debug(int indent, String tag, String message) {
//		if ( _logToConsole ) {
//			System.out.print(_dateFormat.format(Calendar.getInstance().getTime()));
//			System.out.println(" - " + message);
//		}
//		writeLine(indent, tag, message);
	}

	@Override
	public void log(int indent, String tag, String message) {
		if ( _logToConsole ) {
			System.out.print(_dateFormat.format(Calendar.getInstance().getTime()));
			System.out.println(" - " + message);
		}
		writeLine(indent, tag, message);
	}

	@Override
	public void error(String tag, String message) {
		if ( _logToConsole ) {
			System.err.println(message);
		}
		writeLine(0, tag, "ERROR-" + message);		
	}

	@Override
	public void error(String tag, String message, Exception e) {		
		if ( _logToConsole ) {
			System.err.println(message);
			e.printStackTrace();
		}
		writeLine(0, tag, "ERROR-" + message + "\n\t" + e.getMessage());
	}
	
	private void initLog() {
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(_file, true), "UTF8");
			out.write("----------------------------------------------------\n");
			out.write(_dateFormat.format(Calendar.getInstance().getTime()));
			out.write("\tStarting Application\n");
			out.write("----------------------------------------------------\n");
			out.close();
		}
		catch (IOException e) {
			System.err.println("IOException: Could not write to log file at \"" + _file.getPath() + "\". " + e.getMessage());
		}
	}
	
	private void writeLine(int indent, String tag, String message) {
		StringBuilder builder = new StringBuilder();
		builder.append(String.format("%-16s", getThreadName()));
		builder.append(String.format("%-24s", tag));		
		builder.append(_dateFormat.format(Calendar.getInstance().getTime()));
		for (int i = 0; i < indent; i++) {
			builder.append('\t');
		}
		builder.append(message);
		
		try {
			Writer out = new OutputStreamWriter(new FileOutputStream(_file, true), "UTF8");
			out.write(builder.toString());
			out.write("\n");
			out.close();
		}
		catch (IOException e) {
			System.err.println("IOException: Could not write to log file at \"" + _file.getPath() + "\". " + e.getMessage());
		}
	}
	
	protected String getThreadName() {
		String threadName = Thread.currentThread().getName();
		if ( threadName.contains("AWT") ) {
			threadName = "EventQueue-" + Thread.currentThread().getId();					 
		}
		else if ( threadName.contains("SwingWorker") ) {
			threadName = "SwingWorker-" + Thread.currentThread().getId();				
		}
		return(threadName);
	}

}
