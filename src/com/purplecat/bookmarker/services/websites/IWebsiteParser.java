package com.purplecat.bookmarker.services.websites;

import java.util.List;

import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.ServiceException;

public interface IWebsiteParser {
	public List<OnlineMediaItem> load() throws ServiceException;

	public WebsiteInfo getInfo();

	public OnlineMediaItem loadItem(OnlineMediaItem item) throws ServiceException;
}
