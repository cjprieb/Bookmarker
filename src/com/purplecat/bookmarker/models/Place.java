package com.purplecat.bookmarker.models;

public class Place implements Comparable<Place> {
	public int _volume;
	public int _chapter;
	public int _subChapter;
	public int _page;
	public boolean _extra;
	
	public Place() {}
	
	public Place(int v, int c, int s, int p, boolean e) {
		_volume = v;
		_chapter = c;
		_subChapter = s;
		_page = p;
		_extra = e;
	}

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

	@Override
	public int compareTo(Place p) {
		int result = 0;
		
		if ( result == 0 && _volume != 0 && p._volume != 0 && _volume != p._volume ) {
			result = ( _volume > p._volume ) ? 1 : -1;
		}		

		if ( result == 0 && _chapter != p._chapter ) {
			result = ( _chapter > p._chapter ) ? 1 : -1;
		}
		
		if ( result == 0 && _extra != p._extra ) {
			result = ( _extra ) ? 1 : -1;
		}
		
		if ( result == 0 && _subChapter != p._subChapter ) {
			result = ( _subChapter > p._subChapter ) ? 1 : -1;			
		}

		if ( result == 0 && _page != 0 && p._page != 0 && _page != p._page ) {
			result = ( _page > p._page ) ? 1 : -1;
		}
		
		return(result);
	}
}
