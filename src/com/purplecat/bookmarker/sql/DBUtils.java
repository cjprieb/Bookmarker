package com.purplecat.bookmarker.sql;

public class DBUtils {
	
	public static String formatIdList(Iterable<Long> idList) {
		StringBuilder builder = new StringBuilder();
		int i = 0;
		for ( Long id : idList ) {
			if ( i > 0 ) { builder.append(","); }
			builder.append(id);
			i++;
		}
		return builder.toString();
	}

}
