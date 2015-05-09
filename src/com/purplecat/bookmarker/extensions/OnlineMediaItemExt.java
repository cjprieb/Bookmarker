package com.purplecat.bookmarker.extensions;

import com.purplecat.bookmarker.models.OnlineMediaItem;

public class OnlineMediaItemExt {
	
	/**
	 * Only copies the data found from scraping the website,
	 * as newItem was generated from the parsing and doesn't
	 * yet have any user data (like _isIgnored and _isNewlyAdded)
	 * _displayTitle and _websiteName don't have to be copied since 
	 * they were used to look up the database item.
	 * @param newItem Object parsed from the website
	 * @param existing Object found in the database
	 */
	public static void copyNewToExisting(OnlineMediaItem newItem, OnlineMediaItem existing) {		
		if ( newItem._chapterUrl != null && newItem._chapterUrl.length() > 0 ) {
			existing._chapterUrl = newItem._chapterUrl;
		}
		
		if ( newItem._titleUrl != null && newItem._titleUrl.length() > 0 ) {
			existing._titleUrl = newItem._titleUrl;
		}
		
		if ( newItem._rating > 0 ) {
			existing._rating = newItem._rating;
		}
		
		if ( existing._updatedPlace.compareTo(newItem._updatedPlace) < 0 ) {
			existing._updatedPlace = newItem._updatedPlace;
		}
		existing._updatedDate = newItem._updatedDate;		
	}
}
