package com.purplecat.bookmarker.extensions;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.joda.time.DateTime;

import com.purplecat.commons.extensions.Numbers;

public class WebsiteDateExt {
	protected static Pattern _daysAgoRegex = Pattern.compile("(?:(\\d+)|(\\w+)) (\\w+) ago");
	protected static Pattern _dateRegex = Pattern.compile("(\\w+), (\\d+):(\\d+) (\\w+)");
	
	public static DateTime parseBatotoDate(DateTime now, String text) {
		
		DateTime result = null;

		//35 minutes ago 
		//An hour ago
		//4 hours ago 
		//A day ago
		//2 days ago
		Matcher matcher = _daysAgoRegex.matcher(text);
		if (matcher.find()){			
			int unitsAgo = 1;			
			if ( matcher.group(1) != null ) {
				unitsAgo = Numbers.parseInt(matcher.group(1),1);
			}
			
			if ( matcher.group(3).startsWith("minute") ) {
				result = now.minusMinutes(unitsAgo);
			}
			else if ( matcher.group(3).startsWith("hour") ) {
				result = now.minusHours(unitsAgo);
			}
			else if ( matcher.group(3).startsWith("day") ) {
				result = now.minusDays(unitsAgo);
			}
		}
		else {
			//Today, 09:05 PM
			//Today, 08:50 PM
			//Today, 07:47 PM
			matcher = _dateRegex.matcher(text);
			if (matcher.find()){
				
				int hours = Numbers.parseInt(matcher.group(2), 0);	
				int minutes = Numbers.parseInt(matcher.group(3), 0);		
				if ( matcher.group(4).equalsIgnoreCase("PM") ) {
					hours += 12;
				}
				
				if ( matcher.group(1).toLowerCase().equals("today") ) {
					result = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), hours, minutes);
				}
			}			
		}
		return result;
	}
}
