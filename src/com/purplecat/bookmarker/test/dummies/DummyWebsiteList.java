package com.purplecat.bookmarker.test.dummies;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

import com.google.inject.Singleton;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteParser;

@Singleton
public class DummyWebsiteList implements IWebsiteList {
	
	public class DummyWebsiteScraper implements IWebsiteParser {
		
		private boolean _loadCalled;
		private boolean _itemLoadCalled;

		public boolean loadCalled() {
			return _loadCalled;
		}

		public boolean itemLoadCalled() {
			return _itemLoadCalled;
		}
		
		@Override
		public List<OnlineMediaItem> load() {
			_loadCalled = true;			
			List<OnlineMediaItem> list = new LinkedList<OnlineMediaItem>();
			
			OnlineMediaItem item = new OnlineMediaItem();
			item._id = 0;
			item._displayTitle = "Shana oh Yoshitsune";
			item._updatedPlace._volume = 10;
			item._updatedPlace._chapter = 38;
			item._chapterUrl = "http://bato.to/read/_/319045/shana-oh-yoshitsune_v10_ch38_by_easy-going-scans";
			item._titleUrl = "http://bato.to/comic/_/comics/shana-oh-yoshitsune-r5256";
			item._updatedDate = new DateTime().minusMinutes(15);
			list.add(item);
			
			item = new OnlineMediaItem();
			item._id = 0;
			item._displayTitle = "Haikyuu";
			item._updatedPlace._chapter = 155;
			item._chapterUrl = "http://bato.to/read/_/318565/haikyuu_ch155_by_casanova";
			item._titleUrl = "http://bato.to/comic/_/comics/haikyuu-r1873";
			item._updatedDate = new DateTime().minusMinutes(10);
			list.add(item);
			
			item = new OnlineMediaItem();
			item._id = 0;
			item._displayTitle = "Haru Niwa";
			item._chapterUrl = "http://bato.to/read/_/319048/haru-niwa_by_paperdolls-project";
			item._titleUrl = "http://bato.to/comic/_/comics/haru-niwa-r15259";
			item._updatedDate = new DateTime().minusMinutes(5);
			list.add(item);
			
			item = new OnlineMediaItem();
			item._id = 0;
			item._displayTitle = "Ruin Explorer Fam & Ihrlie ";
			item._updatedPlace._volume = 1;
			item._updatedPlace._chapter = 1;
			item._chapterUrl = "http://bato.to/read/_/319039/ruin-explorer-fam-ihrlie_v1_ch1_by_bardass-scanlations";
			item._titleUrl = "http://bato.to/comic/_/comics/ruin-explorer-fam-ihrlie-r15253";
			item._updatedDate = new DateTime();
			list.add(item);
			
			return list;
		}

		@Override
		public WebsiteInfo getInfo() {
			return new WebsiteInfo("name", "url");
		}

		@Override
		public OnlineMediaItem loadItem(OnlineMediaItem item) {
			_itemLoadCalled = true;
			return item;
		}

	}
	
	public List<IWebsiteParser> _list = new LinkedList<IWebsiteParser>();
	public DummyWebsiteScraper _scraper;
	
	public DummyWebsiteList() {
		_scraper = new DummyWebsiteScraper(); 
		_list.add(_scraper);
	}

	@Override
	public Iterable<IWebsiteParser> getList() {
		return _list;
	}
}
