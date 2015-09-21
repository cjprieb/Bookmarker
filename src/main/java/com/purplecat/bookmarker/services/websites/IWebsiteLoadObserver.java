package com.purplecat.bookmarker.services.websites;

import java.util.List;

import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;

public interface IWebsiteLoadObserver {

	void notifyLoadStarted();
	
	void notifySiteStarted(String siteName, String siteUrl);
	
	void notifySiteParsed(String siteName, int itemsFound);
	
	void notifyItemParsed(OnlineMediaItem item, int itemsParsed, int totalUpdateCount);
	
	void notifySiteFinished(String siteName);
	
	void notifyLoadFinished(List<OnlineMediaItem> list);

	void notifyItemRemoved(OnlineMediaItem newItem, int iItemsParsed, int size);

}
