package com.purplecat.bookmarker.test;

import static org.junit.Assert.assertEquals;
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
import com.purplecat.bookmarker.models.WebsiteInfo;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.websites.BatotoWebsite;
import com.purplecat.bookmarker.services.websites.IWebsiteParser;
import com.purplecat.bookmarker.test.modules.TestBookmarkerModule;
import com.purplecat.commons.logs.ILoggingService;

public class WebsiteParsingTests {
	
	@Inject
	public ILoggingService _logging;
	
	@Before
	public void beforeTest() {
		Injector injector = Guice.createInjector(new TestBookmarkerModule());
		_logging = injector.getInstance(ILoggingService.class);
	}

	@Test
	public void batotoInfoTest() {
		IWebsiteParser site = new BatotoWebsite(_logging);
		WebsiteInfo info = site.getInfo();
		assertEquals(info._name, "Batoto");
		assertEquals(info._website, "http://bato.to/");
	}

	@Test
	public void batotoParseTest() {
		IWebsiteParser site = new BatotoWebsite(_logging);		
		try {
			List<OnlineMediaItem> items = site.load();
			assertNotNull(items);
			assertTrue(items.size() > 0);
			for ( OnlineMediaItem item : items ) {
				checkItem(item);
			}
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void checkItem(OnlineMediaItem item) {
		DateTime date = DateTime.now().minusDays(10);
		System.out.println("checking " + item);
		
		Assert.assertTrue("no title", item._displayTitle != null && item._displayTitle.length() > 0);
		Assert.assertTrue("no title url", item._titleUrl != null && item._titleUrl.length() > 0);
		Assert.assertTrue("no website name", item._websiteName != null && item._websiteName.length() > 0);
		Assert.assertNotNull("date is null", item._updatedDate);
		Assert.assertTrue("invalid date", item._updatedDate.isAfter(date));
		
		if ( item._chapterUrl != null && item._chapterUrl.length() > 0) {
			Assert.assertTrue("invalid place", item._updatedPlace.compareTo(new Place()) > 0);
		}
	}

}