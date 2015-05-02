package com.purplecat.bookmarker.extensions;

import com.purplecat.bookmarker.models.Place;
import com.purplecat.commons.extensions.Numbers;

public class PlaceExt {
	private static final int START	 	= 0;
	private static final int VOLUME 	= 1;
	private static final int CHAPTER 	= 2;
	private static final int SUBCHAPTER = 3;
	private static final int EXTRA 		= 4;
	private static final int PAGE 		= 5;
	
	public static Place parse(String str) {
		Place place = new Place();
		int length = str != null ? str.length() : 0;
		if ( length < 2 ) {
			return place; //don't even bother parsing; it should at least have 'c0'
		}
		
		int state = START;
		int iStart = 0;
		for ( int i = 0; i <= length; i++ ) {
			char c = i < length ? str.charAt(i) : 0;
			switch ( state ) {
			case START: 
				if ( c == 'v' || c == 'c' ) { 
					iStart = i+1; 
					state = (c == 'v' ? VOLUME : CHAPTER);
				}
				break;
				
			case VOLUME:
				if ( c == 'c' ) { 
					place._volume = Numbers.parseInt(str.substring(iStart, i), 0);
					iStart = i+1; 
					state = CHAPTER;
				}
				break;
				
			case CHAPTER:
				if ( i == length || c == '.' || c == '*' || c == 'p' ) { 
					place._chapter = Numbers.parseInt(str.substring(iStart, i), 0);
					iStart = i+1; 
					if ( c == '.' ) {
						state = SUBCHAPTER;
					}
					else if ( c == '*' ) {
						state = EXTRA;
					}
					else if ( c == 'p' ) {
						state = PAGE;
						break;
					}
				}
				break;
				
			case SUBCHAPTER:
				if ( i == length || c == '*' || c == 'p' ) { 
					place._subChapter = Numbers.parseInt(str.substring(iStart, i), 0);
					iStart = i+1; 
					if ( c == '*' ) {
						state = EXTRA;
					}
					else if ( c == 'p' ){
						state = PAGE;
					}
				}
				break;
				
			case EXTRA:
				place._extra = true;
				if ( i == length || c == 'p' ) {
					iStart = i+1; 
					if ( c == 'p' ) {
						state = PAGE;
					}
				}
				break;
				
			case PAGE:
				if ( i == length ) {
					place._page = Numbers.parseInt(str.substring(iStart, i), 0);
				}
				break;
				
				default:
			}
		}
		return place;
	}
	
	public static String format(Place place) {
		if ( place == null ) {
			throw new NullPointerException("Place is null");
		}
		StringBuilder builder = new StringBuilder();
		if ( place._volume > 0 ) {
			builder.append('v').append(place._volume);
		}
		builder.append('c').append(place._chapter);
		if ( place._subChapter > 0 ) {
			builder.append('.').append(place._subChapter);
		}
		if ( place._extra ) {
			builder.append('*');
		}
		if ( place._page > 0 ) {
			builder.append('p').append(place._page);
		}
		return builder.toString();
	}
}
