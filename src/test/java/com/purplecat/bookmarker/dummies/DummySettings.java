package com.purplecat.bookmarker.dummies;

import com.purplecat.bookmarker.services.ISettingsService;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Crystal on 9/10/15.
 */
public class DummySettings implements ISettingsService {
	private HashMap<String, Object> _dictionary = new HashMap<String, Object>();

	@Override
	public String getSummaryDirectory() {
		return (String)_dictionary.get("SummaryDirectory");
	}

	@Override
	public void setSummaryDirectory(String directory) {
		_dictionary.put("SummaryDirectory", directory);
	}

	@Override
	public List<String> getWebsiteOrder() {
		return (List<String>)_dictionary.get("WebsiteOrder");
	}

	@Override
	public void setWebsiteOrder(List<String> nameOrder) {
		_dictionary.put("WebsiteOrder", nameOrder);
	}
}
