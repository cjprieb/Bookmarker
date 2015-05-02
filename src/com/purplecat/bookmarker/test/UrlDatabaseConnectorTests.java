package com.purplecat.bookmarker.test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.models.UrlPattern;
import com.purplecat.bookmarker.services.databases.UrlPatternDatabase;
import com.purplecat.bookmarker.test.modules.TestDatabaseModule;
import com.purplecat.commons.tests.GetRandom;

public class UrlDatabaseConnectorTests extends DatabaseConnectorTests {
	
	private static UrlPatternDatabase _database;
	private static List<UrlPattern> _randomItems;

	@BeforeClass
	public static void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new TestDatabaseModule());		
		_database = injector.getInstance(UrlPatternDatabase.class);
		
		_randomItems = _database.query();
		Assert.assertNotNull("List is null", _randomItems);
		Assert.assertTrue("List has no elements", _randomItems.size() > 0);
	}

	@Test
	public void testQueryById() {
		try {
			UrlPattern pattern = GetRandom.getItem(_randomItems);
			List<UrlPattern> list = _database.query(pattern._id);
			Assert.assertNotNull("Query for id list is null", list);
			Assert.assertEquals("List doesn't have 1 element", 1, list.size());
			Assert.assertEquals("Element doesn't match id", pattern._id, list.get(0)._id);
			checkItem(list.get(0));
			checkEquals(pattern, list.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testInsert() {
		try {
			UrlPattern item = new UrlPattern();
			item._patternString = GetRandom.getString(6);
			item._map = new HashMap<String, Integer>();
			item._map.put("TITLE", 1);			
			_database.insert(item);
			
			Assert.assertTrue("Invalid id", item._id > 0);
			
			List<UrlPattern> list = _database.query(item._id);
			Assert.assertNotNull("List is null", list);
			Assert.assertEquals("List doesn't have 1 element", 1, list.size());
			checkItem(list.get(0));
			checkEquals(item, list.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}

	@Test
	public void testUpdate() {
		try {
			UrlPattern item = GetRandom.getItem(_randomItems);
			item._patternString = GetRandom.getString(6);
			item._map = new HashMap<String, Integer>();
			item._map.put("TITLE", 1);
			
			_database.update(item);
			System.out.println("looking up " + item._id + " after update");
			List<UrlPattern> list = _database.query(item._id);
			Assert.assertNotNull("List is null", list);
			Assert.assertEquals("List doesn't have 1 element: " + item._id, 1, list.size());
			checkItem(list.get(0));
			checkEquals(item, list.get(0));
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}


	@Test
	public void testRemove() {
		try {
			UrlPattern media = GetRandom.getItem(_randomItems);
			_database.delete(media._id);
			
			List<UrlPattern> list = _database.query(media._id);
			Assert.assertNotNull("List is null", list);
			Assert.assertTrue("List has elements", list.size() == 0);
			_randomItems.remove(media);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
	}
	
	protected void checkItem(UrlPattern pattern) {
		Assert.assertTrue("id is not valid", pattern._id > 0);
		Assert.assertNotNull("no pattern", pattern._pattern);
		Assert.assertTrue("no pattern string", pattern._patternString != null && pattern._patternString.length() > 0);
		Assert.assertNotNull("no map", pattern._map);
	}
	
	protected void checkEquals(UrlPattern expected, UrlPattern actual) {
		Assert.assertEquals("pattern mismatch", expected._patternString, actual._patternString);
		
		printMap("expected: ", expected._map);
		printMap("actual: ", actual._map);
		
		for ( String key : expected._map.keySet() ) {
			Assert.assertTrue("not found in expected: " + key, actual._map.containsKey(key) || key.equals("NONE"));
		}
		
		for ( String key : actual._map.keySet() ) {
			Assert.assertTrue("not found in actual: " + key, expected._map.containsKey(key) || key.equals("NONE"));
		}
	}
	
	protected void printMap(String title, Map<String, Integer> map) {
		System.out.print(title);
		for ( String key : map.keySet() ) {
			System.out.print(String.format("%s=%d; ", key, map.get(key)));
		}
		System.out.println();
	}

}
