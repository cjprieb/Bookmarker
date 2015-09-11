package com.purplecat.bookmarker;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.services.databases.DatabaseException;
import com.purplecat.bookmarker.services.databases.TitleDatabaseRepository;
import com.purplecat.bookmarker.sql.ConnectionManager;
import com.purplecat.bookmarker.sql.IConnectionManager;
import com.purplecat.bookmarker.modules.TestDatabaseModule;
import com.purplecat.commons.tests.GetRandom;

public class TitleDatabaseRepositoryTests extends DatabaseConnectorTestBase {
	private static IConnectionManager _connectionManager;
	private static TitleDatabaseRepository _titleDatabase;

	@BeforeClass
	public static void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new TestDatabaseModule());

		_titleDatabase = injector.getInstance(TitleDatabaseRepository.class);
		_connectionManager = injector.getInstance(ConnectionManager.class);
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
	public void testQueryByMediaId() {
		try {
			long id = 11; //A way to make a gentle world
			List<String> list = _titleDatabase.queryByMediaId(id);
			Assert.assertNotNull("List is null", list);
			Assert.assertTrue("List has no elements", list.size() > 1);

			System.out.println(list.size() + " titles found");
			for ( String item : list ) {
				System.out.println("  checking " + item);
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}		
	}
	
	@Test
	public void loadAllMediaTitles() {
		try {
			Map<Long, Set<String>> list = _titleDatabase.loadAllMediaTitles();
			Assert.assertNotNull("List is null", list);
			Assert.assertTrue("List has no elements", list.size() > 0);

			Long[] idList = list.keySet().toArray(new Long[]{});
			for (int i = 0; i < 10; i++ ) {
				long id = idList[GetRandom.getInteger(0, idList.length-1)];
				List<String> titles = _titleDatabase.queryByMediaId(id);
				Assert.assertNotNull("title list is null", titles);
				Assert.assertEquals("title size doesn't match", titles.size(), list.get(id).size());
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}		
	}
	
	@Test
	public void testUpdatingTitleList() {
		try {
			long id = 12; //A thousand year's ninetails			
			List<String> list = new LinkedList<String>();
			int max = GetRandom.getInteger(2, 4);
			for ( int i = 0; i < max; i++ ) {
				list.add(GetRandom.getString(10));
			}
			boolean bSuccess = _titleDatabase.updateTitleList(list, id);
			Assert.assertTrue("not successful", bSuccess);

			List<String> newList = _titleDatabase.queryByMediaId(id);
			Assert.assertNotNull("List is null", newList);
			for (String newTitle: newList) {
				Assert.assertTrue("no match found for " + newTitle, list.stream().anyMatch(item -> item.equals(newTitle)));
			}
			for (String title : list ) {
				Assert.assertTrue("no match found for " + title, newList.stream().anyMatch(item -> item.equals(title)));
			}
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}		
	}
}
