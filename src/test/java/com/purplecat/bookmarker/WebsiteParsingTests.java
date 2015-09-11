package com.purplecat.bookmarker;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.databases.IGenreRepository;
import com.purplecat.bookmarker.services.websites.BakaWebsite;
import com.purplecat.bookmarker.services.websites.BatotoWebsite;
import com.purplecat.bookmarker.services.websites.IWebsiteParser;
import com.purplecat.bookmarker.modules.TestBookmarkerModule;
import com.purplecat.commons.logs.ILoggingService;

public class WebsiteParsingTests {
	
	@Inject
	public ILoggingService _logging;
	
	@Inject 
	public IGenreRepository _genreDatabase;
	
	@Before
	public void beforeTest() {
		Injector injector = Guice.createInjector(new TestBookmarkerModule());
		_logging = injector.getInstance(ILoggingService.class);
		_genreDatabase = injector.getInstance(IGenreRepository.class);
	}

	@Test
	public void batotoParseTest() {
		System.out.println("BATOTO");
		IWebsiteParser site = new BatotoWebsite(_logging, _genreDatabase);		
		try {
			DateTime minUpdateDate = DateTime.now().minusHours(24); //hours ago
			List<OnlineMediaItem> items = site.load(minUpdateDate);
			assertNotNull(items);
			assertTrue(items.size() > 0);
			int iCount = 0;
			for ( OnlineMediaItem item : items ) {
				iCount++;
				checkItem(item);
//
				if (iCount < 0 ) { 
					site.loadItem(item);
					checkFullItemLoaded(item);					
				}
				if ( item._updatedDate.compareTo(minUpdateDate) < 0 ) {
					Assert.fail("Item was updated too long ago");
				}
			}
			Assert.assertTrue("no items from yesterday", items.stream().anyMatch(item -> item._updatedDate.getDayOfMonth() == minUpdateDate.getDayOfMonth()));
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void bakaParseTest() {
		System.out.println("BAKA UPDATES");
		IWebsiteParser site = new BakaWebsite(_logging, _genreDatabase);		
		try {
			DateTime minUpdateDate = DateTime.now().minusHours(8); //hours ago
			List<OnlineMediaItem> items = site.load(minUpdateDate);
			assertNotNull(items);
			assertTrue(items.size() > 0);
			int iCount = 0;
			for ( OnlineMediaItem item : items ) {
				iCount++;
				checkItem(item);

				if (iCount < 15 ) { 
					site.loadItem(item);
					checkFullItemLoaded(item);					
				}
				if ( item._updatedDate.compareTo(minUpdateDate) < 0 ) {
					Assert.fail("Item was updated too long ago");
				}
			}
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void checkItem(OnlineMediaItem item) {
		DateTime date = DateTime.now().minusDays(10);
		System.out.println("checking " + item);
		System.out.println("    date: " + item._updatedDate);
		
		Assert.assertTrue("no title", item._displayTitle != null && item._displayTitle.length() > 0);
		Assert.assertTrue("no title url", item._titleUrl != null && item._titleUrl.length() > 0);
		Assert.assertTrue("no website name", item._websiteName != null && item._websiteName.length() > 0);
		Assert.assertNotNull("date is null", item._updatedDate);
		Assert.assertTrue("invalid date", item._updatedDate.isAfter(date));
		
		if ( item._chapterUrl != null && item._chapterUrl.length() > 0 && ( item._chapterName == null || !item._chapterName.contains("Oneshot") )  ) {
//			Assert.assertTrue("invalid place", item._updatedPlace.compareTo(new Place()) > 0);
			if ( item._updatedPlace.compareTo(new Place()) == 0 ) {
				System.err.println("No place found for " + item._displayTitle + " (using " + item._chapterUrl + ")");
			}
		}
	}
	
	protected void checkFullItemLoaded(OnlineMediaItem item) {
		System.out.println("checking " + item);
		
		Assert.assertNotNull("no genres", item._genres);
		if ( item._genres.size() == 0) {
			System.err.println("No genres found for " + item._displayTitle);
		}
		else if ( item._genres.stream().anyMatch(m -> m._name.startsWith("Search for")) ){
			Assert.fail("invalid genre in list");
		}
		Assert.assertNotNull("no summary", item._summary);
		if ( item._summary.length() == 0) {
			System.err.println("No summary found for " + item._displayTitle);
		}
		Assert.assertTrue("invalid rating", item._rating < 11);
		if ( item._rating == 0) {
			System.err.println("No rating found for " + item._displayTitle);
		}
	}

}
