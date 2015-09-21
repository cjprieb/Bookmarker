package com.purplecat.bookmarker.services.websites;

import java.util.*;

import com.google.inject.Inject;
import com.purplecat.bookmarker.services.ISettingsService;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.commons.logs.ILoggingService;

public class DefaultWebsiteList implements IWebsiteList {
	
	private List<IWebsiteParser> _websites = new LinkedList<IWebsiteParser>();
	private final ISettingsService _settings;
	
	@Inject
	public DefaultWebsiteList(ILoggingService logging, IGenreRepository genres, ISettingsService settings) {
		_settings = settings;

		//Add alphabetically
		_websites.add(new BakaWebsite(logging, genres));
		_websites.add(new BatotoWebsite(logging, genres));
	}

	@Override
	public List<IWebsiteParser> getList() {
		return _websites;
	}

	@Override
	public List<IWebsiteParser> getSortedList() {
		List<IWebsiteParser> list = new ArrayList<IWebsiteParser>(_websites.size());

		for (String name : _settings.getWebsiteOrder()) {
			Optional<IWebsiteParser> parser = _websites.stream()
					.filter(item -> name.equals(item.getName()))
					.findFirst();
			if (parser.isPresent()) {
				list.add(parser.get());
			}
		}

		return list;
	}

	@Override
	public int compare(String websiteName1, String websiteName2) {
		List<String> websiteOrder = _settings.getWebsiteOrder();
		int i1 = getIndexIgnoreCase(websiteOrder, websiteName1);
		int i2 = getIndexIgnoreCase(websiteOrder, websiteName2);
		return i1 == i2 ? 0 : (i1 > i2 ? 1 : -1);
	}

	public int getIndexIgnoreCase(List<String> list, String value) {
		for (int i = 0; i < list.size(); i++) {
			if ( list.get(i).equalsIgnoreCase(value) ) return i;
		}
		return -1;
	}

}
