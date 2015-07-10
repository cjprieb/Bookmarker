package com.purplecat.bookmarker.test.dummies;

import java.util.LinkedList;
import java.util.List;

import com.google.inject.Singleton;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;

@Singleton
public class DummyThreadObserver implements IWebsiteLoadObserver {
	private boolean _loadStarted;
	private boolean _siteStarted;
	private boolean _siteLoaded;
	private boolean _itemLoaded;
	private boolean _siteFinished;
	private boolean _loadFinished;
	private List<OnlineMediaItem> _list;
	private int _itemsFound;
	private int _itemsParsed;
	private int _itemsUpdated;
	
	public boolean loadStartedCalled() { return _loadStarted; }
	public boolean siteStartedCalled() { return _siteStarted; }
	public boolean siteLoadedCalled() { return _siteLoaded; }
	public boolean itemLoadedCalled() { return _itemLoaded; }
	public boolean siteFinishedCalled() { return _siteFinished; }
	public boolean loadFinishedCalled() { return _loadFinished; }
	public List<OnlineMediaItem> getList() { return _list; }
	public int getItemsFound() { return _itemsFound; }
	public int getItemsParsed() { return _itemsParsed; }
	public int getItemsUpdated() { return _itemsUpdated; }
	
	@Override
	public void notifyLoadStarted() {
		_loadStarted = true;
	}
	
	@Override
	public void notifySiteStarted(WebsiteInfo site) {
		_siteStarted = true;
	}
	
	@Override
	public void notifySiteParsed(WebsiteInfo site, int itemsFound) {
		_siteLoaded = true;
		_itemsFound = itemsFound;
		System.out.println("site parsed: " + itemsFound);
	}
	
	@Override
	public void notifyItemParsed(OnlineMediaItem item, int itemsParsed, int updateCount) {
		System.out.println("item parsed: " + updateCount);
		_itemLoaded = true;
		_itemsParsed = itemsParsed;
		_itemsUpdated = updateCount;
	}
	
	@Override
	public void notifySiteFinished(WebsiteInfo site) {
		_siteFinished = true;
	}
	
	@Override
	public void notifyLoadFinished(List<OnlineMediaItem> list) {
		if ( _list == null ) {
			_list = new LinkedList<OnlineMediaItem>();
		}
		_loadFinished = true;
		for ( OnlineMediaItem item : list ) {
			if ( !_list.stream().anyMatch(a -> a._id == item._id) ) {
				_list.add(item);
			}
		}
	}
	

}
