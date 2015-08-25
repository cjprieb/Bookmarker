package com.purplecat.bookmarker.test;

import java.util.Collections;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.controller.tasks.OnlineUpdateTask;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.test.dummies.DummyThreadObserver;
import com.purplecat.bookmarker.test.dummies.DummyWebsiteList;
import com.purplecat.bookmarker.test.dummies.DummyWebsiteList.DummyWebsiteScraper;
import com.purplecat.bookmarker.test.modules.ThreadTestingModule;


public class OnlineUpdateThreadTests {
	
	@Test
	public void loadWebsitesTests() {
		Injector injector = Guice.createInjector(new ThreadTestingModule());		
		OnlineUpdateTask _service = injector.getInstance(OnlineUpdateTask.class);	
		DummyWebsiteScraper _scraper = injector.getInstance(DummyWebsiteList.class)._scraper;
		DummyThreadObserver _observer = injector.getInstance(DummyThreadObserver.class);
		
		_service.loadOnlineUpdates(24, true, Collections.singleton(_scraper));//hours ago
		
		Assert.assertTrue("scraper not called", _scraper.loadCalled());
		Assert.assertTrue("scraper item load not called", _scraper.itemLoadCalled());

		Assert.assertTrue("load started not called", _observer.loadStartedCalled());
		Assert.assertTrue("site started not called", _observer.siteStartedCalled());
		Assert.assertTrue("site loaded not called", _observer.siteLoadedCalled());
		Assert.assertTrue("item loaded not called", _observer.itemLoadedCalled());
		Assert.assertTrue("load finished not called", _observer.loadFinishedCalled());
		
		List<OnlineMediaItem> list = _observer.getList();
		Assert.assertNotNull("list is null", list);
		Assert.assertTrue("list has no items", list.size() > 0);
	}
}
