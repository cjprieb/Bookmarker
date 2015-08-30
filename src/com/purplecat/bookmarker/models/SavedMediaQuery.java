package com.purplecat.bookmarker.models;

public class SavedMediaQuery {
	public Boolean _isUpdated;
	public Boolean _isCompleted;
	public String _keyword;
	
	public boolean matches(Media item) {
		boolean bMatch = true;
		if ( bMatch && _isUpdated != null ) {
			bMatch = item.isUpdated() == _isUpdated;
		}
		if ( bMatch && _keyword != null ) {
			bMatch = item.getDisplayTitle().toLowerCase().contains(_keyword);
		}
		if ( bMatch && _isCompleted != null ) {
			bMatch = item._isComplete == _isCompleted;
		}
		return bMatch;
	}	
}
