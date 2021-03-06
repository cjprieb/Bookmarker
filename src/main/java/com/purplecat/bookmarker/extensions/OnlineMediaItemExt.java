package com.purplecat.bookmarker.extensions;

import java.util.Comparator;
import java.util.List;

import com.purplecat.bookmarker.services.websites.IWebsiteList;
import org.joda.time.Duration;

import com.purplecat.bookmarker.models.OnlineMediaItem;

public class OnlineMediaItemExt {
	
	/**
	 * Only copies the data found from scraping the website,
	 * as newItem was generated from the parsing and doesn't
	 * yet have any user data (like _isIgnored)
	 * _displayTitle and _websiteName don't have to be copied since 
	 * they were used to look up the database item.
	 * @param newItem Object parsed from the website
	 * @param existing Object found in the database
	 */
	public static void copyNewToExisting(OnlineMediaItem newItem, OnlineMediaItem existing) {		
		if ( newItem._rating > 0 ) {
			existing._rating = newItem._rating;
		}
		
		if ( newItem._chapterUrl != null && newItem._chapterUrl.length() > 0 ) {
			existing._chapterUrl = newItem._chapterUrl;
		}
		
		if ( newItem._titleUrl != null && newItem._titleUrl.length() > 0 ) {
			existing._titleUrl = newItem._titleUrl;
		}
		
		//Make sure 
		int placeCompare = existing._updatedPlace.compareTo(newItem._updatedPlace);
		if ( placeCompare <= 0 ) {
			existing._updatedPlace = newItem._updatedPlace;
			if ( placeCompare < 0 ) {
			}
		}

		Duration timeDiff = new Duration(existing._updatedDate, newItem._updatedDate);
		if ( placeCompare != 0 || 
				(existing._updatedDate.isBefore(newItem._updatedDate) && timeDiff.getStandardHours() > 24) ) {			
			existing._updatedDate = newItem._updatedDate;
			existing._newlyAdded = true;
		}
		else {
			existing._newlyAdded = false;
		}
	}
	
	public static long getIdWithMaxPlace(List<OnlineMediaItem> list, OnlineMediaItem item) {
		OnlineMediaItem maxItem = item;
		for ( OnlineMediaItem existing : list ) {
			//TODO: find max online media item from preferred website order and last updated date 
			if ( maxItem == null || existing._updatedPlace.compareTo(maxItem._updatedPlace) > 0 ) {
				maxItem = existing;
			}
		}
		return maxItem._id;
	}
	
	public static String getPreferredUrl(OnlineMediaItem item) {
		if ( item == null ) {
			return "";
		}
		else if ( item._isSaved ) {
			return item._chapterUrl;
		}
		else {
			return item._titleUrl;
		}
	}
	
	public static class OnlineBookmarkComparator implements Comparator<OnlineMediaItem> {
		private final IWebsiteList _websiteList;

		public OnlineBookmarkComparator(IWebsiteList websiteList) {
			_websiteList = websiteList;
		}

		@Override
		public int compare(OnlineMediaItem o1, OnlineMediaItem o2) {
			if ( o1._websiteName == null && o2._websiteName == null ) {
				return 0;
			}
			else if ( o1._websiteName == null || o2._websiteName == null ) {
				return o1._websiteName == null ? -1 : 1;
			}
			
			if ( o1._websiteName.equals(o2._websiteName) ) {
				if ( o1._id <= 0 || o2._id <= 0 ) {//assume it's a site bookmark if the _id is less than 0
					return o1._id <= 0 ? -1 : 1;
				}
				else if ( o1.isUpdated() == o2.isUpdated() ) {
					return o2._updatedDate.compareTo(o1._updatedDate);
				}
				else {
					return o1.isUpdated() ? -1 : 1;
				}
			}
			else {
				return _websiteList.compare(o1._websiteName, o2._websiteName);
			}
		}		
	}
}
