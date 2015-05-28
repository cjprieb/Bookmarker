package com.purplecat.commons.utils;

import java.util.Locale;
import java.util.Map;

public class StringUtils {	
	/**
	 * Used by matchStrings<br> 
	 * REGULAR search: uses java's string match function<br>
	 * REGULAR_TOLOWER: uses java's string match function, after converting both lookInThis and searchForThis to lowercase.<br>
	 * SIMPLE: uses java's string contains function, after converting both strings to lowercase.
	 * @author cprieb
	 *
	 */
	public enum SearchType {
		REGULAR,
		REGULAR_TOLOWER,
		SIMPLE_TOLOWER,
		SIMPLE
	}
	
	/**
	 * returns TRUE if searchForThis was found within lookInThis, based on the search type.<br>
	 * REGULAR search: uses java's string match function<br>
	 * REGULAR_TOLOWER: uses java's string match function, after converting both lookInThis and searchForThis to lowercase.<br>
	 * SIMPLE: uses java's string contains function, after converting both strings to lowercase.
	 * @param type	type of search to use (SearchType REGULAR or SIMPLE)
	 * @param lookInThis string to search within
	 * @param searchForThis string to search for
	 * @param validIfEmpty returns TRUE if searchForThis is empty
	 * @return 
	 */
	public static boolean matchStrings(SearchType type, String lookInThis, String searchForThis, boolean validIfEmpty) {
		boolean matches = false;
		if ( lookInThis == null || searchForThis == null ) {
			matches = false;
		}
		else if ( searchForThis.length() == 0 ) {
			matches = validIfEmpty;
		}
		else {
			switch ( type ) {
				case SIMPLE:
					matches = StringUtils.equals(lookInThis, searchForThis);
					break;
				case SIMPLE_TOLOWER:
					matches = lookInThis.toLowerCase().contains(searchForThis.toLowerCase());
					break;
				case REGULAR:
					matches = lookInThis.matches(searchForThis);
					break;
				case REGULAR_TOLOWER:
					matches = lookInThis.toLowerCase().matches(searchForThis.toLowerCase());
					break;
				default:	
					matches = false;	
					break;
			}
		}
		return(matches);
	}
	
	public static StringBuffer getWrappedBuffer(String src, int wrapAtLength, String tabs, boolean includeInFirstLine) {
		StringBuffer s = new StringBuffer("");

		int	length	= src.length();
		int	i_start = 0;
		int	i_end	= (length < wrapAtLength) ? length : wrapAtLength;
		
		while ( i_start <= i_end && i_start >= 0 && i_end <= length ) {
			String hyphen 		= "";
			boolean stripChar 	= false;
			if ( i_end != length ) {
//				System.out.println("1 from " + i_start + " to " + i_end + " (length: " + length + ")");
				int lastSpace 	= src.lastIndexOf(' ',  i_end);
				int lastTab		= src.lastIndexOf('\t', i_end);
				int lastBreak	= src.lastIndexOf('\n', i_end);
				int lastHyphen	= src.lastIndexOf('-',  i_end);
//				System.out.println("indices: (' ', " + lastSpace + ") ('\\t', " + lastTab + ") ('\\n', " + lastBreak + ") ('-', " + lastHyphen + ") ");
				if ( lastBreak > -1 && lastBreak > i_start ) {
					i_end = lastBreak;
					stripChar = true;
				}
				else {
					int temp_end = largestIndex(lastSpace, lastTab, lastHyphen);
					i_end = (temp_end > i_start) ? temp_end : -1;
					if ( i_end == -1 ) {
						i_end = i_start + wrapAtLength - 1;
						hyphen = "-";
					}
					else {
						if ( i_end == lastHyphen ) {
							hyphen = "-";							
						}						
						stripChar = true;				
					}
				}
			}
			if ( i_start <= i_end && i_start < length ) {
//				System.out.println(src.substring(i_start, i_end));
				if ( includeInFirstLine || s.length() > 0 ) {
					s.append(tabs);
				}
				s.append(src.substring(i_start, i_end)).append(hyphen).append("\n");
			}
			else {
				break;
			}
			i_start = i_end + (stripChar ? 1 : 0);
			i_end = (length < wrapAtLength+i_start) ? length : wrapAtLength+i_start;
//			System.out.println("i_end: (" + length + " < " + wrapAtLength + "+" + i_start + ")");
		}
		
		if ( s.length() > 0 && s.charAt(s.length()-1) == '\n' ) {
			s.deleteCharAt(s.length()-1);
		}
		
		return(s);
	}
	
	private static int largestIndex(int ... indices) { 
		int largest = -1;
		for ( int i : indices )		if ( i > largest )		largest = i;
		return(largest);
	}
	
	public static boolean isNullOrEmpty(Object s) {
		return(s == null || s.toString().length() == 0);
	}
	
	public static boolean isNullOrEmpty(String s) {
		return(s == null || s.length() == 0);
	}
	
	public static boolean equals(String s1, String s2) {
		boolean e = (s1 == s2);
		if ( !e && (s1 == null || s2 == null) ) {
			e = false;
		}
		else {
			e = s1.equals(s2);
		}
		return(e);
	}
	
