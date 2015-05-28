package com.purplecat.bookmarker.view.swing;

import java.awt.EventQueue;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.IImageRepository;
import com.purplecat.commons.swing.MyApplication;
import com.purplecat.commons.swing.Toolbox;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;

public class BookmarkerStart extends MyApplication {
	//TODO: move to seperate class
	public static final String CONNECTION_PREFIX = "jdbc:sqlite:";	
	public static final String DATABASE_PATH = "../BookmarkView/databases/bookmarker.db";

	public static void main(String[] args) {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			//if it gets here, there's a problem.
			throw new NullPointerException("No sqlite JDBC connector found! Aborting");
		}
		
		Injector injector = Guice.createInjector(new SwingBookmarkerModule());		
		injector.getInstance(MyApplication.class).startApplication();
	}
	
	@Inject protected Controller _controller;
	@Inject protected ICellRendererFactory _renderer;	
	@Inject protected IImageRepository _imageRepository;	

	@Inject protected MainPanel _mainPanel;
	
	protected GlassTimerPanel _timerGlassPane;

	@Override
	protected void setupMainPanel(JFrame frame) {
		_timerGlassPane = new GlassTimerPanel(_imageRepository, frame);
		frame.getContentPane().add(_mainPanel.create(_timerGlassPane));
	}

	@Override
	protected ImageIcon getApplicationIcon() {
		return _imageRepository.getImage(BookmarkerImages.imgBookmarkerId);
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
		assert(EventQueue.isDispatchThread());
		_timerGlassPane.startTimer();
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
