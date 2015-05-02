package com.purplecat.bookmarker.test;

import static org.junit.Assert.fail;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.DatabaseMangaService;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.view.swing.SwingBookmarkerModule;
import com.purplecat.commons.extensions.DateTimeFormats;
import com.purplecat.commons.tests.Matchers;

public class DatabaseMangaServiceTests {
	
	private DatabaseMangaService _service;

	@Before
	public void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new SwingBookmarkerModule());
		
		_service = injector.getInstance(DatabaseMangaService.class);
	}

	/*@Test
	public void testList() {
		try {
			List<Media> list = _service.list();
			Assert.assertNotNull("List is null", list);
			Assert.assertTrue("List has no elements", list.size() > 0);
		} catch (ServiceException e) {
			fail("Exception thrown: " + e.getMessage());
		}		
	}*/

	@Test
	public void testAdd() {
		try {
			Media manga = new Media();
			_service.add(manga);
			Assert.assertTrue("Invalid id", manga._id > 0);
			
			Media addedManga = _service.get(manga._id);
			Assert.assertEquals("Item was not added", manga._id, addedManga._id);
		} catch (ServiceException e) {
			fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public void testEdit() {
		try {
			Media manga = new Media();
			manga._id = 1;
			_service.edit(manga);
		} catch (ServiceException e) {
			fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public void testInvalidEdit() {
		try {
			Media manga = new Media();
			_service.edit(manga);
			fail("No exception thrown");			
		} catch (ServiceException e) {
			Assert.assertEquals("Wrong type of exception", ServiceException.INVALID_ID, e.getErrorCode());
		}
	}

	@Test
	public void testGet() {
		try {
			long id = 1;
			Media manga = _service.get(id);		
			Assert.assertNotNull("Item is null", manga);
		} catch (ServiceException e) {
			fail("Exception thrown: " + e.getMessage());
		}
	}

	@Test
	public void testInvalidGet() {
		try {
			_service.get(-1);
			fail("No exception thrown");
		} catch (ServiceException e) {
			Assert.assertEquals("Wrong type of exception", ServiceException.INVALID_ID, e.getErrorCode());
		}
	}

	@Test
	public void testRemove() {
		try {
			long id = 1;
			_service.remove(id);
			
			Media manga = _service.get(id);
			Assert.assertNull("Item was not removed", manga);
		} catch (ServiceException e) {
			fail("Exception thrown: " + e.getMessage());
		}
	}
	
	@Test
	public void testUpdateFromUrl() {
		try {
			String url = "http://www.batoto.net/read/_/107602/higaeri-quest_v1_ch1.b_by_obsession-scans/32";			
			Calendar now = Calendar.getInstance();
			
			Media matchingManga = _service.updateFromUrl(url);
			Assert.assertNotNull("Item was not found", matchingManga);
			Assert.assertNotNull("invalid id", matchingManga._id);
			Assert.assertEquals("chapter url mismatch", url, matchingManga._chapterURL);
			Assert.assertTrue("updated date mismatch", Matchers.MatchDateTime(now, matchingManga._lastReadDate, 20));
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
	
	protected void checkMediaItem(Media media) {
		Assert.assertTrue("id is not valid", media._id > 0);
		Assert.assertTrue("no title", media._displayTitle != null && media._displayTitle.length() > 0);
	}
	
	protected void checkSavedMediaItem(Media media) {
		Assert.assertTrue("id is not valid", media._id > 0);
		Assert.assertTrue("no title", media._displayTitle != null && media._displayTitle.length() > 0);
		Assert.assertTrue("not saved", media._isSaved);
		Assert.assertNotNull("no last read date", media._lastReadDate);		
		Assert.assertTrue("invalid last read date: " + DateTimeFormats.FORMAT_SQLITE_DATE.format(media._lastReadDate), media._lastReadDate.after(new GregorianCalendar(2000, 1, 1)));
		Assert.assertNotNull("no place", media._lastReadPlace);
	}
	
	protected void checkEquals(Media expected, Media actual) {
		Assert.assertEquals("id mismatch", expected._id, actual._id);
		Assert.assertEquals("title mismatch", expected._displayTitle, actual._displayTitle);
		Assert.assertEquals("saved mismatch", expected._isSaved, actual._isSaved);
		Assert.assertEquals("last read date mismatch", 
				DateTimeFormats.FORMAT_SQLITE_DATE.format(expected._lastReadDate), 
				DateTimeFormats.FORMAT_SQLITE_DATE.format(actual._lastReadDate));		
		Assert.assertEquals("place mismatch", expected._lastReadPlace, actual._lastReadPlace);
	}
}
