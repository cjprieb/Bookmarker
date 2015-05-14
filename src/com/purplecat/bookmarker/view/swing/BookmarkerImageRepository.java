package com.purplecat.bookmarker.view.swing;

import javax.swing.ImageIcon;

import com.google.inject.Inject;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.AbstractImageRepository;

public class BookmarkerImageRepository extends AbstractImageRepository {
	
	@Inject
	public BookmarkerImageRepository(ILoggingService logger) {
		super(logger);
	}

	@Override
	public ImageIcon getImage(String name) {
		return this.getImageResource("bookmarker", name, "png");
	}

}
