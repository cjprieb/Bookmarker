package com.purplecat.bookmarker.test;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.databases.MangaDatabaseConnector;
import com.purplecat.bookmarker.test.modules.TestDatabaseModule;
import com.purplecat.commons.extensions.DateTimeFormats;
import com.purplecat.commons.tests.GetRandom;

public class MangaDatabaseConnectorTests extends DatabaseConnectorTests {
	
	private static MangaDatabaseConnector _database;
	private static List<Media> _randomSavedMedia;
	private static List<Media> _randomNonSavedMedia;
	private static String[] _sampleTitles = { "360° material", "chihayafuru", "7 centi!", "gozen 3-ji no muhouchitai", "chikutaku bonbon", 
		"d. n. angel", "yume no shizuku ougon no torikago" };

	@BeforeClass
	public static void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new TestDatabaseModule());
		
		_database = injector.getInstance(MangaDatabaseConnector.class);
		//_database._log = new ConsoleLog();
		
		_randomSavedMedia = _database.querySavedMedia();
		Assert.assertNotNull("List is null", _randomSavedMedia);
		Assert.assertTrue("List has no elements", _randomSavedMedia.size() > 0);
		Assert.assertTrue("item not marked as saved: ", _randomSavedMedia.get(0)._isSaved);
		
		_randomNonSavedMedia = _database.queryNonSavedMedia();
		Assert.assertNotNull("List is null", _randomNonSavedMedia);
		Assert.assertTrue("List has no elements", _randomNonSavedMedia.size() > 0);	
		Assert.assertFalse("item marked as saved: ", _randomNonSavedMedia.get(0)._isSaved);
	}

	@Test
	public void testQuery() {
		try {
			List<Media> list = _database.query();
			Assert.assertNotNull("List is null", list);
			Assert.assertTrue("List has no elements", list.size() > 0);
			checkMediaItem(list.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testQueryForSaved() {
		try {
			List<Media> list = _database.querySavedMedia();
			Assert.assertNotNull("List is null", list);
			Assert.assertTrue("List has no elements", list.size() > 0);
			checkSavedMediaItem(GetRandom.getItem(list));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testQueryForNonSaved() {
		try {
			List<Media> list = _database.queryNonSavedMedia();
			Assert.assertNotNull("List is null", list);
			Assert.assertTrue("List has no elements", list.size() > 0);
			Assert.assertFalse("item marked as saved: ", list.get(0)._isSaved);
			checkMediaItem(GetRandom.getItem(list));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testQueryById() {
		try {
			Media media = GetRandom.getItem(_randomSavedMedia);
			List<Media> list = _database.query(media._id);
			Assert.assertNotNull("Query for id list is null", list);
			Assert.assertEquals("List doesn't have 1 element", 1, list.size());
			Assert.assertEquals("Element doesn't match id", media._id, list.get(0)._id);
			checkMediaItem(list.get(0));
			checkEquals(media, list.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testInsert() {
		try {
			Media media = new Media();
			media._displayTitle = GetRandom.getString(6);
			_database.insert(media);
			
			Assert.assertTrue("Invalid id", media._id > 0);	
			System.out.println("Media added: "  + media._id);
			
			List<Media> list = _database.query(media._id);
			Assert.assertNotNull("List is null", list);
			Assert.assertEquals("List doesn't have 1 element", 1, list.size());
			checkMediaItem(list.get(0));
			checkEquals(media, list.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testInsertSaved() {
		try {
			Media media = new Media();
			media._displayTitle = GetRandom.getString(6);
			media._lastReadPlace._chapter++;
			media._chapterURL = "http://sampleurl";
			media._lastReadDate = Calendar.getInstance();
			media._isSaved = true;
			_database.insert(media);
			
			Assert.assertTrue("Invalid id", media._id > 0);	
			System.out.println("Media added: "  + media._id);
			
			List<Media> list = _database.query(media._id);
			Assert.assertNotNull("List is null", list);
			Assert.assertEquals("List doesn't have 1 element", 1, list.size());
			checkSavedMediaItem(list.get(0));
			checkEquals(media, list.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testUpdate() {
		try {
			Media media = GetRandom.getItem(_randomSavedMedia);
			
			media._displayTitle = GetRandom.getString(6);
			media._lastReadPlace._chapter++;
			media._chapterURL = "http://sampleurl";
			media._lastReadDate = Calendar.getInstance();
			
			_database.update(media);
			System.out.println("looking up " + media._id + " after update");
			List<Media> list = _database.query(media._id);
			Assert.assertNotNull("List is null", list);
			Assert.assertEquals("List doesn't have 1 element: " + media._id, 1, list.size());
			checkSavedMediaItem(list.get(0));
			checkEquals(media, list.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testUpdateNonSaved() {
		try {
			Media media = GetRandom.getItem(_randomNonSavedMedia);
			
			media._displayTitle = GetRandom.getString(6);
			media._lastReadPlace._chapter++;
			media._chapterURL = "http://sampleurl";
			media._lastReadDate = Calendar.getInstance();
			media._isSaved = true;
			
			_database.update(media);
			System.out.println("looking up " + media._id + " after update");
			List<Media> list = _database.query(media._id);
			Assert.assertNotNull("List is null", list);
			Assert.assertEquals("List doesn't have 1 element", 1, list.size());
			checkSavedMediaItem(list.get(0));
			checkEquals(media, list.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testRemove() {
		try {
			Media media = GetRandom.getItem(_randomSavedMedia);
			_database.delete(media._id);
			
			List<Media> list = _database.query(media._id);
			Assert.assertNotNull("List is null", list);
			Assert.assertTrue("List has elements", list.size() == 0);
			_randomSavedMedia.remove(media);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testQueryByTitle() {
		try {
			for ( String title : _sampleTitles ) {
				System.out.println("looking for \"" + title + "\"");
				List<Media> list = _database.queryByTitle(title);
				Assert.assertNotNull("List is null", list);
				Assert.assertTrue("No elements matching " + title, list.size() > 0);
				System.out.println("  match found: "  + list.get(0)._displayTitle);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
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
