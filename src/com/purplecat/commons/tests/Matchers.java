package com.purplecat.commons.tests;

import java.util.Calendar;

public class Matchers {
	
	public static boolean MatchDateTime(Calendar expected, Calendar actual, int secondsLeeway) {
		long offset = secondsLeeway * 1000;
		return expected.getTimeInMillis() - offset < actual.getTimeInMillis() &&
				expected.getTimeInMillis() + offset > actual.getTimeInMillis();
	}

}
