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

public class UrlDatabaseRepositoryTests extends DatabaseConnectorTestBase {
	
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
			UrlPattern item = _database.queryById(pattern._id);
			Assert.assertNotNull("item is null", item);
			Assert.assertEquals("Element doesn't match id", pattern._id, item._id);
			checkItem(item);
			checkEquals(pattern, item);
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

			UrlPattern foundItem = _database.queryById(item._id);
			Assert.assertNotNull("item is null", item);
			checkItem(foundItem);
			checkEquals(item, foundItem);
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

			UrlPattern foundItem = _database.queryById(item._id);
			Assert.assertNotNull("item is null", item);
			checkItem(foundItem);
			checkEquals(item, foundItem);
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

			UrlPattern foundItem = _database.queryById(media._id);
			Assert.assertNull("item is not null", foundItem);
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