	public static String getFirstDigitString(int iStartIndex, String s) {
		String digits = "";
		for ( int i = iStartIndex; i < s.length(); i++ ) {
			if ( Character.isDigit(s.charAt(i)) ) {
				digits += s.charAt(i);
			}
			else if ( digits.length() > 0 ) {
				break;
			}			
		}
		return(digits);
	}
	
	/*
	 * String Formats:
	 */	
	public static String format(com.purplecat.commons.Point p) {
		if ( p != null ) {
			return(String.format("(%d, %d)", p.x, p.y));
		}
		else {
			return("(null point)");
		}		
	}
	
	public static String formatPoint(int x, int y) {
		return(String.format("(%d, %d)", x, y));
	}
	
	public static String formatDimension(int width, int height) {
		return(String.format("(%d x %d)", width, height));
	}

	public static <T> StringBuilder format(T[] array) {
		StringBuilder s = new StringBuilder();
		if ( array != null ) {
			s.append('[');
			for ( int i = 0; i < array.length; i++) {
				if ( i > 0 ) s.append(", ");
				s.append(array[i]);
			}
			s.append(']');
		}
		else {
			s.append("<NULL>");
		}
		return(s);
	}

	public static StringBuilder format(long[] array) {
		StringBuilder s = new StringBuilder();
		if ( array != null ) {
			s.append('[');
			for ( int i = 0; i < array.length; i++) {
				if ( i > 0 ) s.append(", ");
				s.append(array[i]);
			}
			s.append(']');
		}
		else {
			s.append("<NULL>");
		}
		return(s);
	}

	public static StringBuilder format(int[] array) {
		StringBuilder s = new StringBuilder();
		if ( array != null ) {
			s.append('[');
			for ( int i = 0; i < array.length; i++) {
				if ( i > 0 ) s.append(", ");
				s.append(array[i]);
			}
			s.append(']');
		}
		else {
			s.append("<NULL>");
		}
		return(s);
	}

	public static String format(boolean[] array) {
		StringBuilder s = new StringBuilder();
		if ( array != null ) {
			s.append('[');
			for ( int i = 0; i < array.length; i++) {
				if ( i > 0 ) s.append(", ");
				s.append(array[i]);
			}
			s.append(']');
		}
		else {
			s.append("<NULL>");
		}
		return(s.toString());
	}

	public static <K, V> StringBuilder format(Map<K, V> map) {
		StringBuilder s = new StringBuilder();
		if ( map != null ) {
			int i = 0;
			s.append('[');
			for ( K key : map.keySet() ) {
				if ( i > 0 ) s.append(", ");
				s.append('[').append(key).append('=').append(map.get(key)).append(']');
				i++;
			}
			s.append(']');
		}
		else {
			s.append("<NULL>");
		}
		return(s);
	}

	public static <T> StringBuilder format(Iterable<T> list) {
		StringBuilder s = new StringBuilder();
		if ( list != null ) {
			int i = 0;
			s.append('[');
			for ( T item : list ) {
				if ( i > 0 ) s.append(", ");
				s.append(item);
				i++;
			}
			s.append(']');
		}
		else {
			s.append("<NULL>");
		}
		return(s);
	}
	
	public static boolean isEqual(StringBuilder builder, String str) {
		boolean b = builder.length() == str.length();		
		if ( b ) {
			for ( int i = 0; i < str.length(); i++ ) {
				if ( str.charAt(i) != builder.charAt(i) ) {
					b = false;
					break;
				}
			}
		}
		return(b);
	}
	
	public static char[] bytesToCharArrayUTF8(byte[] bytes) {
		char[] buffer = new char[bytes.length >> 1];
		for ( int i = 0; i < buffer.length; i++ ) {
			int bpos = i << 1;
			char c = (char)(((bytes[bpos]&0x00FF)<<8) + (bytes[bpos+1]&0x00FF));
			buffer[i] = c;
		}
		return(buffer);
	}
	
	public static String decodeUrl(String str) {
		//http://www.batoto.net/read/_/176789/360%c2%b0-material_v3_ch13_by_lila-wolves/44
		StringBuilder buf = new StringBuilder();
		for ( int i = 0; i < str.length(); i++ ) {
			char c = str.charAt(i);
			if ( c == '%' ) {
				if ( i+3 <= str.length() ) {
					String sAscii = str.substring(i+1, i+3);
					try {
						int iAscii = Integer.decode("0x" + sAscii);
						buf.append((char)iAscii);
						i = i+2;
					} catch (NumberFormatException e) {
						buf.append(c);						
					}
				}
			}
			else {
				buf.append(c);
			}			
		}
		return(buf.toString());
	}
	
	public static String getValueFromQueryString(String sUrl, String sKey) {
		String sValue = null;
		if ( !StringUtils.isNullOrEmpty(sUrl) ) {
			int iQueryStart = sUrl.lastIndexOf('?');
			if ( iQueryStart > 0 ) {
				String[] pairs = sUrl.substring(iQueryStart+1).split("&");
//				Log.logMessage(1, pairs.length + " pairs: " + StringUtils.format(pairs));
				for ( String sPair : pairs ) {
					String[] pair = sPair.split("=");
//					Log.logMessage(1, "pair: " + StringUtils.format(pair));
					if ( pair.length > 0 && pair[0].toLowerCase(Locale.US).equals(sKey) ) {
						sValue = ( pair.length == 2 ) ? pair[1] : "";
					}
				}
			}
		}
		return(sValue);
	}
}
