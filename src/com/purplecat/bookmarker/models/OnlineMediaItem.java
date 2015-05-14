package com.purplecat.bookmarker.models;

import org.joda.time.DateTime;

public class OnlineMediaItem {
	
	/**
	 * Loaded from website
	 */
	public String _chapterUrl = "";

	/**
	 * Loaded from website
	 */
	public String _displayTitle = "";
	
	public long _id = -1;
	
	public boolean _isIgnored = false;
	
	public boolean _isSaved = false;
	
	public Place _lastReadPlace;
	
	public DateTime _lastReadDate;
	
	public long _mediaId = -1;
	
	public boolean _newlyAdded = false;

	/**
	 * Loaded from website
	 */
	public double _rating;

	/**
	 * Loaded from website
	 */
	public String _titleUrl = "";

	/**
	 * Loaded from website
	 */
	public Place _updatedPlace = new Place();

	/**
	 * Loaded from website
	 */
	public DateTime _updatedDate;

	/**
	 * Loaded from website
	 */
	public String _websiteName;
	
	@Override
	public String toString() {
		return String.format("[OnlineMediaItem-%d [title=%s][url=%s][place=%s]]", _id, _displayTitle, _chapterUrl, _updatedPlace.toString());
	}

}
