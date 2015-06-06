package com.purplecat.bookmarker.services.websites;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.commons.logs.ILoggingService;

public class DefaultWebsiteList implements IWebsiteList {
	
	private List<IWebsiteParser> _websites = new LinkedList<IWebsiteParser>();
	
	@Inject
	public DefaultWebsiteList(ILoggingService logging, IGenreRepository genres) {
		_websites.add(new BatotoWebsite(logging, genres));
	}

	@Override
	public Iterable<IWebsiteParser> getList() {
		return _websites;
	}

}
