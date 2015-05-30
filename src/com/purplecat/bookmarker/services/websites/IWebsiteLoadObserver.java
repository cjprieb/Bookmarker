package com.purplecat.bookmarker.services.websites;

import java.util.List;

import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;

public interface IWebsiteLoadObserver {

	public void notifyLoadStarted();
	
	public void notifySiteStarted(WebsiteInfo site);
	
	public void notifySiteParsed(WebsiteInfo site, int itemsFound);
	
	public void notifyItemParsed(OnlineMediaItem item, int itemsParsed, int totalUpdateCount);
	
	public void notifySiteFinished(WebsiteInfo site);
	
	public void notifyLoadFinished(List<OnlineMediaItem> list);	

}
