package com.purplecat.bookmarker.models;

public class Genre {
	public long _id;
	
	public String _name;
	
	public String _altName;
	
	public boolean _include;
	
	public Genre() {}
	
	public Genre(String str) {
		_name = str;
	}
	
	@Override
	public String toString() {
		return String.format("[Genre-%d \"%s\" (%b)]", _id, _name, _include);
	}
	
	@Override
	public int hashCode() {
		if ( _id == 0 ) {
			return _name.hashCode();
		}
		else {
			return (int)_id;
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if ( obj instanceof Genre ) {
			Genre genre = (Genre)obj;
			if ( genre._id == this._id ) {
				if ( this._id == 0 ) {
					return genre._name.equals(genre._name);
				}
				return true;
			}
			return false;
		}
		return false;
	}

}
