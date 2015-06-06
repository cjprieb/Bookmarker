package com.purplecat.bookmarker.models;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.joda.time.DateTime;

public class Media extends BaseDatabaseItem implements Comparable<Media> {
	
	public String _chapterURL;

	public String _displayTitle;
	
	public final List<Genre> _genres;
	
	public boolean _isComplete;

	public boolean _isSaved;

	public DateTime _lastReadDate;

	public Place _lastReadPlace;
	
	public String _notes;
	
	public EFavoriteState _rating;
	
	public EStoryState _storyState;

	public DateTime _updatedDate;

	public Place _updatedPlace;
	
	public String _updatedUrl;
	
	public Media() {
		_lastReadPlace = new Place();
		_genres = new ArrayList<Genre>();
	}
	
	public boolean isUpdated() {
		if ( _isSaved && _updatedPlace != null ) {
			return _lastReadPlace.compareTo(_updatedPlace) < 0;
		}
		else {
			return false;
		}
	}
	
	public boolean isRead() {
		if ( _updatedPlace != null ) {
			return _lastReadPlace.compareTo(_updatedPlace) >= 0;
		}
		else {
			return false;
		}
	}

	@Override
	public String toString() {
		return String.format("[Media (%d): %s]", _id, _displayTitle);
	}
	
	@Override
	public int compareTo(Media m) {
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

	public Media copy() {
		Media media = new Media();
		media._id = this._id;
		media._chapterURL = this._chapterURL;
		media._displayTitle = this._displayTitle;
		media._isSaved = this._isSaved;
		media._lastReadDate = this._lastReadDate;
		media._lastReadPlace = this._lastReadPlace.copy();
		media._updatedDate = this._updatedDate;
		media._updatedPlace = (this._updatedPlace != null ? this._updatedPlace.copy() : null);
		media._storyState = this._storyState;
		media._notes = this._notes;
		media._rating = this._rating;
		media._isComplete = this._isComplete;
		media._genres.clear();
		media._genres.addAll(this._genres);
		return media;
	}

	public void updateFrom(OnlineMediaItem item) {
		this._updatedDate = item._updatedDate;
		this._updatedPlace = item._updatedPlace;
		this._genres.clear();
		this._genres.addAll(item._genres);
	}
	
	public static class FavoriteComparor implements Comparator<EFavoriteState> {
		@Override
		public int compare(EFavoriteState s1, EFavoriteState s2) {
			return(s1.mRelativeValue == s2.mRelativeValue ? 0 : (s1.mRelativeValue > s2.mRelativeValue ? 1 : -1));
		}
	}
}
