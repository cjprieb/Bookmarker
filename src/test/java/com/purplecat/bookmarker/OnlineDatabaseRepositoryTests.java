package com.purplecat.bookmarker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.models.Genre;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.bookmarker.services.databases.DatabaseException;
import com.purplecat.bookmarker.services.databases.GenreDatabaseRepository;
import com.purplecat.bookmarker.services.databases.IOnlineMediaRepository;
import com.purplecat.bookmarker.services.databases.MediaDatabaseRepository;
import com.purplecat.bookmarker.sql.ConnectionManager;
import com.purplecat.bookmarker.sql.IConnectionManager;
import com.purplecat.bookmarker.modules.TestDatabaseModule;
import com.purplecat.commons.tests.GetRandom;

public class OnlineDatabaseRepositoryTests extends DatabaseConnectorTestBase {

	private static IConnectionManager _connectionManager;
	private static IOnlineMediaRepository _database;
	private static MediaDatabaseRepository _savedDatabase;
	private static GenreDatabaseRepository _genreDatabase;
	private static List<OnlineMediaItem> _randomSavedItems = new ArrayList<OnlineMediaItem>();
	private static List<OnlineMediaItem> _randomUnsavedItems = new ArrayList<OnlineMediaItem>();
	private static List<Genre> _randomGenres = new ArrayList<Genre>();

