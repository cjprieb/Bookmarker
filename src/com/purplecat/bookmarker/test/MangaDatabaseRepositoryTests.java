package com.purplecat.bookmarker.test;

import java.util.List;

import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.models.EFavoriteState;
import com.purplecat.bookmarker.models.EStoryState;
import com.purplecat.bookmarker.models.Genre;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.services.databases.DatabaseException;
import com.purplecat.bookmarker.services.databases.GenreDatabaseRepository;
import com.purplecat.bookmarker.services.databases.MediaDatabaseRepository;
import com.purplecat.bookmarker.sql.ConnectionManager;
import com.purplecat.bookmarker.sql.IConnectionManager;
import com.purplecat.bookmarker.test.modules.TestDatabaseModule;
import com.purplecat.commons.extensions.DateTimeFormats;
import com.purplecat.commons.tests.GetRandom;
import com.purplecat.commons.utils.StringUtils;

public class MangaDatabaseRepositoryTests extends DatabaseConnectorTestBase {
	private static IConnectionManager _connectionManager;
	private static MediaDatabaseRepository _database;
	private static GenreDatabaseRepository _genreDatabase;
	private static List<Media> _randomSavedMedia;
	private static String[] _sampleTitles = { "360° material", "chihayafuru", "7 centi!", "gozen 3-ji no muhouchitai", "chikutaku bonbon", 
		"d. n. angel", "yume no shizuku ougon no torikago", "birdmen", "adventure of sinbad", "twelve nights" };
	
	private static long[] _sampleAltTitleIds = { 5, 6, 7, 9, 10, 11, 26, 28, 31, 46, 54, 60, 67 };

