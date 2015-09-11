package com.purplecat.bookmarker.services;

public class DefaultSettingsService implements ISettingsService {
	@Override
	public String getSummaryDirectory() {
		return "data/summaries/";
	}

	@Override
	public void setSummaryDirectory(String directory) {}

}
