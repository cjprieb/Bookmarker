package com.purplecat.bookmarker.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.Before;
import org.junit.Assert;

import com.purplecat.bookmarker.extensions.PlaceExt;
import com.purplecat.bookmarker.models.Place;

public class ExtensionTests {
	
	List<PlacePair> _placeParseTests;
	List<PlacePair> _placeRenderTests;
	List<PlaceComparePair> _placeCompareTests;
	
	@Before
	public void setupTestStrings() {
		_placeParseTests = new ArrayList<PlacePair>(100);
		_placeParseTests.add(new PlacePair("v12c64p48", 12, 64, 0, 48, false));
		_placeParseTests.add(new PlacePair("v2c8p54", 2, 8, 0, 54, false));
		_placeParseTests.add(new PlacePair("v2c11p35", 2, 11, 0, 35, false));
		_placeParseTests.add(new PlacePair("c21", 0, 21, 0, 0, false));
		_placeParseTests.add(new PlacePair("c0", 0, 0, 0, 0, false));
		_placeParseTests.add(new PlacePair("v21c0", 21, 0, 0, 0, false));
		_placeParseTests.add(new PlacePair("c7p35", 0, 7, 0, 35, false));
		_placeParseTests.add(new PlacePair("v2c7p1", 2, 7, 0, 1, false));
		_placeParseTests.add(new PlacePair("v12c18.17p14", 12, 18, 17, 14, false));
		_placeParseTests.add(new PlacePair("v5c3.5p1", 5, 3, 5, 1, false));
		_placeParseTests.add(new PlacePair("v10c14.5*", 10, 14, 5, 0, true));
		_placeParseTests.add(new PlacePair("v10c42*p53", 10, 42, 0, 53, true));
		_placeParseTests.add(new PlacePair("v3c14*p30", 3, 14, 0, 30, true));

		_placeCompareTests = new ArrayList<PlaceComparePair>(100);
		_placeCompareTests.add(new PlaceComparePair(new Place(), new Place(), 0));
		_placeCompareTests.add(new PlaceComparePair(new Place(12, 64, 0, 48, false), new Place(12, 64, 0, 48, false), 0));
		_placeCompareTests.add(new PlaceComparePair(new Place(12, 64, 0, 48, false), new Place(0, 64, 0, 48, false), 0));
		_placeCompareTests.add(new PlaceComparePair(new Place(12, 64, 0, 48, false), new Place(6, 64, 0, 48, false), 1));
		_placeCompareTests.add(new PlaceComparePair(new Place(12, 64, 0, 48, false), new Place(12, 70, 0, 48, false), -1));
		_placeCompareTests.add(new PlaceComparePair(new Place(12, 64, 1, 48, false), new Place(12, 64, 0, 48, false), 1));
		_placeCompareTests.add(new PlaceComparePair(new Place(12, 64, 1, 48, false), new Place(12, 64, 3, 48, false), -1));
		_placeCompareTests.add(new PlaceComparePair(new Place(12, 64, 1, 48, true), new Place(12, 64, 1, 48, false), 1));
		_placeCompareTests.add(new PlaceComparePair(new Place(2, 0, 0, 0, false), new Place(), 1));
		
		_placeRenderTests = new ArrayList<PlacePair>(100);
		_placeRenderTests.add(new PlacePair("v 12 ch 64", 12, 64, 0, 48, false));
		_placeRenderTests.add(new PlacePair("v 2 ch 8", 2, 8, 0, 54, false));
		_placeRenderTests.add(new PlacePair("v 2 ch 11", 2, 11, 0, 35, false));
		_placeRenderTests.add(new PlacePair("ch 21", 0, 21, 0, 0, false));
		_placeRenderTests.add(new PlacePair("ch 0", 0, 0, 0, 0, false));
		_placeRenderTests.add(new PlacePair("v 21 ch 0", 21, 0, 0, 0, false));
		_placeRenderTests.add(new PlacePair("ch 7", 0, 7, 0, 35, false));
		_placeRenderTests.add(new PlacePair("v 2 ch 7", 2, 7, 0, 1, false));
		_placeRenderTests.add(new PlacePair("v 12 ch 18.17", 12, 18, 17, 14, false));
		_placeRenderTests.add(new PlacePair("v 5 ch 3.5", 5, 3, 5, 1, false));
		_placeRenderTests.add(new PlacePair("v 10 ch 14.5*", 10, 14, 5, 0, true));
		_placeRenderTests.add(new PlacePair("v 10 ch 42*", 10, 42, 0, 53, true));
		_placeRenderTests.add(new PlacePair("v 3 ch 14*", 3, 14, 0, 30, true));
	}
		
	@Test
	public void placeParseTests() {
		for ( PlacePair pair : _placeParseTests ) {
			Place actual = PlaceExt.parse(pair._value);
			Assert.assertEquals(pair._place, actual);
		}
	}

	@Test
	public void placeFormatTests() {
		for ( PlacePair pair : _placeParseTests ) {
			String actual = PlaceExt.format(pair._place);
			Assert.assertEquals(pair._value, actual);
		}		
	}
	
	@Test
	public void placeRenderTests() {
		for ( PlacePair pair : _placeRenderTests ) {
			String actual = PlaceExt.render(pair._place);
			Assert.assertEquals(pair._value, actual);
		}
	}

	@Test
	public void placeCompareTests() {
		for ( PlaceComparePair pair : _placeCompareTests ) {
			System.out.println("Compareing: " + pair._place1 + " to " + pair._place2);
			Assert.assertEquals(pair._place1.compareTo(pair._place2), pair._expectedResult);
		}		
	}
	
	private static class PlacePair {
		Place _place;
		String _value;
		
		public PlacePair(String value, int v, int c, int sub, int page, boolean extra) {
			_value = value;
			_place = new Place(v, c, sub, page, extra);
		}
	}
	
	private static class PlaceComparePair {
		Place _place1;
		Place _place2;
		int _expectedResult;
		
		public PlaceComparePair(Place place1, Place place2, int result) {
			_place1 = place1;
			_place2 = place2;
			_expectedResult = result;
		}
	}

}
