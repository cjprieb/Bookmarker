package com.purplecat.bookmarker.extensions;

public class TitleExt {	
	//Assumes to be Latin characters without diacractics
	public static String stripTitle(String s) {
		int iString = 0;
		int iStrippedCnt = 0;
		for ( iString = 0; iString < s.length(); iString++ ) {
			char c = s.charAt(iString);
			if ( Character.isDigit(c) || Character.isLetter(c) ) {
				iStrippedCnt++;
			}
		}
		
		//char[] stripped = new char[s.length()];
		StringBuilder sb = new StringBuilder(iStrippedCnt);
		for ( iString = 0; iString < s.length(); iString++ ) {
			char c = s.charAt(iString);
			if ( Character.isDigit(c) ) {
				sb.append(c);
			}
			else if ( Character.isLetter(c) ) {
				sb.append(Character.toLowerCase(c));
			}
		}
		return(sb.toString());
	}
}