	@BeforeClass
	public static void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new TestDatabaseModule());
		
		_database = injector.getInstance(MediaDatabaseRepository.class);
		_genreDatabase = injector.getInstance(GenreDatabaseRepository.class);
		_connectionManager = injector.getInstance(ConnectionManager.class);
		//_database._log = new ConsoleLog();
		
		try {
			_connectionManager.open();
			_randomSavedMedia = _database.querySavedMedia(new TestMediaObserver());
			Assert.assertNotNull("List is null", _randomSavedMedia);
			Assert.assertTrue("List has no elements", _randomSavedMedia.size() > 0);
			Assert.assertTrue("item not marked as saved: ", _randomSavedMedia.get(0)._isSaved);
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
	public void testQuery() {
		try {
			List<Media> list = _database.query();
			Assert.assertNotNull("List is null", list);
			Assert.assertTrue("List has no elements", list.size() > 0);
			checkMediaItem(list.get(0));
			Assert.assertTrue("no genres", list.get(0)._genres.size() > 0);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testQueryForSaved() {
		try {
			TestMediaObserver obs = new TestMediaObserver();
			List<Media> list = _database.querySavedMedia(obs);
			Assert.assertNotNull("List is null", list);
			Assert.assertTrue("List has no elements", list.size() > 0);
			Assert.assertTrue("total not found", obs.iTotal > 0);
			Assert.assertEquals("total doesn't equal last index", obs.iTotal, obs.iIndex);
			Assert.assertTrue("total was not valid", obs.bValidTotal);
			Media media = GetRandom.getItem(list);
			checkSavedMediaItem(media);
			Assert.assertTrue("no genres", media._genres.size() > 0);
			
			boolean hasUpdatedItem = false;
			for ( Media item : list ) {
				if ( item.isUpdated() ) {
					hasUpdatedItem = true;
				}
			}
			Assert.assertTrue("no updated item", hasUpdatedItem);
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
			Assert.assertTrue("no genres", media._genres.size() > 0);
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
			media.setDisplayTitle(GetRandom.getString(6));
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
			media.setDisplayTitle(GetRandom.getString(6));
			media._lastReadPlace._chapter++;
			media._chapterUrl = "http://sampleurl";
			media._titleUrl = "http://www.sampleurl.com/thetitle";
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
	public void testInsertFind() {
		try {
			Media media = new Media();
			media.setDisplayTitle(GetRandom.getString(6));
			media._lastReadPlace._chapter++;
			media._chapterUrl = "http://sampleurl";
			media._titleUrl = "http://www.sampleurl.com/thetitle";
			media._storyState = EStoryState.NEW_BOOKMARK;
			media._notes = "Some notes!";
			media._rating = EFavoriteState.GOOD;
			media._lastReadDate = new DateTime();
			media._isSaved = true;
			_database.insert(media);
			
			Assert.assertTrue("Invalid id", media._id > 0);	
			System.out.println("Media added: "  + media._id);
			
			List<Media> foundMedia = _database.queryByTitle(media.getDisplayTitle());
			Assert.assertNotNull("list is null", foundMedia);
			Assert.assertTrue("list has no items", foundMedia.size() > 0);
			Assert.assertEquals("wrong id", media._id, foundMedia.get(0)._id);
			Assert.assertEquals("wrong title", media.getDisplayTitle(), foundMedia.get(0).getDisplayTitle());
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testUpdate() {
		try {
			Media media = GetRandom.getItem(_randomSavedMedia);
			
			media.setDisplayTitle(GetRandom.getString(6));
			media._lastReadPlace._chapter++;
			media._chapterUrl = "http://sampleurl";
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
			Assert.assertTrue("no genres", media._genres.size() > 0);
			checkEquals(media, item);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testUpdateFind() {
		try {
			Media media = GetRandom.getItem(_randomSavedMedia);
			
			String oldTitle = media.getDisplayTitle();

			media.setDisplayTitle(GetRandom.getString(6));
			media._lastReadPlace._chapter++;
			media._chapterUrl = "http://sampleurl";
			media._titleUrl = "http://www.sampleurl.com/thetitle";
			media._storyState = EStoryState.NEW_BOOKMARK;
			media._notes = "Some notes!";
			media._rating = EFavoriteState.GOOD;
			media._lastReadDate = new DateTime();
			media._isSaved = true;
			_database.update(media);
			
			Assert.assertTrue("Invalid id", media._id > 0);	
			System.out.println("Media added: "  + media._id);
			
			List<Media> foundMedia = _database.queryByTitle(media.getDisplayTitle());
			Assert.assertNotNull("list is null", foundMedia);
			Assert.assertTrue("list has no items", foundMedia.size() > 0);
			Assert.assertEquals("wrong id", media._id, foundMedia.get(0)._id);
			Assert.assertEquals("wrong title", media.getDisplayTitle(), foundMedia.get(0).getDisplayTitle());

			foundMedia = _database.queryByTitle(oldTitle);
			Assert.assertNotNull("list is null", foundMedia);
			Assert.assertEquals("list has items", 1, foundMedia.size()); //will be one as we didn't remove the old title
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testUpdateNonSaved() {
		try {
			Media media = _database.queryById(23660);//known unsaved item
			
			media.setDisplayTitle(GetRandom.getString(6));
			media._lastReadPlace._chapter++;
			media._chapterUrl = "http://sampleurl";
			media._lastReadDate = new DateTime();
			media._isSaved = true;
			
			_database.update(media);
			System.out.println("looking up " + media._id + " after update");
			
			Media item = _database.queryById(media._id);
			Assert.assertNotNull("item is null", item);
			checkSavedMediaItem(item);
			Assert.assertTrue("no genres", media._genres.size() > 0);
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
			
			List<Genre> genres = _genreDatabase.queryByMediaId(media._id);
			Assert.assertEquals("genres not removed", 0, genres.size());
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
				Assert.assertEquals("No elements matching " + title, 1, list.size());
				System.out.println("  match found: "  + list.get(0).getDisplayTitle());
				
				boolean bTitleFound = list.get(0).getDisplayTitle().equalsIgnoreCase(title);
				for ( String altTitle : list.get(0)._altTitles ) {
					if ( altTitle.equalsIgnoreCase(title) ) {
						bTitleFound = true;
						break;
					}
				}
				Assert.assertTrue("no title found in media", bTitleFound);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testLoadItemWithMultipleTitles() {
		try {
			long id = _sampleAltTitleIds[GetRandom.getInteger(0, _sampleAltTitleIds.length-1)];
			Media media = _database.queryById(id);
			Assert.assertNotNull("media is null", media);
			Assert.assertTrue("no alt titles", media._altTitles.size() > 0);
			checkSavedMediaItem(media);			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}
	
	protected void checkMediaItem(Media media) {
		Assert.assertTrue("id is not valid", media._id > 0);
		Assert.assertTrue("no title", !StringUtils.isNullOrEmpty(media.getDisplayTitle()));
	}
	
	protected void checkSavedMediaItem(Media media) {
		System.out.println("checking saved media item: " + media);
		Assert.assertTrue("id is not valid", media._id > 0);
		Assert.assertTrue("no title", !StringUtils.isNullOrEmpty(media.getDisplayTitle()));
		Assert.assertTrue("not saved", media._isSaved);
		Assert.assertNotNull("no last read date", media._lastReadDate);		
		Assert.assertTrue("invalid last read date: " + media._lastReadDate.toString(DateTimeFormats.SQLITE_DATE_FORMAT), media._lastReadDate.isAfter(new DateTime(2000, 1, 1, 0, 0)));
		Assert.assertNotNull("no place", media._lastReadPlace);
		Assert.assertNotNull("no chapter url", media._chapterUrl);
		Assert.assertNotNull("no title url", media._titleUrl);
	}
	
	protected void checkEquals(Media expected, Media actual) {
		Assert.assertEquals("id mismatch", expected._id, actual._id);
		Assert.assertEquals("title mismatch", expected.getDisplayTitle(), actual.getDisplayTitle());
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
		Assert.assertEquals("title url mismatch", expected._titleUrl, actual._titleUrl);
		Assert.assertEquals("chapter url mismatch", expected._chapterUrl, actual._chapterUrl);
	}
	
	public static class TestMediaObserver implements IListLoadedObserver<Media> {
		int iIndex;
		int iTotal;
		boolean bValidTotal;

		@Override
		public void notifyItemLoaded(Media item, int index, int total) {
			iIndex = index;
			iTotal = total;
			bValidTotal = iTotal >= iIndex && iTotal > 0;
		}

		@Override
		public void notifyListLoaded(List<Media> list) {
			// TODO Auto-generated method stub
			
		}
		
	}
}
