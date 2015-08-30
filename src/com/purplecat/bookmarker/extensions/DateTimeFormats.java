package com.purplecat.bookmarker.extensions;

import java.util.Comparator;

import org.joda.time.DateTime;

public class DateTimeFormats {
	
	public static class ReverseDateTimeComparor implements Comparator<DateTime> {
		@Override
		public int compare(DateTime s1, DateTime s2) {
			int result = 0;
			if ( s1 != null && s2 != null ) {
				result = -s1.compareTo(s2);
			}
			else if ( s1 == null && s2 == null ) {
				result = 0;
			}
			else {
				result = ( s1 != null ? 1 : -1 );
			}
			
			return(result);
		}
	}

}
