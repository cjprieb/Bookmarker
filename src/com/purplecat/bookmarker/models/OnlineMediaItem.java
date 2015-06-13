package com.purplecat.bookmarker.models;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

public class OnlineMediaItem extends BaseDatabaseItem implements Comparable<OnlineMediaItem> {
	
	/**
	 * Loaded from website
	 */
	public String _chapterName = "";

	/**
	 * Loaded from website
	 */
	public String _chapterUrl = "";

	/**
	 * Loaded from website
	 */
	public String _displayTitle = "";
	
	public final Set<Genre> _genres;
	
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
	public String _summary = "";

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
	
	public OnlineMediaItem() {
		_genres = new HashSet<Genre>();
	}
	
	public boolean isUpdated() {
		if ( _isSaved && _lastReadPlace != null ) {
			return _lastReadPlace.compareTo(_updatedPlace) < 0;
		}
		else {
			return false;
		}
	}
	
	public boolean isRead() {
		if ( _isSaved && _lastReadPlace != null ) {
			return _lastReadPlace.compareTo(_updatedPlace) >= 0;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return String.format("[OnlineMediaItem-%d [title=%s][url=%s][place=%s]]", _id, _displayTitle, _chapterUrl, _updatedPlace.toString());
	}
	
	@Override
	public int compareTo(OnlineMediaItem m) {
		if ( isUpdated() == m.isUpdated() ) {
			if ( isUpdated() ) {
				if ( _updatedDate != null && m._updatedDate != null ) {
					return m._updatedDate.compareTo(_updatedDate);
				}
				else {
					return _updatedDate != null ? -1 : 1;
				}
			}
			else {
				if ( _lastReadDate != null && m._lastReadDate != null ) {
					return m._lastReadDate.compareTo(_lastReadDate);
				}
				else {
					return _lastReadDate != null ? -1 : 1;
				}
			}
		}
		else {
			return isUpdated() ? -1 : 1;
		}
	}

	public OnlineMediaItem copy() {
		OnlineMediaItem media = new OnlineMediaItem();
		media._id = this._id;
		media._chapterUrl = this._chapterUrl;
		media._chapterName = this._chapterName;
		media._displayTitle = this._displayTitle;
		media._isSaved = this._isSaved;
		media._lastReadDate = this._lastReadDate;
		media._lastReadPlace = (this._lastReadPlace != null ? this._lastReadPlace.copy() : null);
		media._updatedDate = this._updatedDate;
		media._updatedPlace = this._updatedPlace.copy();
		//media._storyState = this._storyState;
		//media._notes = this._notes;
		media._rating = this._rating;
		//media._isComplete = this._isComplete;
		media._genres.clear();
		media._genres.addAll(this._genres);
		return media;
	}

	public void updateFrom(Media item) {
		this._isSaved = item._isSaved;
		this._lastReadDate = item._lastReadDate;
		this._lastReadPlace = (item._lastReadPlace != null ? item._lastReadPlace.copy() : null);
		this._genres.clear();
		this._genres.addAll(item._genres);
	}

}
