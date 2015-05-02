package com.purplecat.bookmarker.view.swing;

import java.io.File;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.commons.logs.ConsoleLog;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.MyApplication;
import com.purplecat.commons.threads.SwingThreadPool;

public class BookmarkerStart extends MyApplication {
	//TODO: move to seperate class
	public static final String CONNECTION_PREFIX = "jdbc:sqlite:";	
	public static final String DATABASE_PATH = "../BookmarkView/databases/bookmarker.db";

	public static void main(String[] args) {
		new BookmarkerStart(new ConsoleLog()).startApplication();
	}
	
	Controller _controller;
	
	public BookmarkerStart(ILoggingService logging) {
		super(logging);
		
		File dest = new File(DATABASE_PATH);
		_controller = new Controller(new SwingThreadPool(), CONNECTION_PREFIX + dest.getAbsolutePath());
	}

	@Override
	protected void setupMainPanel(JFrame frame) {
		frame.getContentPane().add(MainPanel.create(_controller));
	}

	@Override
	protected ImageIcon getApplicationIcon() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getApplicationName() {
		return "Bookmarker";
	}

	@Override
	protected String getApplicationTitle() {
		return "Bookmarker";
	}

	@Override
	protected void setupActions() {
		this.loadPreferences(Preferences.userNodeForPackage(getClass()), false);
	}

	@Override
	protected void finalInitialization() {
		_controller.loadSavedMedia();
	}

	@Override
	protected Runnable getOnVisibleAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void applicationQuit() {
		// TODO Auto-generated method stub
		
	}
	

}
