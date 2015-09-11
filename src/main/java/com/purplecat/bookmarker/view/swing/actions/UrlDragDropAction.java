package com.purplecat.bookmarker.view.swing.actions;

import java.awt.Component;
import java.awt.datatransfer.Transferable;
import java.io.File;
import java.io.IOException;
import java.util.List;

import com.apple.eawt.AppEvent.OpenFilesEvent;
import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.commons.io.FileUtils;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.AppUtils.IDragDropAction;
import com.purplecat.commons.utils.StringUtils;

public abstract class UrlDragDropAction implements IDragDropAction {
	private final static String TAG = "UrlDragDropAction";
	
	private final ILoggingService _logger;
	
	@Inject
	public UrlDragDropAction(ILoggingService logger) {
		_logger = logger;
	}
	
	@Override
	public void filesDropped(Component c, File[] files) {
		_logger.debug(0, TAG, "files dropped (url-action)");
		for ( File file : files ) {
			_logger.debug(1, TAG, "file: \"" + file.getAbsolutePath() + "\"");
			if ( file.getName().toLowerCase().endsWith("url") ) {
				try {
					stringDropped(c, FileUtils.parseInternetShortcut(file));
				} catch (IOException e) {
					_logger.error(TAG, "Error extracting URL from file: " + file.getAbsolutePath(), e);
				}
			}
		}
	}

	@Override
	public void objectDropped(Component c, Transferable obj) {
		_logger.debug(0, TAG, "object dropped (url-action): " + obj);
	}

	@Override
	public void openFiles(OpenFilesEvent e) {
		_logger.debug(0, TAG, "open files from files event (no action)");
	}

	@Override
	public void listDropped(Component c, List<?> list) {
		_logger.debug(0, TAG, "list dropped (no action): " + StringUtils.format(list));
	}
	
	public static class AddMangaUrlDropAction extends UrlDragDropAction {
		private final Controller _controller;

		@Inject
		public AddMangaUrlDropAction(ILoggingService logger, Controller controller) {
			super(logger);
			_controller = controller;
		}

		@Override
		public void stringDropped(Component c, String s) {
			_controller.updateMangaFromUrl(s);
		}
		
	}
}