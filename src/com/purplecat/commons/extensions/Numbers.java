package com.purplecat.commons.extensions;

public class Numbers {
	public static int parseInt(String s, int def) {
		int value = def;
		try {
			value = Integer.parseInt(s);
		} catch (NumberFormatException e) {
			value = def;
		}
		return value;
	}

	public static long parseLong(String s, long def) {
		long value = def;
		try {
			value = Long.parseLong(s);
		} catch (NumberFormatException e) {
			value = def;
		}
		return value;
	}
}
