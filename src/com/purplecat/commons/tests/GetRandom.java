package com.purplecat.commons.tests;

import java.util.Calendar;
import java.util.List;

public class GetRandom {
	static java.util.Random _random = new java.util.Random(Calendar.getInstance().getTimeInMillis());
	
	public static <T> T getItem(T[] array) {
		if ( array == null || array.length == 0 ) {
			return null;
		}
		else {
			return array[getInteger(0, array.length-1)];
		}			
	}
	
	public static <T> T getItem(List<T> list) {
		if ( list == null || list.size() == 0 ) {
			return null;
		}
		else {
			return list.get(getInteger(0, list.size()-1));
		}			
	}
	
	public static int getInteger() {
		return _random.nextInt();
	}
	
	public static int getInteger(int min, int max) {
		return _random.nextInt(max-min+1) + min;
	}

	public static String getString(int length) {
		StringBuilder builder = new StringBuilder();
		for(int i = 0; i < length; i++) {
			builder.append(getLetter());
		}
		return builder.toString();
	}

	public static char getLetter() {
		return (char)((_random.nextBoolean() ? 'a' : 'A') + _random.nextInt(26));
	}

}
