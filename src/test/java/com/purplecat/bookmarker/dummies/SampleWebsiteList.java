package com.purplecat.bookmarker.dummies;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteParser;
import com.purplecat.bookmarker.services.websites.SampleBatotoWebsite;
import com.purplecat.commons.logs.ILoggingService;

@Singleton
public class SampleWebsiteList implements IWebsiteList {
	
	private List<IWebsiteParser> _websites = new LinkedList<IWebsiteParser>();
	private SampleBatotoWebsite _sampleSite;
	
	@Inject
	public SampleWebsiteList(ILoggingService logging, IGenreRepository genres) {
		_sampleSite = new SampleBatotoWebsite(logging, genres);
		_websites.add(_sampleSite);
	}

	@Override
	public List<IWebsiteParser> getList() {
		return _websites;
	}

	@Override
	public List<IWebsiteParser> getSortedList() {
		return _websites;
	}

	@Override
	public int compare(String websiteName, String websiteName1) {
		return 0;
	}

	public int getSampleHoursAgo() {
		return 8;//this will work for batoto
	}

	public int getLoadItemCount() {
		return _sampleSite.getLoadItemCount();
	}

}
