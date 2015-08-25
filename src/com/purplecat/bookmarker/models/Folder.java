package com.purplecat.bookmarker.models;

public class Folder {
	public long _id;
	
	public String _name;
	
	public EStoryState _storyState;

	public void updateFrom(Folder folder) {
		_name = folder._name;
		_storyState = folder._storyState;
	}

	public Folder copy() {
		Folder folder = new Folder();
		folder._id = _id;
		folder._name = _name;
		folder._storyState = _storyState;
		return folder;
	}

	public boolean ignoreUpdate() {
		if ( _storyState == null ) {
			return false;
		}
		
		switch (_storyState) {
			case DONT_READ:
			case MIDDLE_CHAPTER_BORED:
			case MIDDLE_CHAPTER_REREAD:
				return true;
			default:
				return false;
		}
	}
}
