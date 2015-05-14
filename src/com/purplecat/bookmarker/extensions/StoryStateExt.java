package com.purplecat.bookmarker.extensions;

import com.purplecat.bookmarker.models.EStoryState;


public class StoryStateExt {

	public static EStoryState parse(int value) {
		for ( EStoryState item : EStoryState.values() ) {
			if ( item.getValue() == value ) {
				return(item);
			}
		}
		return(EStoryState.LAST_AVAILABLE_CHAPTER);
	}
	
	/*public static boolean isIgnoreUpdateState(BookmarkFolder folder) {
		boolean ignore = false;
		if ( folder != null ) {
			ignore = folder.ignoreUpdates();
//			switch ( folder.getStoryState() ) {
//				case DONT_READ:
//				case NEW_BOOKMARK:
//				case MIDDLE_CHAPTER: 
//				case MIDDLE_CHAPTER_BORED: 	ignore = true;	break;
//				default:					ignore = false;	break;
//			}
		}
		return(ignore);
	}
	
	public static int getCompareValue(EStoryState state) {
		int key = 0;
		if ( state != null ) {
			switch (state) {
				case FINISHED_BOOKMARK: 		key = 0;	break;
				case LAST_AVAILABLE_CHAPTER: 	key = 1;	break;
				case MIDDLE_CHAPTER: 			key = 2;	break;
				case NEW_BOOKMARK: 				key = 4;	break;
				case MIDDLE_CHAPTER_REREAD:		key = 5;	break;
				case MIDDLE_CHAPTER_BORED: 		key = 6;	break;
				case DONT_READ:					key = 7;	break;
				default:									break;
			}
		}
		return(key);
	}
	
	public static String getImageKey(EStoryState state, boolean isMovie, String key) {		
		if ( state != null ) {
			switch (state) {
				case LAST_AVAILABLE_CHAPTER: 	key = isMovie ? AppImages.imgEndSeasonId : AppImages.imgOpenBookId;			break;
				case MIDDLE_CHAPTER: 			key = isMovie ? AppImages.imgMidSeasonId : AppImages.imgOpenBookQuestionId;	break;
				case FINISHED_BOOKMARK: 		key = isMovie ? AppImages.imgFinishedSeasonId : AppImages.imgClosedBookId;	break;
				case NEW_BOOKMARK: 				key = isMovie ? AppImages.imgNewSeasonId : AppImages.imgClosedBowRedId;		break;
				
				case DONT_READ:					key = AppImages.imgClosedRedXId;			break;
				case MIDDLE_CHAPTER_REREAD:		key = AppImages.imgOpenBookmarkOrangeId;	break;
				case MIDDLE_CHAPTER_BORED: 		key = AppImages.imgClosedBookmarkRedId;		break;
				default:																	break;
			}
		}
		return(key);
	}*/

}
