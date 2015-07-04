package com.purplecat.bookmarker.extensions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.commons.IResourceService;
import com.purplecat.commons.extensions.Numbers;

public class PlaceExt {
	private static final int START	 	= 0;
	private static final int VOLUME 	= 1;
	private static final int CHAPTER 	= 2;
	private static final int SUBCHAPTER = 3;
	private static final int EXTRA 		= 4;
	private static final int PAGE 		= 5;

	private static final Pattern _bakaPlaceRegex = Pattern.compile("(?:v\\.(\\d+) )?c\\.(.+)");
	
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

	public static String render(Place place) {
		if ( place == null ) {
			throw new NullPointerException("Place is null");
		}
		StringBuilder builder = new StringBuilder();
		if ( place._volume > 0 ) {
			builder.append("v ").append(place._volume).append(" ");
		}
		builder.append("ch ").append(place._chapter);
		if ( place._subChapter > 0 ) {
			builder.append('.').append(place._subChapter);
		}
		if ( place._extra ) {
			builder.append('*');
		}
		return builder.toString();
	}
	
	public static String formatPlaceAndDate(IResourceService resources, Place place, DateTime date) {
		if ( place == null ) {
			return "";
		}
		if ( date != null ) {
			return String.format(resources.getString(Resources.string.htmlPlaceAndDate), 
					render(place), 
					date.toString(DateTimeFormat.mediumDate()));
		}
		else {
			return render(place);
		}		
	}

	public static Place parseBakaPlace(String text) {
		Place place = new Place();
		Matcher matcher = _bakaPlaceRegex.matcher(text);
		if ( matcher.matches() ) {
			if ( matcher.group(1) != null ) {
				place._volume = Numbers.parseInt(matcher.group(1), 0);
			}
			if ( matcher.group(2) != null ) {
				String sChapterText = matcher.group(2).toLowerCase();
				String sChapter = sChapterText;
				//68
				//69-71
				//Extra (end)
				//13.5
				//1v2 +2-3
				//Oneshot
				//Epilogue
				//15 (end)
				//10a
//				int charIndex = sChapter.indexOf('-');
//				if ( charIndex >= 0 ) {
//					sChapter = sChapter.substring(charIndex+1);
//				}	
//				
//				charIndex = sChapter.indexOf('v');
//				if ( charIndex >= 0 ) {
//					sChapter = sChapter.substring(0, charIndex);
//				}
				
				StringBuilder chapterBuilder = new StringBuilder();
				StringBuilder subChapterBuilder = new StringBuilder();
				boolean addToChapter = true;
				boolean addToSubChapter = false;
				boolean resetChapter = false;
				for ( char c : sChapter.toCharArray() ) {
					if ( Character.isDigit(c) ) {
						if ( addToSubChapter ) {
							subChapterBuilder.append(c);						
						}
						else if ( addToChapter ) {
							if ( resetChapter ) {
								chapterBuilder.setLength(0);
								resetChapter = false;
							}
							chapterBuilder.append(c);
						}
					}
					else if ( c == '.' ) {
						addToSubChapter = true;
					}
					else if ( c == '-' ) {
						addToSubChapter = false;
						addToChapter = true;
						resetChapter = true;
					}
					else if ( chapterBuilder.length() > 0 && Character.isLetter(c) && c != 'v' && !resetChapter ) {
						//'v' stands for volume
						addToChapter = false;
						subChapterBuilder.append((c-'a'+1));
					}
					else if ( c == ' ' ) {
						addToChapter = true;
						resetChapter = true;
					}
					else {
						addToSubChapter = false;
						addToChapter = false;
					}
				}

//				String sSubChapter = "";
//				charIndex = sChapter.indexOf('.');
//				if ( charIndex >= 0 ) {
//					sSubChapter = sChapter.substring(charIndex+1);
//					sChapter = sChapter.substring(0, charIndex);
//				}

				if ( sChapterText.contains("epilogue") || sChapterText.contains("extra") ) {
					place._extra = true;
				}
				
				place._chapter = Numbers.parseInt(chapterBuilder.toString(), 0);
				place._subChapter = Numbers.parseInt(subChapterBuilder.toString(), 0);
				
			}
		}
		return place;
	}
}
