package com.purplecat.bookmarker.test.dummies;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteParser;
import com.purplecat.bookmarker.services.websites.SampleBatotoWebsite;
import com.purplecat.commons.logs.ILoggingService;

public class SampleWebsiteList implements IWebsiteList {
	
	private List<IWebsiteParser> _websites = new LinkedList<IWebsiteParser>();
	
	@Inject
	public SampleWebsiteList(ILoggingService logging, IGenreRepository genres) {
		_websites.add(new SampleBatotoWebsite(logging, genres));
	}

	@Override
	public Iterable<IWebsiteParser> getList() {
		return _websites;
	}

}