	@BeforeClass
	public static void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new TestDatabaseModule());	
		_database = injector.getInstance(IOnlineMediaRepository.class);
		_savedDatabase = injector.getInstance(MediaDatabaseRepository.class);
		_genreDatabase = injector.getInstance(GenreDatabaseRepository.class);
		_connectionManager = injector.getInstance(ConnectionManager.class);
		
		try {
			_connectionManager.open();
			_randomGenres = _genreDatabase.query();
			
			List<OnlineMediaItem> randomItems = _database.query();
			Assert.assertNotNull("List is null", randomItems);
			Assert.assertTrue("List has no elements", randomItems.size() > 0);
			
			for(OnlineMediaItem item : randomItems) {
				if ( item._isSaved ) {
					_randomSavedItems.add(item);
				}
				else {
					_randomUnsavedItems.add(item);
				}
			}
			Assert.assertTrue("List has no saved elements", _randomSavedItems.size() > 0);
		} 
		catch (DatabaseException e) {
			e.printStackTrace();
			Assert.fail("Database connection failed");
		}
		finally {
			_connectionManager.close();
		}
	}
	
	@Before
	public void openConnection() {
		try {
			_connectionManager.open();
		} 
		catch (DatabaseException e) {
			e.printStackTrace();
			Assert.fail("Database connection failed");
			_connectionManager.close();
		}		
	}
	
	@After
	public void closeConnection() {
		_connectionManager.close();	
	}

	@Test
	public void testQueryById() {
		try {
			OnlineMediaItem expected = GetRandom.getItem(_randomSavedItems);
			OnlineMediaItem actual = _database.queryById(expected._id);
			Assert.assertNotNull("Query for id list is null", actual);
			Assert.assertEquals("Element doesn't match id", expected._id, actual._id);
			Assert.assertTrue("no genres", actual._genres.size() > 0);
			Assert.assertEquals("Element doesn't match genre size", expected._genres.size(), actual._genres.size());
			checkItem(actual);
			checkEquals(expected, actual);
			
			Media actualMedia = _savedDatabase.queryById(expected._mediaId);
			checkEquals(actualMedia, expected);
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
			item._displayTitle = media.getDisplayTitle();
			item._chapterUrl = "http://bato.to/read/_/319045/shana-oh-yoshitsune_v10_ch38_by_easy-going-scans";
			item._titleUrl = "http://bato.to/comic/_/comics/shana-oh-yoshitsune-r5256";
			item._updatedDate = new DateTime();
			item._isIgnored = true;
			item._newlyAdded = false;
			item._rating = .75;
			item._websiteName = "Batoto";
			item._folderId = media._folderId;
			item._lastReadUrl = media._chapterUrl;
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
	public void testInsertNew() {
		try {					
			OnlineMediaItem item = new OnlineMediaItem();
			item._updatedPlace._volume = 1;
			item._updatedPlace._chapter = 12;
			item._mediaId = -1;
			item._lastReadPlace = new Place();
			item._displayTitle = GetRandom.getString(10);
			item._chapterUrl = "http://bato.to/read/_/319045/shana-oh-yoshitsune_v10_ch38_by_easy-going-scans";
			item._titleUrl = "http://bato.to/comic/_/comics/shana-oh-yoshitsune-r5256";
			item._updatedDate = new DateTime();
			item._isIgnored = true;
			item._newlyAdded = false;
			item._rating = .75;
			item._websiteName = "Batoto";
			_database.insert(item);
			System.out.println("item created: " + item._id + " (media id: " + item._mediaId + ")");
			
			Assert.assertTrue("Invalid id", item._id > 0);
			item = _database.queryById(item._id);
			
			Assert.assertNotNull("item not found", item);
			
			long itemId = item._id;
			long mediaId = item._mediaId;
			System.out.println("item found: " + item._id + " (media id: " + item._mediaId + ")");
			
			OnlineMediaItem actual = _database.findOrCreate(item);
			Assert.assertNotNull("Item is null", actual);
			Assert.assertFalse("Item is marked new", actual._newlyAdded);
			Assert.assertEquals("Invalid id", itemId, actual._id);
			Assert.assertEquals("Invalid media id", mediaId, actual._mediaId);
			checkItem(actual);
			checkEquals(item, actual);
			
//			Media media = _savedDatabase.queryById(item._mediaId);
//			Assert.assertTrue("no alt titles", media._altTitles.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}
	
	@Test
	public void testStoryState() {		
		try {
			OnlineMediaItem item = GetRandom.getItem(_randomSavedItems);
			Media media = _savedDatabase.queryById(item._mediaId);
			media._folderId = 2;
			_savedDatabase.update(media);
			
			OnlineMediaItem actual = _database.queryByMediaId(media._id).get(0);
			checkItem(actual);
			Assert.assertEquals(media._folderId, actual._folderId);
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void testUpdate() {
		try {
			OnlineMediaItem item = GetRandom.getItem(_randomSavedItems);
			Media media = _savedDatabase.queryById(item._mediaId);
			item._updatedPlace._volume = 10;
			item._updatedPlace._chapter = 380;
			item._chapterUrl = "http://bato.to/read/_/319045/shana-oh-yoshitsune_v10_ch38_by_easy-going-scans";
			item._titleUrl = "http://bato.to/comic/_/comics/shana-oh-yoshitsune-r5256";
			item._updatedDate = new DateTime();
			item._isIgnored = false;
			item._newlyAdded = true;
			item._rating = .75;
			item._websiteName = "Batoto";
			
			item._genres.clear();
			for ( int i = 0; i < GetRandom.getInteger(0, 10); i++ ) {
				item._genres.add(GetRandom.getItem(_randomGenres));
			}
			
			_database.update(item);
			System.out.println("looking up " + item._id + " after update");
			OnlineMediaItem actual = _database.queryById(item._id);
			Assert.assertNotNull("Item is null", actual);
			Assert.assertTrue("no genres", item._genres.size() > 0);
			checkItem(actual);
			checkEquals(item, actual);

			for (Genre newGenre : actual._genres) {
				boolean bFound = false;
				for ( Genre genre : item._genres ) {
					if ( genre._id == newGenre._id ) {
						bFound = true; break;
					}
				}
				Assert.assertTrue("no match found for " + newGenre, bFound);
			}
			for (Genre genre : item._genres ) {
				boolean bFound = false;
				for ( Genre newGenre : actual._genres ) {
					if ( genre._id == newGenre._id ) {
						bFound = true; break;
					}
				}
				Assert.assertTrue("no match found for " + genre, bFound);
			}
			
			Media updatedMedia = _savedDatabase.queryById(item._mediaId);
			if ( updatedMedia._updatedPlace.compareTo(item._updatedPlace) == 0 ) { 
				Assert.assertTrue("old place was greater", media._updatedPlace.compareTo(updatedMedia._updatedPlace) <= 0);
			}
			else {
				Assert.assertTrue("current place is greater", media._updatedPlace.compareTo(item._updatedPlace) > 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testFindOrCreate() {
		try {					
			OnlineMediaItem item = GetRandom.getItem(_randomUnsavedItems);
			item._id = 0;
			item._mediaId = 0;
			item._updatedPlace._chapter++;
			item._chapterUrl = "http://bato.to/read/_/319045/shana-oh-yoshitsune_v10_ch38_by_easy-going-scans";
			item._titleUrl = "http://bato.to/comic/_/comics/shana-oh-yoshitsune-r5256";
			item._updatedDate = new DateTime();
			item._newlyAdded = true;
			OnlineMediaItem result = _database.findOrCreate(item);
			
			Assert.assertNotNull("Item is null", result);		
			Assert.assertTrue("Item is marked new", result._newlyAdded);	
			Assert.assertTrue("Invalid id", result._id > 0);
			Assert.assertTrue("Invalid media id", result._mediaId > 0);
			
			Media mediaItem = _savedDatabase.queryById(item._mediaId);
			Assert.assertEquals("url not matched: " + item._mediaId, item._titleUrl, mediaItem._updatedUrl);
			Assert.assertEquals(item._updatedPlace, mediaItem._updatedPlace);
			Assert.assertEquals(item._updatedDate, mediaItem._updatedDate);
			
			checkItem(result);
			checkEquals(item, result);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testSavedFindOrCreate() {
		try {					
			OnlineMediaItem item = GetRandom.getItem(_randomSavedItems);
			item._id = 0;
			item._mediaId = 0;
			item._updatedPlace._chapter++;
			item._chapterUrl = "http://bato.to/read/_/319045/shana-oh-yoshitsune_v10_ch38_by_easy-going-scans";
			item._titleUrl = "http://bato.to/comic/_/comics/shana-oh-yoshitsune-r5256";
			item._updatedDate = new DateTime();
			item._newlyAdded = true;
			OnlineMediaItem result = _database.findOrCreate(item);
			
			Assert.assertNotNull("Item is null", result);			
			Assert.assertTrue("Invalid id", result._id > 0);
			Assert.assertTrue("Invalid media id", result._mediaId > 0);
			Assert.assertTrue("no genres", result._genres.size() > 0);
			
			Media mediaItem = _savedDatabase.queryById(item._mediaId);
			Assert.assertEquals(item._chapterUrl, mediaItem._updatedUrl);
			Assert.assertEquals(item._updatedPlace, mediaItem._updatedPlace);
			Assert.assertEquals(item._updatedDate, mediaItem._updatedDate);
			
			checkItem(result);
			checkEquals(item, result);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testSavedFindOrCreateSame() {
		try {					
			OnlineMediaItem item = GetRandom.getItem(_randomSavedItems);
			item._id = 0;
			item._mediaId = 0;
			item._chapterUrl = "http://bato.to/read/_/3190456/shana-oh-yoshitsune_v10_ch38_by_easy-going-scans";
			item._titleUrl = "http://bato.to/comic/_/comics/shana-oh-yoshitsune-r52566";
			item._updatedDate = new DateTime();
			item._newlyAdded = true;
			OnlineMediaItem result = _database.findOrCreate(item);
			
			Assert.assertNotNull("Item is null", result);			
			Assert.assertTrue("Invalid id", result._id > 0);
			Assert.assertTrue("Invalid media id", result._mediaId > 0);
			Assert.assertTrue("no genres", result._genres.size() > 0);
			
			Media mediaItem = _savedDatabase.queryById(item._mediaId);
			System.out.println("(saveOrCreateSame) media item found: " + mediaItem);
			Assert.assertEquals(item._chapterUrl, mediaItem._updatedUrl);
			Assert.assertEquals(item._updatedPlace, mediaItem._updatedPlace);
			Assert.assertEquals(item._updatedDate, mediaItem._updatedDate);
			
			checkItem(result);
			checkEquals(item, result);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testSavedFindOrCreateSlightlyOlder() {
		try {					
			OnlineMediaItem item = GetRandom.getItem(_randomSavedItems);
			item._id = 0;
			item._mediaId = 0;
			item._chapterUrl = "http://bato.to/read/_/3190456/shana-oh-yoshitsune_v10_ch38_by_easy-going-scans";
			item._titleUrl = "http://bato.to/comic/_/comics/shana-oh-yoshitsune-r52566";
			item._updatedDate = item._updatedDate.plusMinutes(2);
			OnlineMediaItem result = _database.findOrCreate(item);
			
			Assert.assertNotNull("Item is null", result);			
			Assert.assertTrue("Invalid id", result._id > 0);
			Assert.assertTrue("Invalid media id", result._mediaId > 0);
			Assert.assertTrue("no genres", result._genres.size() > 0);
			Assert.assertFalse("Item is marked newly added", item._newlyAdded);
			
//			Media mediaItem = _savedDatabase.queryById(item._mediaId);
//			Assert.assertEquals(item._chapterUrl, mediaItem._updatedUrl);
//			Assert.assertEquals(item._updatedPlace, mediaItem._updatedPlace);
//			Assert.assertNotEquals(item._updatedDate, mediaItem._updatedDate);
			
			checkItem(result);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testQueryByMediaId() {
		try {					
			OnlineMediaItem item = GetRandom.getItem(_randomSavedItems.stream().filter(m -> m.isUpdated()).collect(Collectors.toList()));
			List<OnlineMediaItem> result = _database.queryByMediaId(item._mediaId);
			
			Assert.assertNotNull("Item is null", result);			
			Assert.assertTrue("No items found", result.size() > 0);
			Assert.assertTrue("Invalid media id", result.stream().anyMatch(a -> a._id == item._id));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testRemove() {
		try {
			OnlineMediaItem media = GetRandom.getItem(_randomUnsavedItems);
			_database.delete(media._id);

			OnlineMediaItem actual = _database.queryById(media._id);
			Assert.assertNull("Item is null: " + media._id, actual);
			_randomUnsavedItems.remove(media);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}
	
	protected void checkItem(OnlineMediaItem pattern) {
		Assert.assertTrue("id is not valid", pattern._id > 0);
		//Assert.assertTrue("no chapter url", pattern._chapterUrl != null && pattern._chapterUrl.length() > 0);
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
		Assert.assertEquals("story state mismatch", expected._folderId, actual._folderId);
		Assert.assertEquals("media mismatch", expected._mediaId, actual._mediaId);
		Assert.assertEquals("title mismatch", expected._displayTitle, actual._displayTitle);
		Assert.assertEquals("saved place", expected._lastReadPlace, actual._lastReadPlace);
		Assert.assertEquals("last read url", expected._lastReadUrl, actual._lastReadUrl);
		if ( expected._lastReadDate != null && actual._lastReadDate != null ) {
			Assert.assertTrue("saved date", expected._lastReadDate.compareTo(actual._lastReadDate) == 0);
		}
		else {
			Assert.assertEquals("saved date", expected._lastReadDate, actual._lastReadDate);
		}
	}
	
	protected void checkEquals(Media expected, OnlineMediaItem actual) {
		Assert.assertEquals("is saved mismatch", expected._isSaved, actual._isSaved);
		Assert.assertEquals("story state mismatch", expected._folderId, actual._folderId);
		Assert.assertEquals("saved place", expected._lastReadPlace, actual._lastReadPlace);
		Assert.assertEquals("last read url", expected._chapterUrl, actual._lastReadUrl);
		if ( expected._lastReadDate != null && actual._lastReadDate != null ) {
			Assert.assertTrue("saved date", expected._lastReadDate.compareTo(actual._lastReadDate) == 0);
		}
		else {
			Assert.assertEquals("saved date", expected._lastReadDate, actual._lastReadDate);
		}
	}
}
