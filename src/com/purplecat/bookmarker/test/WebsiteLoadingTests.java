package com.purplecat.bookmarker.test;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.purplecat.bookmarker.controller.tasks.OnlineUpdateTask;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.UrlPatternService;
import com.purplecat.bookmarker.services.databases.GenreDatabaseRepository;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.bookmarker.services.databases.IMediaRepository;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.services.databases.IUrlPatternDatabase;
import com.purplecat.bookmarker.services.databases.MediaDatabaseRepository;
import com.purplecat.bookmarker.services.databases.OnlineMediaDatabase;
import com.purplecat.bookmarker.services.databases.UrlPatternDatabase;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.sql.ConnectionManager;
import com.purplecat.bookmarker.sql.IConnectionManager;
import com.purplecat.bookmarker.test.dummies.DummyThreadObserver;
import com.purplecat.bookmarker.test.dummies.SampleWebsiteList;
import com.purplecat.commons.logs.ConsoleLog;
import com.purplecat.commons.logs.ILoggingService;

public class WebsiteLoadingTests extends DatabaseConnectorTestBase {
	
	public class DatabaseWebsiteScrapingModule extends AbstractModule {

		@Override
		protected void configure() {
			//Utility Items
			bind(ILoggingService.class).to(ConsoleLog.class);
			
			//Website Items
			bind(IWebsiteList.class).to(SampleWebsiteList.class);
			bind(IWebsiteLoadObserver.class).to(DummyThreadObserver.class);
			bind(OnlineUpdateTask.class);
			
			//Database/Repository items
			bind(IConnectionManager.class).to(ConnectionManager.class);
			bind(IUrlPatternDatabase.class).to(UrlPatternDatabase.class);
			bind(IOnlineMediaRepository.class).to(OnlineMediaDatabase.class);
			bind(IMediaRepository.class).to(MediaDatabaseRepository.class);
			bind(IGenreRepository.class).to(GenreDatabaseRepository.class);
			bind(UrlPatternService.class);
			bind(String.class).annotatedWith(Names.named("JDBC URL")).toInstance("jdbc:sqlite:" + DatabaseConnectorTestBase.TEST_DATABASE_PATH);	
		}
	}
	
	@Test
	public void loadWebsites() {
		Injector injector = Guice.createInjector(new DatabaseWebsiteScrapingModule());		
		OnlineUpdateTask _service = injector.getInstance(OnlineUpdateTask.class);	
		DummyThreadObserver _observer = injector.getInstance(DummyThreadObserver.class);
		
		_service.loadOnlineUpdates();

		Assert.assertTrue("load started not called", _observer.loadStartedCalled());
		Assert.assertTrue("site started not called", _observer.siteStartedCalled());
		Assert.assertTrue("site loaded not called", _observer.siteLoadedCalled());
		//Assert.assertTrue("item loaded not called", _observer.itemLoadedCalled());
		Assert.assertTrue("load finished not called", _observer.loadFinishedCalled());
		
		List<OnlineMediaItem> list = _observer.getList();
		Assert.assertNotNull("list is null", list);
		Assert.assertTrue("list has no items", list.size() > 0);
		Assert.assertTrue(_observer.getItemsFound() > 0);
		Assert.assertEquals(_observer.getItemsParsed(), _observer.getItemsFound());
		
		Assert.assertTrue(_observer.getItemsUpdated() > 0);
		Assert.assertTrue(_observer.getItemsUpdated() < _observer.getItemsFound());
		
		boolean bUpdated = true;
		DateTime lastUpdated = null;
		for ( OnlineMediaItem item : _observer.getList() ) {
			System.out.println("Looking at: " + item);
			if ( !item.isUpdated() ) {
				bUpdated = false;
				lastUpdated = item._updatedDate;
			}
			else if ( !bUpdated && item.isUpdated() ) {
				Assert.fail("Updated item in wrong order");
			}
			else if ( lastUpdated == null ) {
				lastUpdated = item._updatedDate;
			}
			else {
				Assert.assertTrue("time wrong", lastUpdated.compareTo(item._updatedDate) >= 0);
			}
		}
	}

}
