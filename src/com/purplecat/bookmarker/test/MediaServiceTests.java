package com.purplecat.bookmarker.test;

import static org.junit.Assert.fail;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.bookmarker.services.SavedMediaService;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.databases.DatabaseException;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.test.modules.TestBookmarkerModule;
import com.purplecat.commons.extensions.DateTimeFormats;
import com.purplecat.commons.tests.GetRandom;
import com.purplecat.commons.utils.StringUtils;

public class MediaServiceTests {
	
	private SavedMediaService _service;
	private IOnlineMediaRepository _onlineRepository;

	@Before
	public void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new TestBookmarkerModule());
		
		_service = injector.getInstance(SavedMediaService.class);
		_onlineRepository = injector.getInstance(IOnlineMediaRepository.class);
	}
	
	@Test
	public void testUpdateFromUrl() {
		try {
			String url = "http://www.batoto.net/read/_/107602/higaeri-quest_v1_ch1.b_by_obsession-scans/32";			
			DateTime now = new DateTime();
			
			Media matchingManga = _service.updateFromUrl(url);
			Assert.assertNotNull("Item was not found", matchingManga);
			Assert.assertNotNull("invalid id", matchingManga._id);
			Assert.assertEquals("chapter url mismatch", url, matchingManga._chapterUrl);
			Assert.assertTrue("updated date mismatch", Matchers.MatchDateTime(now, matchingManga._lastReadDate, 20));
			Assert.assertEquals(1, matchingManga._lastReadPlace._volume);
			Assert.assertEquals(1, matchingManga._lastReadPlace._chapter);
			Assert.assertEquals(2, matchingManga._lastReadPlace._subChapter);
			Assert.assertEquals(32, matchingManga._lastReadPlace._page);
			Assert.assertTrue("not saved", matchingManga._isSaved);
			
			//Immediate update succeeded; check that it was saved correctly
			Media updatedManga = _service.get(matchingManga._id);
			Assert.assertNotNull("Item was not found", updatedManga);
			checkEquals(matchingManga, updatedManga);
		} catch (ServiceException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}		
	}
	
	@Test
	public void testUpdateFromOnlineMedia() {
		try {
			OnlineMediaItem onlineItem = null;
			Media mediaItem = null;
			try {
				onlineItem = GetRandom.getItem(_onlineRepository.query());
				mediaItem = GetRandom.getItem(_service._database.query());
			}
			catch (DatabaseException e) {
				e.printStackTrace();
				fail("DatabaseEception thrown: " + e.getMessage());
			}
			onlineItem._mediaId = mediaItem._id;
			DateTime now = new DateTime();
			
			Media matchingManga = _service.updateFromOnlineItem(onlineItem);
			Assert.assertNotNull("Item was not found", matchingManga);
			Assert.assertEquals("invalid id", onlineItem._mediaId, matchingManga._id);
			Assert.assertEquals("chapter url mismatch", onlineItem._chapterUrl, matchingManga._chapterUrl);
			Assert.assertTrue("updated date mismatch", Matchers.MatchDateTime(now, matchingManga._lastReadDate, 20));
			Assert.assertEquals(onlineItem._updatedPlace._volume, matchingManga._lastReadPlace._volume);
			Assert.assertEquals(onlineItem._updatedPlace._chapter, matchingManga._lastReadPlace._chapter);
			Assert.assertEquals(onlineItem._updatedPlace._subChapter, matchingManga._lastReadPlace._subChapter);
			Assert.assertEquals(onlineItem._updatedPlace._page, matchingManga._lastReadPlace._page);
			Assert.assertTrue("not saved", matchingManga._isSaved);
			
			//Immediate update succeeded; check that it was saved correctly
			Media updatedManga = _service.get(matchingManga._id);
			Assert.assertNotNull("Item was not found", updatedManga);
			checkEquals(matchingManga, updatedManga);
		} catch (ServiceException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}		
	}
	
	@Test
	public void testUpdateFromSummaryPane() {
		try {
			Media mediaItem = null;
			try {
				mediaItem = GetRandom.getItem(_service._database.query());
			}
			catch (DatabaseException e) {
				e.printStackTrace();
				fail("DatabaseEception thrown: " + e.getMessage());
			}
			String url = "random url";
			Place newPlace = mediaItem._lastReadPlace.copy();
			newPlace._chapter++;
			DateTime now = new DateTime();
			
			Media matchingManga = _service.updatePlace(mediaItem, newPlace, url);
			Assert.assertNotNull("Item was not found", matchingManga);
			Assert.assertTrue("updated date mismatch", Matchers.MatchDateTime(now, matchingManga._lastReadDate, 20));
			Assert.assertEquals(mediaItem._lastReadPlace._volume, matchingManga._lastReadPlace._volume);
			Assert.assertEquals(mediaItem._lastReadPlace._chapter, matchingManga._lastReadPlace._chapter);
			Assert.assertEquals(mediaItem._lastReadPlace._subChapter, matchingManga._lastReadPlace._subChapter);
			Assert.assertEquals(mediaItem._lastReadPlace._page, matchingManga._lastReadPlace._page);
			Assert.assertTrue("not saved", matchingManga._isSaved);
			Assert.assertEquals(url, matchingManga._chapterUrl);
		} catch (ServiceException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}		
	}
	
	@Test
	public void testLoadSummary() {
		try {
			Media mediaItem = new Media();
			mediaItem._id = 1;
			mediaItem._titleUrl = "http://bato.to/comic/_/comics/iede-shounen-to-natsu-no-yoru-r11079";
			
			
			OnlineMediaItem updatedManga = _service.loadMediaSummary(mediaItem._id, mediaItem._titleUrl);
			Assert.assertNotNull("Item was not found", updatedManga);
			Assert.assertTrue("no genres", updatedManga._genres.size() > 0);
			Assert.assertTrue("no summary", updatedManga._summary.length() > 0);
		} catch (ServiceException e) {
			e.printStackTrace();
			fail("Exception thrown: " + e.getMessage());
		}		
	}
	
	protected void checkMediaItem(Media media) {
		Assert.assertTrue("id is not valid", media._id > 0);
		Assert.assertTrue("no title", !StringUtils.isNullOrEmpty(media.getDisplayTitle()));
	}
	
	protected void checkSavedMediaItem(Media media) {
		Assert.assertTrue("id is not valid", media._id > 0);
		Assert.assertTrue("no title", !StringUtils.isNullOrEmpty(media.getDisplayTitle()));
		Assert.assertTrue("not saved", media._isSaved);
		Assert.assertNotNull("no last read date", media._lastReadDate);		
		Assert.assertTrue("invalid last read date: " + media._lastReadDate.toString(DateTimeFormats.SQLITE_DATE_FORMAT), media._lastReadDate.isAfter(new DateTime(2000, 1, 1, 0, 0)));
		Assert.assertNotNull("no place", media._lastReadPlace);
	}
	
	protected void checkEquals(Media expected, Media actual) {
		Assert.assertEquals("id mismatch", expected._id, actual._id);
		Assert.assertEquals("title mismatch", expected.getDisplayTitle(), actual.getDisplayTitle());
		Assert.assertEquals("saved mismatch", expected._isSaved, actual._isSaved);
		Assert.assertEquals("last read date mismatch", 
				expected._lastReadDate.toString(DateTimeFormats.SQLITE_DATE_FORMAT), 
				actual._lastReadDate.toString(DateTimeFormats.SQLITE_DATE_FORMAT));		
		Assert.assertEquals("place mismatch", expected._lastReadPlace, actual._lastReadPlace);
	}
}
