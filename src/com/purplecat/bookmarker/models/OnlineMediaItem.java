package com.purplecat.bookmarker.models;

import org.joda.time.DateTime;

public class OnlineMediaItem {
	
	public String _chapterUrl = "";
	
	public String _displayTitle = "";
	
	public long _id = -1;
	
	public boolean _isIgnored = false;
	
	public boolean _isSaved = false;
	
	public Place _lastReadPlace;
	
	public DateTime _lastReadDate;
	
	public long _mediaId = -1;
	
	public boolean _newlyAdded = false;
	
	public double _rating;
	
	public String _titleUrl = "";
	
	public Place _updatedPlace = new Place();
	
	public DateTime _updatedDate;
	
	public String _websiteName;

}
