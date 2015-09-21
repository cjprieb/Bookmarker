package com.purplecat.bookmarker.services.websites;

import java.util.List;

public interface IWebsiteList {

	List<IWebsiteParser> getList();

	List<IWebsiteParser> getSortedList();

	int compare(String websiteName, String websiteName1);
}
