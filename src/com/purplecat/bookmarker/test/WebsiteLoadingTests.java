package com.purplecat.bookmarker.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.purplecat.bookmarker.controller.tasks.OnlineUpdateTask;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.UrlPatternService;
import com.purplecat.bookmarker.services.databases.IMediaRepository;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.services.databases.IUrlPatternDatabase;
import com.purplecat.bookmarker.services.databases.MediaDatabaseRepository;
import com.purplecat.bookmarker.services.databases.OnlineMediaDatabase;
import com.purplecat.bookmarker.services.databases.UrlPatternDatabase;
import com.purplecat.bookmarker.services.websites.DefaultWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.test.dummies.DummyThreadObserver;
import com.purplecat.commons.logs.ConsoleLog;
import com.purplecat.commons.logs.ILoggingService;

public class WebsiteLoadingTests extends DatabaseConnectorTestBase {
	
	public class DatabaseWebsiteScrapingModule extends AbstractModule {

		@Override
		protected void configure() {
			//Utility Items
			bind(ILoggingService.class).to(ConsoleLog.class);
			
			//Website Items
			bind(IWebsiteList.class).to(DefaultWebsiteList.class);
			bind(IWebsiteLoadObserver.class).to(DummyThreadObserver.class);
			bind(OnlineUpdateTask.class);
			
			//Database/Repository items
			bind(IUrlPatternDatabase.class).to(UrlPatternDatabase.class);
			bind(IOnlineMediaRepository.class).to(OnlineMediaDatabase.class);
			bind(IMediaRepository.class).to(MediaDatabaseRepository.class);
			bind(UrlPatternService.class);
			bind(String.class).annotatedWith(Names.named("JDBC URL")).toInstance("jdbc:sqlite:" + DatabaseConnectorTestBase.TEST_DATABASE_PATH);	
		}
	}
	
	@Test
	public void loadWebsites() {
		Injector injector = Guice.createInjector(new DatabaseWebsiteScrapingModule());		
		OnlineUpdateTask _service = injector.getInstance(OnlineUpdateTask.class);
		
		//It's setup to be this way
		DummyThreadObserver _observer = (DummyThreadObserver)_service._observer;
		_service.loadOnlineUpdates();

		Assert.assertTrue("load started not called", _observer.loadStartedCalled());
		Assert.assertTrue("site started not called", _observer.siteStartedCalled());
		Assert.assertTrue("site loaded not called", _observer.siteLoadedCalled());
		//Assert.assertTrue("item loaded not called", _observer.itemLoadedCalled());
		Assert.assertTrue("load finished not called", _observer.loadFinishedCalled());
		
		List<OnlineMediaItem> list = _observer.getList();
		Assert.assertNotNull("list is null", list);
		Assert.assertTrue("list has no items", list.size() > 0);
	}

}
