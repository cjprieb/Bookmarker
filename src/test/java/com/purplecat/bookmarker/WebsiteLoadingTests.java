package com.purplecat.bookmarker;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.name.Names;
import com.purplecat.bookmarker.controller.Controller;
import com.purplecat.bookmarker.controller.tasks.OnlineUpdateTask;
import com.purplecat.bookmarker.models.Genre;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.IFolderRepository;
import com.purplecat.bookmarker.services.ISummaryRepository;
import com.purplecat.bookmarker.services.UrlPatternService;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.bookmarker.services.databases.IMediaRepository;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.services.databases.IUrlPatternDatabase;
import com.purplecat.bookmarker.services.websites.IWebsiteList;
import com.purplecat.bookmarker.services.websites.IWebsiteLoadObserver;
import com.purplecat.bookmarker.services.websites.WebsiteThreadObserver;
import com.purplecat.bookmarker.sql.IConnectionManager;
import com.purplecat.bookmarker.dummies.DummyConnectionManager;
import com.purplecat.bookmarker.dummies.DummySummaryRepository;
import com.purplecat.bookmarker.dummies.DummyThreadObserver;
import com.purplecat.bookmarker.dummies.DummyThreadPool;
import com.purplecat.bookmarker.dummies.SampleDatabaseService.SampleFolderDatabase;
import com.purplecat.bookmarker.dummies.SampleDatabaseService.SampleGenreDatabase;
import com.purplecat.bookmarker.dummies.SampleDatabaseService.SamplePatternDatabase;
import com.purplecat.bookmarker.dummies.SampleMangaDatabase;
import com.purplecat.bookmarker.dummies.SampleOnlineMangaDatabase;
import com.purplecat.bookmarker.dummies.SampleWebsiteList;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.logs.ConsoleLog;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.swing.IImageRepository;
import com.purplecat.commons.swing.SwingImageRepository;
import com.purplecat.commons.swing.SwingResourceService;
import com.purplecat.commons.swing.Toolbox;
import com.purplecat.commons.tests.GetRandom;
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
			bind(ISummaryRepository.class).to(DummySummaryRepository.class);
			bind(IFolderRepository.class).to(SampleFolderDatabase.class);
			bind(Controller.class);
			bind(WebsiteThreadObserver.class);
			bind(IWebsiteList.class).to(SampleWebsiteList.class);
			bind(String.class).annotatedWith(Names.named("JDBC URL")).toInstance("jdbc:sqlite:" + TEST_DATABASE_PATH);
			
			//Swing Items
			bind(Toolbox.class);
			bind(IThreadPool.class).to(DummyThreadPool.class);
			bind(IResourceService.class).to(SwingResourceService.class);
			bind(IImageRepository.class).to(SwingImageRepository.class);
		}
	}
	
	@Test
	public void removeInvalidGenres() {
		Injector injector = Guice.createInjector(new DatabaseWebsiteScrapingModule());		
		OnlineUpdateTask _service = injector.getInstance(OnlineUpdateTask.class);	
		
		OnlineMediaItem item = new OnlineMediaItem();
		item._displayTitle = GetRandom.getString(10);
		
		Genre genre = new Genre();
		genre._name = GetRandom.getString(10);
		genre._id = GetRandom.getInteger();
		genre._include = true;
		item._genres.add(genre);
		
		genre = new Genre();
		genre._name = GetRandom.getString(10);
		genre._id = GetRandom.getInteger();
		genre._include = true;
		item._genres.add(genre);
		
		assertTrue(_service.IncludeOnlineUpdateItem(item));
		
		genre = new Genre();
		genre._name = GetRandom.getString(10);
		genre._id = GetRandom.getInteger();
		genre._include = false;
		item._genres.add(genre);
		
		assertFalse(_service.IncludeOnlineUpdateItem(item));
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
	
	@Test
	public void loadWebsitesTwice() {
		Injector injector = Guice.createInjector(new DatabaseWebsiteScrapingModule());		
		OnlineUpdateTask _service = injector.getInstance(OnlineUpdateTask.class);	
		DummyThreadObserver _observer = injector.getInstance(DummyThreadObserver.class);
		SampleWebsiteList _websites = injector.getInstance(SampleWebsiteList.class);
		
		int hoursAgo = _websites.getSampleHoursAgo();
		_service.loadOnlineUpdates(hoursAgo, false, _websites.getList());
		
		Map<Long, DateTime> times = _observer.getList().stream().collect(Collectors.toMap(m -> m._id, m -> m._updatedDate));
		
		for ( OnlineMediaItem item : _observer.getList() ) {
			System.out.println("ITEM " + item._displayTitle + " (" + item._id + "): " + item._updatedDate);
			Assert.assertTrue("Item isn't new: " + item._displayTitle, item._newlyAdded);
		}
		
		List<OnlineMediaItem> origList = new LinkedList<OnlineMediaItem>();
		origList.addAll(_observer.getList());
		
		for ( Long key : times.keySet() ) {
			System.out.println("ITEM (" + key + "): " + times.get(key));
		}
		
		try {
			synchronized(this) {
				this.wait(1000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		_service.loadOnlineUpdates(hoursAgo, false, _websites.getList());
		
		List<OnlineMediaItem> list = _observer.getList();
		Assert.assertNotNull("list is null", list);
		Assert.assertTrue("list has no items", list.size() > 0);
		Assert.assertTrue(_observer.getItemsFound() > 0);
		
		for ( OnlineMediaItem item : list ) {
			System.out.println("ITEM " + item._displayTitle + " (" + item._id + "): " + item._updatedDate);
			DateTime earlierDate = times.get(item._id);
			if ( earlierDate != null ) {
				Assert.assertEquals("dates not equal for " + item._id, earlierDate, item._updatedDate);
			}
			if ( origList.stream().anyMatch(orig -> orig._id == item._id) ) {
				Assert.assertFalse("Item is still new: " + item._displayTitle, item._newlyAdded);
			}
			else {
				Assert.assertTrue("Item isn't new: " + item._displayTitle, item._newlyAdded);
			}
		}
		
	}

}
