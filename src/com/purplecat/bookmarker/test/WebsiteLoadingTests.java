package com.purplecat.bookmarker.test;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.tasks.OnlineUpdateTask;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.UrlPatternService;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.bookmarker.services.databases.IMediaRepository;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.services.databases.IUrlPatternDatabase;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.services.websites.WebsiteThreadObserver;
import com.purplecat.bookmarker.sql.IConnectionManager;
import com.purplecat.bookmarker.test.OnlineUpdateThreadTests.DummyThreadPool;
import com.purplecat.bookmarker.test.dummies.DummyConnectionManager;
import com.purplecat.bookmarker.test.dummies.DummyThreadObserver;
import com.purplecat.bookmarker.test.dummies.SampleDatabaseService.SampleGenreDatabase;
import com.purplecat.bookmarker.test.dummies.SampleDatabaseService.SamplePatternDatabase;
import com.purplecat.bookmarker.test.dummies.SampleMangaDatabase;
import com.purplecat.bookmarker.test.dummies.SampleOnlineMangaDatabase;
import com.purplecat.bookmarker.test.dummies.SampleWebsiteList;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.logs.ConsoleLog;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.IImageRepository;
import com.purplecat.commons.swing.SwingImageRepository;
import com.purplecat.commons.swing.SwingResourceService;
import com.purplecat.commons.swing.Toolbox;
import com.purplecat.commons.threads.IThreadPool;

public class WebsiteLoadingTests extends DatabaseConnectorTestBase {
	
	public class DatabaseWebsiteScrapingModule extends AbstractModule {

		@Override
		protected void configure() {
			//Utility Items
			bind(ILoggingService.class).to(ConsoleLog.class);
			bind(String.class).annotatedWith(Names.named("Resource File")).toInstance("com.purplecat.bookmarker.Resources");
			bind(String.class).annotatedWith(Names.named("Project Path")).toInstance("/com/purplecat/bookmarker/");

			//Controller items
			bind(IWebsiteLoadObserver.class).to(DummyThreadObserver.class);
			
			//Database/Repository items
			bind(IConnectionManager.class).to(DummyConnectionManager.class);
			bind(IUrlPatternDatabase.class).to(SamplePatternDatabase.class);
			bind(UrlPatternService.class);
			bind(IMediaRepository.class).to(SampleMangaDatabase.class);
			bind(IGenreRepository.class).to(SampleGenreDatabase.class);
			bind(IOnlineMediaRepository.class).to(SampleOnlineMangaDatabase.class);
			bind(Controller.class);
			bind(WebsiteThreadObserver.class);
			bind(IWebsiteList.class).to(SampleWebsiteList.class);
			bind(String.class).annotatedWith(Names.named("JDBC URL")).toInstance("jdbc:sqlite:" + DatabaseConnectorTestBase.TEST_DATABASE_PATH);
			
			//Swing Items
			bind(Toolbox.class);
			bind(IThreadPool.class).to(DummyThreadPool.class);
			bind(IResourceService.class).to(SwingResourceService.class);
			bind(IImageRepository.class).to(SwingImageRepository.class);
		}
	}
	
	@Test
	public void loadWebsitesGenre() {
		Injector injector = Guice.createInjector(new DatabaseWebsiteScrapingModule());		
		OnlineUpdateTask _service = injector.getInstance(OnlineUpdateTask.class);	
		DummyThreadObserver _observer = injector.getInstance(DummyThreadObserver.class);
		SampleWebsiteList _websites = injector.getInstance(SampleWebsiteList.class);
		
		_service.loadOnlineUpdates(_websites.getSampleHoursAgo(), true, _websites.getList());

		Assert.assertTrue("load started not called", _observer.loadStartedCalled());
		Assert.assertTrue("site started not called", _observer.siteStartedCalled());
		Assert.assertTrue("site loaded not called", _observer.siteLoadedCalled());
		Assert.assertTrue("item loaded not called", _observer.itemLoadedCalled());
		Assert.assertTrue("load finished not called", _observer.loadFinishedCalled());
		
		List<OnlineMediaItem> list = _observer.getList();
		Assert.assertNotNull("list is null", list);
		Assert.assertTrue("list has no items", list.size() > 0);
		Assert.assertTrue("no items were found", _observer.getItemsFound() > 0);
		Assert.assertEquals("items parsed does not match items found", _observer.getItemsParsed(), _observer.getItemsFound());
		Assert.assertTrue("items not loaded", _websites.getLoadItemCount() > 0);
	}
	
	@Test
	public void loadWebsitesOrder() {
		Injector injector = Guice.createInjector(new DatabaseWebsiteScrapingModule());		
		OnlineUpdateTask _service = injector.getInstance(OnlineUpdateTask.class);	
		DummyThreadObserver _observer = injector.getInstance(DummyThreadObserver.class);
		SampleWebsiteList _websites = injector.getInstance(SampleWebsiteList.class);
		
		_service.loadOnlineUpdates(_websites.getSampleHoursAgo(), false, _websites.getList());

		Assert.assertTrue("load started not called", _observer.loadStartedCalled());
		Assert.assertTrue("site started not called", _observer.siteStartedCalled());
		Assert.assertTrue("site loaded not called", _observer.siteLoadedCalled());
		Assert.assertTrue("item loaded not called", _observer.itemLoadedCalled());
		Assert.assertTrue("load finished not called", _observer.loadFinishedCalled());
		Assert.assertEquals("items were loaded", 0, _websites.getLoadItemCount());
		
		List<OnlineMediaItem> list = _observer.getList();
		Assert.assertNotNull("list is null", list);
		Assert.assertTrue("list has no items", list.size() > 0);
		Assert.assertTrue("no items were found", _observer.getItemsFound() > 0);
		Assert.assertEquals("items parsed does not match items found", _observer.getItemsParsed(), _observer.getItemsFound());
		
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
	
	@Test
	public void loadWebsitesTime() {
		Injector injector = Guice.createInjector(new DatabaseWebsiteScrapingModule());		
		OnlineUpdateTask _service = injector.getInstance(OnlineUpdateTask.class);	
		DummyThreadObserver _observer = injector.getInstance(DummyThreadObserver.class);
		SampleWebsiteList _websites = injector.getInstance(SampleWebsiteList.class);
		
		int hoursAgo = _websites.getSampleHoursAgo();
		DateTime minUpdateDate = DateTime.now().minusHours(hoursAgo);
		_service.loadOnlineUpdates(hoursAgo, false, _websites.getList());
		
		List<OnlineMediaItem> list = _observer.getList();
		Assert.assertNotNull("list is null", list);
		Assert.assertTrue("list has no items", list.size() > 0);
		Assert.assertTrue(_observer.getItemsFound() > 0);
		Assert.assertEquals(_observer.getItemsParsed(), _observer.getItemsFound());
		
		for ( OnlineMediaItem item : _observer.getList() ) {
			System.out.println("Looking at: " + item);
			if ( item._updatedDate.compareTo(minUpdateDate) < 0 ) {
				Assert.fail("Loaded too old of item");
			}
		}
	}

}
