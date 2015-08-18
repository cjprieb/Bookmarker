package com.purplecat.bookmarker.view.swing.observers;

import com.purplecat.bookmarker.models.EFavoriteState;
import com.purplecat.bookmarker.models.EStoryState;
import com.purplecat.bookmarker.models.Place;

public interface ISummaryPanelListener {
	void notifyCategoryChanged(String categoryName);
	
	void notifyPlaceChanged(Place place);
	
	void notifyStoryStateChanged(EStoryState state);
	
	void notifyFavoriteStateChanged(EFavoriteState state);
}
