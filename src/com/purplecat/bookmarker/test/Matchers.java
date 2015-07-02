package com.purplecat.bookmarker.test;

import java.util.Calendar;

import org.joda.time.DateTime;

public class Matchers {
	
	public static boolean MatchDateTime(Calendar expected, Calendar actual, int secondsLeeway) {
		long offset = secondsLeeway * 1000;
		return expected.getTimeInMillis() - offset < actual.getTimeInMillis() &&
				expected.getTimeInMillis() + offset > actual.getTimeInMillis();
	}
	
	public static boolean MatchDateTime(DateTime expected, DateTime actual, int secondsLeeway) {
		long offset = secondsLeeway * 1000;
		return expected.getMillis() - offset < actual.getMillis() &&
				expected.getMillis() + offset > actual.getMillis();
	}

}
