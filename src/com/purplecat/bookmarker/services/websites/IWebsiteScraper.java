package com.purplecat.bookmarker.services.websites;

import java.util.List;

import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;

public interface IWebsiteScraper {
	public List<OnlineMediaItem> load();

	public WebsiteInfo getInfo();

	public OnlineMediaItem loadItem(OnlineMediaItem item);
}
