package com.purplecat.bookmarker.extensions;

import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.models.EStoryState;
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
	
	public static StoryStateModel getView(Media view) {
		StoryStateModel imageModel = new StoryStateModel();
		
		EStoryState state = view._isComplete ? EStoryState.FINISHED_BOOKMARK : EStoryState.LAST_AVAILABLE_CHAPTER;
		/*BookmarkFolder folder = view.getFolder();
		if (  folder != null ) {
			state =  folder.getStoryState();
		}*/
		imageModel._imageKey = getImageKey(state, false); //false=not movie
		
		if ( view.isUpdated() ) {
			imageModel._updateMode = StoryStateModel.FULL_UPDATE;
					/*(folder == null || !folder.ignoreUpdates()) ? 
					Options.get(BookmarkOptions.UPDATED_COLOR) : 
					Options.get(BookmarkOptions.MUTED_UPDATED_COLOR);*/
		}
		else {
			imageModel._updateMode = StoryStateModel.NOT_UPDATED;
		}

		return(imageModel);		
	}
	
	public static StoryStateModel getView(OnlineMediaItem view) {
		StoryStateModel imageModel = new StoryStateModel();
		
		if ( view._id > 0 ) {
			
			//EStoryState state = view._isComplete ? EStoryState.FINISHED_BOOKMARK : EStoryState.LAST_AVAILABLE_CHAPTER;
			/*BookmarkFolder folder = view.getFolder();
			if (  folder != null ) {
				state =  folder.getStoryState();
			}*/
			imageModel._imageKey = getImageKey(EStoryState.LAST_AVAILABLE_CHAPTER, false); //false=not movie
			
			if ( view._isSaved ) {
				imageModel._updateMode = view.isUpdated() ? StoryStateModel.FULL_UPDATE : StoryStateModel.MUTED_UPDATE;
						/*(folder == null || !folder.ignoreUpdates()) ? 
						Options.get(BookmarkOptions.UPDATED_COLOR) : 
						Options.get(BookmarkOptions.MUTED_UPDATED_COLOR);*/
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
	}*/

}
