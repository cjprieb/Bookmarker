package com.purplecat.bookmarker.services;

import java.util.List;

public interface ISettingsService {
	
	String getSummaryDirectory();
	
	void setSummaryDirectory(String directory);

	List<String> getWebsiteOrder();

	void setWebsiteOrder(List<String> nameOrder);

}
