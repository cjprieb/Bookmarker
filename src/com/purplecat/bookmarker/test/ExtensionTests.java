package com.purplecat.bookmarker.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

import com.purplecat.bookmarker.extensions.PlaceExt;
import com.purplecat.bookmarker.models.Place;

public class ExtensionTests {
	
	List<PlacePair> _placeTests;	
	
	@Before
	public void setupTestStrings() {
		_placeTests = new ArrayList<PlacePair>(100);
		_placeTests.add(new PlacePair("v12c64p48", 12, 64, 0, 48, false));
		_placeTests.add(new PlacePair("v2c8p54", 2, 8, 0, 54, false));
		_placeTests.add(new PlacePair("v2c11p35", 2, 11, 0, 35, false));
		_placeTests.add(new PlacePair("c21", 0, 21, 0, 0, false));
		_placeTests.add(new PlacePair("c0", 0, 0, 0, 0, false));
		_placeTests.add(new PlacePair("v21c0", 21, 0, 0, 0, false));
		_placeTests.add(new PlacePair("c7p35", 0, 7, 0, 35, false));
		_placeTests.add(new PlacePair("v2c7p1", 2, 7, 0, 1, false));
		_placeTests.add(new PlacePair("v12c18.17p14", 12, 18, 17, 14, false));
		_placeTests.add(new PlacePair("v5c3.5p1", 5, 3, 5, 1, false));
		_placeTests.add(new PlacePair("v10c14.5*", 10, 14, 5, 0, true));
		_placeTests.add(new PlacePair("v10c42*p53", 10, 42, 0, 53, true));
		_placeTests.add(new PlacePair("v3c14*p30", 3, 14, 0, 30, true));
	}
		
	@Test
	public void placeParseTests() {
		for ( PlacePair pair : _placeTests ) {
			Place actual = PlaceExt.parse(pair._value);
			Assert.assertEquals(pair._place, actual);
		}
	}

	@Test
	public void placeFormatTests() {
		for ( PlacePair pair : _placeTests ) {
			String actual = PlaceExt.format(pair._place);
			Assert.assertEquals(pair._value, actual);
		}		
	}
	
	private static class PlacePair {
		Place _place;
		String _value;
		
		public PlacePair(String value, int v, int c, int sub, int page, boolean extra) {
			_value = value;
			_place = new Place();
			_place._volume = v;
			_place._chapter = c;
			_place._subChapter = sub;
			_place._page = page;
			_place._extra = extra;
		}
	}

}
