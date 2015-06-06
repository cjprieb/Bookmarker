package com.purplecat.bookmarker.test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.models.Genre;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.databases.GenreDatabaseRepository;
import com.purplecat.bookmarker.services.databases.MediaDatabaseRepository;
import com.purplecat.bookmarker.test.modules.TestDatabaseModule;
import com.purplecat.commons.tests.GetRandom;

public class GenreDatabaseRepositoryTests extends DatabaseConnectorTestBase {
	private static MediaDatabaseRepository _mediaDatabase;
	private static GenreDatabaseRepository _genreDatabase;
	private static List<Genre> _randomGenres;

	@BeforeClass
	public static void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new TestDatabaseModule());

		_mediaDatabase = injector.getInstance(MediaDatabaseRepository.class);
		_genreDatabase = injector.getInstance(GenreDatabaseRepository.class);
		//_database._log = new ConsoleLog();
		
		_randomGenres = _genreDatabase.query();
		Assert.assertNotNull("List is null", _randomGenres);
		Assert.assertTrue("List has no elements", _randomGenres.size() > 0);
	}

	@Test
	public void testQuery() {
		try {
			List<Genre> list = _genreDatabase.query();
			Assert.assertNotNull("List is null", list);
			Assert.assertTrue("List has no elements", list.size() > 0);

			System.out.println(list.size() + " genres found");
			Genre prevItem = null;
			for ( Genre item : list ) {
				System.out.println("  checking " + item);
				if ( prevItem != null ) {
					Assert.assertTrue("invalid sort order", prevItem._name.compareTo(item._name) < 0);
				}
				checkGenre(item);
				prevItem = item;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}
	
	@Test
	public void testQueryByMediaId() {
		try {
			long id = 12; //A thousand year's ninetails
			List<Genre> list = _genreDatabase.queryByMediaId(id);
			Assert.assertNotNull("List is null", list);
			Assert.assertTrue("List has no elements", list.size() > 0);
			Assert.assertTrue("should not have the same # of genres as master list", _randomGenres.size() > list.size());

			System.out.println(list.size() + " genres found");
			Genre prevItem = null;
			for ( Genre item : list ) {
				System.out.println("  checking " + item);
				if ( prevItem != null ) {
					Assert.assertTrue("invalid sort order", prevItem._name.compareTo(item._name) < 0);
				}
				//Assert.assertTrue("is marked 'not include'", item._include);
				checkGenre(item);
				prevItem = item;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}		
	}
	
	@Test
	public void testLoadAllMediaGenres() {
		try {
			Map<Long, Set<Genre>> list = _genreDatabase.loadAllMediaGenres();
			Assert.assertNotNull("List is null", list);
			Assert.assertTrue("List has no elements", list.size() > 0);

			Long[] idList = list.keySet().toArray(new Long[]{});
			for (int i = 0; i < 10; i++ ) {
				long id = idList[GetRandom.getInteger(0, idList.length-1)];
				Media media = _mediaDatabase.queryById(id);
				System.out.println("looking for media matching id " + id + " : "  + media);
				Assert.assertNotNull("media is null", media);
				Assert.assertEquals("genres size doesn't match", media._genres.size(), list.get(id).size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}		
	}
	
	@Test
	public void testUpdatingGenreList() {
		try {
			long id = 12; //A thousand year's ninetails			
			List<Genre> list = new LinkedList<Genre>();
			int max = GetRandom.getInteger(4, _randomGenres.size()-1);
			for ( int i = 0; i < max; i++ ) {
				list.add(_randomGenres.get(GetRandom.getInteger(0, _randomGenres.size()-1)));
			}
			list.add(list.get(0));//making sure the list has a duplicate to see if it is handled gracefully
			list.add(new Genre("Sample Name"));
			boolean bSuccess = _genreDatabase.updateGenreList(list, id);
			Assert.assertTrue("not successful", bSuccess);

			List<Genre> newList = _genreDatabase.queryByMediaId(id);
			Assert.assertNotNull("List is null", newList);
			for (Genre newGenre : newList) {
				boolean bFound = false;
				for ( Genre genre : list ) {
					if ( (genre._id == 0 && genre._name.equals(newGenre._name)) || genre._id == newGenre._id) {
						bFound = true; break;
					}
				}
				Assert.assertTrue("no match found for " + newGenre, bFound);
			}
			for (Genre genre : list ) {
				boolean bFound = false;
				for ( Genre newGenre : newList ) {
					if ( (genre._id == 0 && genre._name.equals(newGenre._name)) || genre._id == newGenre._id) {
						bFound = true; break;
					}
				}
				Assert.assertTrue("no match found for " + genre, bFound);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}		
	}
	
	@Test
	public void testFindGenreName() {
		try {
			Genre actual = GetRandom.getItem(_randomGenres);
			Genre genre = _genreDatabase.find(actual._name.toLowerCase());
			Assert.assertNotNull("Genre is null", genre);
			Assert.assertEquals("Genre has invalid id", actual._id, genre._id);
			Assert.assertEquals("Genre doesn't match name", actual._name, genre._name);
			
			genre = _genreDatabase.find("Computers");
			Assert.assertNotNull("Genre is null", genre);
			Assert.assertTrue("Genre has valid id", genre._id == 0);
			Assert.assertEquals("Genre doesn't match name", "Computers", genre._name);
			
			genre = _genreDatabase.find("webtoons");
			Assert.assertNotNull("Genre is null", genre);
			Assert.assertTrue("Genre has invalid id", genre._id > 0);
			Assert.assertEquals("Genre doesn't match name", "Webtoon", genre._name);
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}		
	}
	
	private void checkGenre(Genre item) {
		Assert.assertTrue("invalid id", item._id > 0);
		Assert.assertTrue("name is null", item._name != null && item._name.length() > 0);
		
		if ( item._name.equals("Ecchi" ) ){
			Assert.assertFalse("marked as included", item._include);
		}
		else if ( item._name.equals("Adventure") ) {
			Assert.assertTrue("marked as not included", item._include);
		}
		
		if ( item._name.equals("Webtoon") ) {
			Assert.assertEquals("alt name is wrong", "Webtoons", item._altName);
		}
	}
}
