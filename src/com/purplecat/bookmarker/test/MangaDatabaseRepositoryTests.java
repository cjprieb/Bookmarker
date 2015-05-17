package com.purplecat.bookmarker.test;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.models.EFavoriteState;
import com.purplecat.bookmarker.models.EStoryState;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.databases.MediaDatabaseRepository;
import com.purplecat.bookmarker.test.modules.TestDatabaseModule;
import com.purplecat.commons.extensions.DateTimeFormats;
import com.purplecat.commons.tests.GetRandom;

public class MangaDatabaseRepositoryTests extends DatabaseConnectorTestBase {
	
	private static MediaDatabaseRepository _database;
	private static List<Media> _randomSavedMedia;
	private static String[] _sampleTitles = { "360° material", "chihayafuru", "7 centi!", "gozen 3-ji no muhouchitai", "chikutaku bonbon", 
		"d. n. angel", "yume no shizuku ougon no torikago" };

	@BeforeClass
	public static void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new TestDatabaseModule());
		
		_database = injector.getInstance(MediaDatabaseRepository.class);
		//_database._log = new ConsoleLog();
		
		_randomSavedMedia = _database.querySavedMedia();
		Assert.assertNotNull("List is null", _randomSavedMedia);
		Assert.assertTrue("List has no elements", _randomSavedMedia.size() > 0);
		Assert.assertTrue("item not marked as saved: ", _randomSavedMedia.get(0)._isSaved);
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
	public void testQueryById() {
		try {
			Media media = GetRandom.getItem(_randomSavedMedia);			
			Media item = _database.queryById(media._id);
			Assert.assertNotNull("List is null", item);
			Assert.assertEquals("Element doesn't match id", media._id, item._id);
			checkSavedMediaItem(item);
			checkEquals(media, item);
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
			media._isSaved = false;
			_database.insert(media);
		} catch (ServiceException e) {
			//cannot save 'unsaved' media through this method; so exception is expected
			if ( e.getErrorCode() != ServiceException.INVALID_DATA ) {
				Assert.fail("Service Exception occurred");
			}
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
			media._storyState = EStoryState.NEW_BOOKMARK;
			media._notes = "Some notes!";
			media._rating = EFavoriteState.GOOD;
			media._lastReadDate = new DateTime();
			media._isSaved = true;
			_database.insert(media);
			
			Assert.assertTrue("Invalid id", media._id > 0);	
			System.out.println("Media added: "  + media._id);
			
			Media item = _database.queryById(media._id);
			Assert.assertNotNull("item is null", item);
			checkSavedMediaItem(item);
			checkEquals(media, item);
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
			media._storyState = EStoryState.MIDDLE_CHAPTER;
			media._notes = "Some notes!";
			media._rating = EFavoriteState.AWESOME;
			media._isComplete = true;
			media._lastReadDate = new DateTime();
			
			_database.update(media);
			System.out.println("looking up " + media._id + " after update");
			
			Media item = _database.queryById(media._id);
			Assert.assertNotNull("item is null", item);
			checkSavedMediaItem(item);
			checkEquals(media, item);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testUpdateNonSaved() {
		try {
			Media media = _database.queryById(17083);//known unsaved item
			
			media._displayTitle = GetRandom.getString(6);
			media._lastReadPlace._chapter++;
			media._chapterURL = "http://sampleurl";
			media._lastReadDate = new DateTime();
			media._isSaved = true;
			
			_database.update(media);
			System.out.println("looking up " + media._id + " after update");
			
			Media item = _database.queryById(media._id);
			Assert.assertNotNull("item is null", item);
			checkSavedMediaItem(item);
			checkEquals(media, item);
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

			
			Media item = _database.queryById(media._id);
			Assert.assertNull("item is not null", item);
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
		Assert.assertTrue("invalid last read date: " + media._lastReadDate.toString(DateTimeFormats.SQLITE_DATE_FORMAT), media._lastReadDate.isAfter(new DateTime(2000, 1, 1, 0, 0)));
		Assert.assertNotNull("no place", media._lastReadPlace);
	}
	
	protected void checkEquals(Media expected, Media actual) {
		Assert.assertEquals("id mismatch", expected._id, actual._id);
		Assert.assertEquals("title mismatch", expected._displayTitle, actual._displayTitle);
		Assert.assertEquals("saved mismatch", expected._isSaved, actual._isSaved);
		if ( expected._lastReadDate != null && actual._lastReadDate != null ) {
			Assert.assertEquals("last read date mismatch", 
				expected._lastReadDate.toString(DateTimeFormats.SQLITE_DATE_FORMAT), 
				actual._lastReadDate.toString(DateTimeFormats.SQLITE_DATE_FORMAT));
		}
		else {
			Assert.assertEquals("last read date mismatch", expected._lastReadDate, actual._lastReadDate);
		}
		Assert.assertEquals("place mismatch", expected._lastReadPlace, actual._lastReadPlace);
		//Assert.assertEquals("folder mismatch", expected._folder, actual._folder);
		//Assert.assertEquals("updated mismatch", expected._isUpdated, actual._isUpdated);
		Assert.assertEquals("updated mismatch", expected._notes, actual._notes);
		Assert.assertEquals("updated mismatch", expected._rating, actual._rating);
		Assert.assertEquals("updated mismatch", expected._storyState, actual._storyState);
		Assert.assertEquals("updated mismatch", expected._isComplete, actual._isComplete);
	}
}
