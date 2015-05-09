package com.purplecat.bookmarker.test;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.databases.MediaDatabaseRepository;
import com.purplecat.bookmarker.services.databases.OnlineMediaDatabase;
import com.purplecat.bookmarker.test.modules.TestDatabaseModule;
import com.purplecat.commons.tests.GetRandom;

public class OnlineDatabaseRepositoryTests extends DatabaseConnectorTestBase {
	
	private static OnlineMediaDatabase _database;
	private static MediaDatabaseRepository _savedDatabase;
	private static List<OnlineMediaItem> _randomItems;

	@BeforeClass
	public static void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new TestDatabaseModule());	
		_database = injector.getInstance(OnlineMediaDatabase.class);
		_savedDatabase = injector.getInstance(MediaDatabaseRepository.class);
		
		_randomItems = _database.query();
		Assert.assertNotNull("List is null", _randomItems);
		Assert.assertTrue("List has no elements", _randomItems.size() > 0);
	}

	@Test
	public void testQueryById() {
		try {
			OnlineMediaItem expected = GetRandom.getItem(_randomItems);
			OnlineMediaItem actual = _database.queryById(expected._id);
			Assert.assertNotNull("Query for id list is null", actual);
			Assert.assertEquals("Element doesn't match id", expected._id, actual._id);
			checkItem(actual);
			checkEquals(expected, actual);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testInsert() {
		try {
			Media media = _savedDatabase.queryById(15);
					
			OnlineMediaItem item = new OnlineMediaItem();
			item._updatedPlace._volume = 1;
			item._updatedPlace._chapter = 12;
			item._mediaId = media._id;
			item._lastReadDate = new DateTime(media._lastReadDate);
			item._lastReadPlace = media._lastReadPlace;
			item._displayTitle = media._displayTitle;
			item._chapterUrl = "http://bato.to/read/_/319045/shana-oh-yoshitsune_v10_ch38_by_easy-going-scans";
			item._titleUrl = "http://bato.to/comic/_/comics/shana-oh-yoshitsune-r5256";
			item._updatedDate = new DateTime();
			item._isIgnored = true;
			item._newlyAdded = false;
			item._rating = .75;
			item._websiteName = "Batoto";
			_database.insert(item);
			
			Assert.assertTrue("Invalid id", item._id > 0);
			
			OnlineMediaItem actual = _database.queryById(item._id);
			Assert.assertNotNull("Item is null", actual);
			checkItem(actual);
			checkEquals(item, actual);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testUpdate() {
		try {
			OnlineMediaItem item = GetRandom.getItem(_randomItems);
			item._updatedPlace._volume = 1;
			item._updatedPlace._chapter = 12;
			item._chapterUrl = "http://bato.to/read/_/319045/shana-oh-yoshitsune_v10_ch38_by_easy-going-scans";
			item._titleUrl = "http://bato.to/comic/_/comics/shana-oh-yoshitsune-r5256";
			item._updatedDate = new DateTime();
			item._isIgnored = false;
			item._newlyAdded = true;
			item._rating = .75;
			item._websiteName = "Batoto";
			
			_database.update(item);
			System.out.println("looking up " + item._id + " after update");
			OnlineMediaItem actual = _database.queryById(item._id);
			Assert.assertNotNull("Item is null", actual);
			checkItem(actual);
			checkEquals(item, actual);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testFind() {
		try {					
			OnlineMediaItem item = new OnlineMediaItem();
			item._updatedPlace._volume = 1;
			item._updatedPlace._chapter = 12;
			item._displayTitle = "Shana oh Yoshitsune";
			item._chapterUrl = "http://bato.to/read/_/319045/shana-oh-yoshitsune_v10_ch38_by_easy-going-scans";
			item._titleUrl = "http://bato.to/comic/_/comics/shana-oh-yoshitsune-r5256";
			item._updatedDate = new DateTime();
			OnlineMediaItem result = _database.findOrCreate(item);
			
			Assert.assertNotNull("Item is null", result);			
			Assert.assertTrue("Invalid id", result._id > 0);
			Assert.assertTrue("Invalid media id", result._mediaId > 0);
			
			checkItem(result);
			checkEquals(item, result);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}


	@Test
	public void testRemove() {
		try {
			OnlineMediaItem media = GetRandom.getItem(_randomItems);
			_database.delete(media._id);

			OnlineMediaItem actual = _database.queryById(media._id);
			Assert.assertNull("Item is null: " + media._id, actual);
			_randomItems.remove(media);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}
	
	protected void checkItem(OnlineMediaItem pattern) {
		Assert.assertTrue("id is not valid", pattern._id > 0);
		Assert.assertTrue("no chapter url", pattern._chapterUrl != null && pattern._chapterUrl.length() > 0);
		Assert.assertNotNull("no date", pattern._updatedDate);
	}
	
	protected void checkEquals(OnlineMediaItem expected, OnlineMediaItem actual) {
		Assert.assertEquals("chapter url mismatch", expected._chapterUrl, actual._chapterUrl);
		Assert.assertEquals("title url mismatch", expected._titleUrl, actual._titleUrl);
		Assert.assertEquals("place mismatch", expected._updatedPlace, actual._updatedPlace);
		Assert.assertEquals("date mismatch", expected._updatedDate, actual._updatedDate);
		Assert.assertEquals("isIgnored mismatch", expected._isIgnored, actual._isIgnored);
		Assert.assertEquals("newlyAdded mismatch", expected._newlyAdded, actual._newlyAdded);
		Assert.assertEquals("rating mismatch", expected._rating, actual._rating, .0005);
		Assert.assertEquals("website name mismatch", expected._websiteName, actual._websiteName);
		
		Assert.assertEquals("media mismatch", expected._mediaId, actual._mediaId);
		Assert.assertEquals("title mismatch", expected._displayTitle, actual._displayTitle);
		Assert.assertEquals("saved place", expected._lastReadPlace, actual._lastReadPlace);
		if ( expected._lastReadDate != null && actual._lastReadDate != null ) {
			Assert.assertTrue("saved date", expected._lastReadDate.compareTo(actual._lastReadDate) == 0);
		}
		else {
			Assert.assertEquals("saved date", expected._lastReadDate, actual._lastReadDate);
		}
	}
}
