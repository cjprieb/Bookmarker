package com.purplecat.bookmarker.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.test.dummies.SampleMangaDatabase;
import com.purplecat.bookmarker.test.modules.TestBookmarkerModule;

public class SortingTest {
	
	private SampleMangaDatabase _database;

	@Before
	public void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new TestBookmarkerModule());
		
		_database = injector.getInstance(SampleMangaDatabase.class);
	}

	@Test
	public void sortSavedMedia() {
		List<Media> list = new ArrayList<Media>();
		for ( Media media : _database.query() ) { list.add(media); }
		assertTrue(list.size() >= 5);
		
		int expected = list.get(0).compareTo(list.get(1));
		assertFalse(expected == 0);
		assertEquals(-expected, list.get(1).compareTo(list.get(0)));
		
		expected = list.get(1).compareTo(list.get(2));
		assertFalse(expected == 0);
		assertEquals(-expected, list.get(2).compareTo(list.get(1)));
		
		Collections.sort(list);
		
		System.out.println("Sorting saved media");
		for ( Media media : list ) {
			System.out.println(String.format("  %s - %s - Read: %s - Updated Place: %s", media, media.isUpdated(), 
					media._updatedDate != null ? media._updatedDate.toString("MMM dd, yyyy") : "",
					media._updatedPlace != null ? media._updatedPlace : ""));
		}

		System.out.println("Testing sort");
		for ( int i = 0; i < list.size(); i++ ) {
			System.out.println(String.format("%d. %s", i, list.get(i)));
			assertEquals("Ids not equal at " + (i+1), (long)_database._preferredOrder.get(i+1), list.get(i)._id);
		}
	}

}
