package com.purplecat.bookmarker.models;


public enum EStoryState {
	//DON'T CHANGE ORDER: Values are saved as the order-index
	/*Bookmark States*/
	LAST_AVAILABLE_CHAPTER(0),	//0
	MIDDLE_CHAPTER(1),			//1
	FINISHED_BOOKMARK(2),		//2
	DONT_READ(3),				//3
	NEW_BOOKMARK(4),			//4
	ALL(5),						//5
	MIDDLE_CHAPTER_REREAD(6),	//6
	MIDDLE_CHAPTER_BORED(7);	//7
	
	public final int mValue;
	
	EStoryState(int value) { mValue = value; }
	
	public int getValue() { return(mValue); }
	
	public static EStoryState[] MEDIA_STATE_ORDER = {
			EStoryState.LAST_AVAILABLE_CHAPTER,	//0
			EStoryState.FINISHED_BOOKMARK,		//2
			EStoryState.MIDDLE_CHAPTER_REREAD,	//6
			EStoryState.MIDDLE_CHAPTER_BORED,	//7
			EStoryState.DONT_READ,			};	//3
}
