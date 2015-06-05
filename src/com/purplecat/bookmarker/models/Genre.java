package com.purplecat.bookmarker.models;

public class Genre {
	public long _id;
	
	public String _name;
	
	public String _altName;
	
	public boolean _include;
	
	@Override
	public String toString() {
		return String.format("[Genre-%d \"%s\" (%b)]", _id, _name, _include);
	}

}
