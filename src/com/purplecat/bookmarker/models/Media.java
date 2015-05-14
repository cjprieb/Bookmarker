package com.purplecat.bookmarker.models;

import org.joda.time.DateTime;

public class Media extends BaseDatabaseItem {
	
	public String _chapterURL;

	public String _displayTitle;
	
	public boolean _isComplete;

	public boolean _isSaved;
	
	public boolean _isUpdated;

	public DateTime _lastReadDate;

	public Place _lastReadPlace;
	
	public String _notes;
	
	public EFavoriteState _rating;
	
	public EStoryState _storyState;
	
	public Media() {
		_lastReadPlace = new Place();
	}

	@Override
	public String toString() {
		return String.format("[Media (%d): %s]", _id, _displayTitle);
	}

	public Media copy() {
		Media media = new Media();
		media._id = this._id;
		media._chapterURL = this._chapterURL;
		media._displayTitle = this._displayTitle;
		media._isSaved = this._isSaved;
		media._lastReadDate = this._lastReadDate;
		media._lastReadPlace = this._lastReadPlace.copy();		
		return media;
	}
}
