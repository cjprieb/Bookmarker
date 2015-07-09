package com.purplecat.bookmarker.test;

import java.util.Collections;
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
import com.purplecat.bookmarker.services.databases.DatabaseException;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.bookmarker.services.databases.IMediaRepository;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.services.databases.IUrlPatternDatabase;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.services.websites.WebsiteThreadObserver;
import com.purplecat.bookmarker.sql.IConnectionManager;
import com.purplecat.bookmarker.test.dummies.DummyOnlineItemRepository;
import com.purplecat.bookmarker.test.dummies.DummyThreadObserver;
import com.purplecat.bookmarker.test.dummies.DummyWebsiteList;
import com.purplecat.bookmarker.test.dummies.DummyWebsiteList.DummyWebsiteScraper;
import com.purplecat.bookmarker.test.dummies.SampleDatabaseService.SampleGenreDatabase;
import com.purplecat.bookmarker.test.dummies.SampleDatabaseService.SamplePatternDatabase;
import com.purplecat.bookmarker.test.dummies.SampleMangaDatabase;
import com.purplecat.commons.logs.ConsoleLog;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.threads.IThreadPool;
import com.purplecat.commons.threads.IThreadTask;


public class OnlineUpdateThreadTests {
	
	public class TestWebsiteScrapingModule extends AbstractModule {

		@Override
		protected void configure() {
			//Utility Items
			bind(ILoggingService.class).to(ConsoleLog.class);
			bind(IThreadPool.class).to(DummyThreadPool.class);
			
			//Website Items
			bind(IWebsiteLoadObserver.class).to(DummyThreadObserver.class);
			bind(OnlineUpdateTask.class);
			
			//Database/Repository items
			bind(IConnectionManager.class).to(DummyConnectionManager.class);
			bind(IUrlPatternDatabase.class).to(SamplePatternDatabase.class);
			bind(IOnlineMediaRepository.class).to(DummyOnlineItemRepository.class);
			bind(IGenreRepository.class).to(SampleGenreDatabase.class);
			bind(UrlPatternService.class);
			bind(WebsiteThreadObserver.class);
			bind(IWebsiteList.class).to(DummyWebsiteList.class);
			bind(IMediaRepository.class).to(SampleMangaDatabase.class);
			bind(String.class).annotatedWith(Names.named("JDBC URL")).toInstance("jdbc:sqlite:" + DatabaseConnectorTestBase.TEST_DATABASE_PATH);	
		}
	}
	
	@Test
	public void loadWebsitesTests() {
		Injector injector = Guice.createInjector(new TestWebsiteScrapingModule());		
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
	
	public static class DummyConnectionManager implements IConnectionManager {

		@Override
		public void open() throws DatabaseException {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void close() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	public static class DummyThreadPool implements IThreadPool {

		@Override
		public void runOnUIThread(IThreadTask task) {
			task.uiTaskCompleted();
		}

		@Override
		public void runOnUIThread(Runnable task) {
			task.run();
		}

		@Override
		public void runOnWorkerThread(IThreadTask task) {
			task.workerTaskStart();
			runOnUIThread(task);
		}

		@Override
		public void runOnWorkerThread(Runnable task) {
			task.run();
		}

		@Override
		public boolean isUIThread() {
			return false;
		}
		
	}
}
