package com.purplecat.bookmarker.test.dummies;

import java.util.List;

import com.google.inject.Singleton;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.websites.IThreadObserver;

@Singleton
public class DummyThreadObserver implements IThreadObserver {
	private boolean _loadStarted;
	private boolean _siteStarted;
	private boolean _siteLoaded;
	private boolean _itemLoaded;
	private boolean _siteFinished;
	private boolean _loadFinished;
	private List<OnlineMediaItem> _list;
	
	public boolean loadStartedCalled() { return _loadStarted; }
	public boolean siteStartedCalled() { return _siteStarted; }
	public boolean siteLoadedCalled() { return _siteLoaded; }
	public boolean itemLoadedCalled() { return _itemLoaded; }
	public boolean siteFinishedCalled() { return _siteFinished; }
	public boolean loadFinishedCalled() { return _loadFinished; }
	public List<OnlineMediaItem> getList() { return _list; }
	
	@Override
	public void notifyLoadStarted() {
		_loadStarted = true;
	}
	
	@Override
	public void notifySiteStarted(WebsiteInfo site) {
		_siteStarted = true;
	}
	
	@Override
	public void notifySiteParsed(WebsiteInfo site) {
		_siteLoaded = true;
	}
	
	@Override
	public void notifyItemParsed(OnlineMediaItem item) {
		_itemLoaded = true;
	}
	
	@Override
	public void notifySiteFinished(WebsiteInfo site) {
		_siteFinished = true;
	}
	
	@Override
	public void notifyLoadFinished(List<OnlineMediaItem> list) {
		_loadFinished = true;
		_list = list;
	}
	

}
