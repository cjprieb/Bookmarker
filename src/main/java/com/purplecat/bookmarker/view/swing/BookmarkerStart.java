package com.purplecat.bookmarker.view.swing;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.Preferences;

import javax.swing.ImageIcon;
import javax.swing.JFrame;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.view.swing.panels.GlassTimerPanel;
import com.purplecat.commons.swing.AppIcons;
import com.purplecat.commons.swing.IImageRepository;
import com.purplecat.commons.swing.MyApplication;
import com.purplecat.commons.swing.renderer.ICellRendererFactory;

public class BookmarkerStart extends MyApplication implements IWebsiteLoadObserver {
	
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
	protected int _totalUpdateCount;

	@Override
	protected void setupMainPanel(JFrame frame) {
		_timerGlassPane = new GlassTimerPanel(_imageRepository, frame);
		frame.getContentPane().add(_mainPanel.create(_timerGlassPane));
		frame.setJMenuBar(_mainPanel.initializeMenu());
	}

	@Override
	protected ImageIcon getApplicationIcon() {
		return _imageRepository.getImage(Resources.image.imgBookmarkerId);
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
		this.loadPreferences(Preferences.userNodeForPackage(getClass()), true);
		_controller.observeOnlineThreadLoading(this);
	}

	@Override
	protected void finalInitialization() {
		assert(EventQueue.isDispatchThread());
		_timerGlassPane.startTimer();
		_controller.loadFolders();
		_controller.loadSavedMedia();
	}

	@Override
	protected Runnable getOnVisibleAction() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void applicationQuit() {
		this.savePreferences(Preferences.userNodeForPackage(getClass()), true);
		_mainPanel.savePreferences();
	}

	@Override
	public void notifyLoadStarted() {
		updateIcon(Color.yellow, Color.black, 0);
	}

	@Override
	public void notifySiteStarted(WebsiteInfo site) {}

	@Override
	public void notifySiteParsed(WebsiteInfo site, int itemsFound) {}

	@Override
	public void notifyItemParsed(OnlineMediaItem item, int itemsParsed,	int totalUpdateCount) {
		if ( totalUpdateCount >= 0 && _totalUpdateCount != totalUpdateCount ) {
			updateIcon(Color.yellow, Color.black, totalUpdateCount);
		}
	}
	
	@Override
	public void notifyItemRemoved(OnlineMediaItem newItem, int itemsParsed, int size) {}

	@Override
	public void notifySiteFinished(WebsiteInfo site) {}

	@Override
	public void notifyLoadFinished(List<OnlineMediaItem> list) {
		updateIcon(Color.green, Color.black, _totalUpdateCount);
	}
	
	protected void updateIcon(Color background, Color foreground, int count) {
		_totalUpdateCount = count;

		ArrayList<Image> 	appIcons 		= new ArrayList<Image>();
		
		appIcons.add(_imageRepository.getScaledImage(Resources.image.imgBookmarkerId, AppIcons.SMALL_DOCK_IMAGE_SCALE).getImage());
		//appIcons.add(_imageRepository.getScaledImage(Resources.image.imgBookmarkerId, AppIcons.MEDIUM_DOCK_IMAGE_SCALE).getImage());
		appIcons.add(_imageRepository.getScaledImage(Resources.image.imgBookmarkerId, AppIcons.LARGE_DOCK_IMAGE_SCALE).getImage());
		
		if ( count > 0 ) {
			appIcons.set(1, _imageRepository.getDockImage(Resources.image.imgBookmarkerId, background, String.valueOf(count)).getImage());
		}
		else {
			appIcons.set(1, _imageRepository.getScaledImage(Resources.image.imgBookmarkerId, AppIcons.LARGE_DOCK_IMAGE_SCALE).getImage());
		}
		
		setApplicateImages(appIcons);
	}
}
