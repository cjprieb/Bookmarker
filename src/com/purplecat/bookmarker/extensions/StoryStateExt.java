package com.purplecat.bookmarker.extensions;

import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.models.EStoryState;
import com.purplecat.bookmarker.models.Folder;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.models.StoryStateModel;


public class StoryStateExt {

	public static EStoryState parse(int value) {
		for ( EStoryState item : EStoryState.values() ) {
			if ( item.getValue() == value ) {
				return(item);
			}
		}
		return(EStoryState.LAST_AVAILABLE_CHAPTER);
	}
	
	public static StoryStateModel getView(Media view, Folder folder) {
		StoryStateModel imageModel = new StoryStateModel();
		
		EStoryState state = folder != null ? folder._storyState : EStoryState.LAST_AVAILABLE_CHAPTER;
		imageModel._imageKey = getImageKey(state, false); //false=not movie
		
		if ( view.isUpdated() ) {
			imageModel._updateMode = StoryStateModel.FULL_UPDATE;
		}
		else {
			imageModel._updateMode = StoryStateModel.NOT_UPDATED;
		}

		return(imageModel);		
	}
	
	public static StoryStateModel getView(OnlineMediaItem view, Folder folder) {
		StoryStateModel imageModel = new StoryStateModel();
		boolean ignored = folder != null ? folder.ignoreUpdate() : false;
		
		if ( view._id > 0 ) {
			
			imageModel._imageKey = getImageKey(EStoryState.LAST_AVAILABLE_CHAPTER, false); //false=not movie
			
			if ( view._isSaved ) {
				imageModel._updateMode = (view.isUpdated() && !ignored) ? StoryStateModel.FULL_UPDATE : StoryStateModel.MUTED_UPDATE;
			}
			else {
				imageModel._updateMode = StoryStateModel.NOT_UPDATED;
			}
		}

		return(imageModel);		
	}
	
	public static int getImageKey(EStoryState state, boolean isMovie) {
		int key = 0;
		if ( state != null ) {
			switch (state) {
				case LAST_AVAILABLE_CHAPTER: 	key = isMovie ? Resources.image.imgEndSeasonId : Resources.image.imgOpenBookId;			break;
				case MIDDLE_CHAPTER: 			key = isMovie ? Resources.image.imgMidSeasonId : Resources.image.imgOpenBookQuestionId;	break;
				case FINISHED_BOOKMARK: 		key = isMovie ? Resources.image.imgFinishedSeasonId : Resources.image.imgClosedBookId;	break;
				case NEW_BOOKMARK: 				key = isMovie ? Resources.image.imgNewSeasonId : Resources.image.imgClosedBowRedId;		break;
				
				case DONT_READ:					key = Resources.image.imgClosedRedXId;			break;
				case MIDDLE_CHAPTER_REREAD:		key = Resources.image.imgOpenBookmarkOrangeId;	break;
				case MIDDLE_CHAPTER_BORED: 		key = Resources.image.imgClosedBookmarkRedId;	break;
				default:																		break;
			}
		}
		return(key);
	}
	
//	public static boolean isIgnoreUpdateState(EStoryState state) {
//		boolean ignore = false;
//		if ( state != null ) {
////			ignore = folder.ignoreUpdates();
//			switch ( state ) {
//				case DONT_READ:
//				case NEW_BOOKMARK:
//				case MIDDLE_CHAPTER: 
//				case MIDDLE_CHAPTER_BORED: 	ignore = true;	break;
//				default:					ignore = false;	break;
//			}
//		}
//		return(ignore);
//	}
	
	/*public static int getCompareValue(EStoryState state) {
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
	}*/

}
