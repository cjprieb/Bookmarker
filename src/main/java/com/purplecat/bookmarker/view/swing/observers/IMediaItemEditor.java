package com.purplecat.bookmarker.view.swing.observers;

import com.purplecat.bookmarker.models.EFavoriteState;
import com.purplecat.bookmarker.models.EStoryState;
import com.purplecat.bookmarker.models.Place;

public interface IMediaItemEditor {
	void addCategory(String categoryName);
	
	void updatePlace(Place place);
	
	void updateStoryState(EStoryState state);
	
	void updateFavoriteState(EFavoriteState state);
}
