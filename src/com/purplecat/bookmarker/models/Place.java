package com.purplecat.bookmarker.models;

public class Place {
	public int _volume;
	public int _chapter;
	public int _subChapter;
	public int _page;
	public boolean _extra;

	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof Place ) {
			Place p = (Place)obj;
			return _volume == p._volume && 
					_chapter == p._chapter && 
					_subChapter == p._subChapter && 
					_page == p._page && 
					_extra == p._extra;
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return String.format("%d|%d|%d|%d|%s", _volume, _chapter, _subChapter, _page, _extra ? "*" : "").hashCode();
	}

	@Override
	public String toString() {
		return String.format("[Place: v%d ch%d.%d%s pg%d]", _volume, _chapter, _subChapter, _extra ? "*" : "", _page);
	}

	public Place copy() {
		Place place = new Place();
		place._volume = this._volume;
		place._chapter = this._chapter;
		place._subChapter= this._subChapter;
		place._page = this._page;
		place._extra = this._extra;
		return place;
	}
}
