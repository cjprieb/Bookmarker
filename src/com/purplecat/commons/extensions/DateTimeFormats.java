package com.purplecat.commons.extensions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;

public class DateTimeFormats {
	public static final String SQLITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.SSS";	
	
	public static final CalendarFormat FORMAT_FILE_DATE 	= new CalendarFormat("yy-MM-dd HH-mm");
	public static final CalendarFormat FORMAT_MMDD_HHMM 	= new CalendarFormat("MM/dd HH:mm");
	public static final CalendarFormat FORMAT_MMDDYY		= new CalendarFormat("MM/dd/yy");
	public static final CalendarFormat FORMAT_YYMMDD_HHMM	= new CalendarFormat("yy/MM/dd HH:mm");
	public static final CalendarFormat FORMAT_YYMMDD_HHMM_Z	= new CalendarFormat("yy/MM/dd HH:mm Z");
	public static final CalendarFormat FORMAT_HHMM 			= new CalendarFormat("HH:mm");
	public static final CalendarFormat FORMAT_HHMMSSM 		= new CalendarFormat("HH:mm:ss.SSS");
	public static final CalendarFormat FORMAT_SQLITE_DATE 	= new CalendarFormat("yyyy-MM-dd HH:mm:ss.SSS");
	
	public static final int DATE_ONLY 		= 0;
	public static final int TIME_ONLY 		= 1;
	public static final int DATE_AND_TIME 	= 2;
	
	public static Calendar parse(String format, String str, Calendar def) {
		return(parse(new SimpleDateFormat(format), str, def));
	}
	
	public static Calendar parse(DateFormat format, String str, Calendar def) {
		Calendar c = def;
		try {
			Date d = format.parse(str);
			if ( d != null ) {
				c.setTime(d);
			}
		} catch (ParseException e) {}
		return(c);
	}
	
	public static boolean tryParse(String format, String str, Calendar def) {
		return(tryParse(new SimpleDateFormat(format), str, def));
	}
	
	public static boolean tryParse(DateFormat format, String str, Calendar def) {
		try {
			Date d = format.parse(str);
			if ( d != null && def != null ) {				
				def.setTime(d);
				return(true);
			}
		} catch (ParseException e) {}
		return(false);
	}
	
	/** Using Calendar - THE CORRECT WAY**/  
	public static long daysBetween(Calendar startDate, Calendar endDate, boolean roundUp) {  
		Calendar date = (Calendar) startDate.clone();  
		long daysBetween = 0;  
		
		long result = compareDate(date, endDate, !roundUp);
		if ( result == 0 ) {
			daysBetween = 0;
		}
		else if ( result > 0 ) {
			do {  
				date.add(Calendar.DAY_OF_MONTH, -1);  
				daysBetween--;
				result = compareDate(date, endDate, !roundUp);				
			} while ( result > 0 );
		}
		else {
			do {  
				date.add(Calendar.DAY_OF_MONTH, 1);  
				daysBetween++;  
				result = compareDate(date, endDate, !roundUp);			
			} while ( result < 0 );	
		}
		return daysBetween;  
	}
	
	public static long compareDate(Calendar date1, Calendar date2, boolean dateOnly) { 
		if ( dateOnly ) {
			if ( date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
					date1.get(Calendar.MONTH) == date2.get(Calendar.MONTH) &&
					date1.get(Calendar.DAY_OF_MONTH) == date2.get(Calendar.DAY_OF_MONTH) ) {
				return(0);
			}
		}
		return(date1.compareTo(date2));
	}
	
	public static class CalendarFormat {
		private SimpleDateFormat mFormat;
		
		public CalendarFormat(String s) {
			mFormat = new SimpleDateFormat(s);
		}
		
		public Calendar parseOrDefault(String s, Calendar def) {
			Date date = null;
			if ( s != null ) {
				try {
					synchronized (mFormat) {
						date = mFormat.parse(s);
					}
				} catch (ParseException e) {
					date = null;
				}
			}
			if ( date != null ) {
				Calendar c = Calendar.getInstance();
				c.setTime(date);
				return(c);				
			}
			else {
				return def;
			}
		}
		
		public Calendar parse(String s) throws ParseException {
			synchronized (mFormat) {
				Date d = mFormat.parse(s);
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				return(c);
			}
		}
		
		public String format(Calendar c) {
			synchronized (mFormat) {
				if ( c != null ) {
					return(mFormat.format(c.getTime()));
				}
				else {
					return("");
				}
			}
		}
		
		public String formatString() {
			synchronized (mFormat) {
				return(mFormat.toPattern());
			}
		}
	}
	
	public static class ReverseDateComparor implements Comparator<Calendar> {
		@Override
		public int compare(Calendar s1, Calendar s2) {
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
