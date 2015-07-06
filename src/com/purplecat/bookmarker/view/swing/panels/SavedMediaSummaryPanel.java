package com.purplecat.bookmarker.view.swing.panels;

import com.google.inject.Singleton;
import com.purplecat.bookmarker.models.Media;

@Singleton
public class SavedMediaSummaryPanel extends MediaSummaryPanel {
	public void update(Media view) {
		_dataTitle.setText(view._displayTitle);		
//		setAltTitles(view._altTitles);
		setUpdateColor(view._isSaved && view.isUpdated(), view.isRead());
//		setAuthor(view._author);
		if ( view.isUpdated() ) {
			setPlaceAndDateTime(view._lastReadPlace, view._lastReadDate, view._updatedPlace, view._updatedDate);
		}
		else {
			setPlaceAndDateTime(view._lastReadPlace, view._lastReadDate, null, null);			
		}
		setGenres(view._genres);
//		setCategories(view._categories);
		_dataRating.setVisible(false);
//		setType(view._type);
		setSummary(view._summary);
		setDescription(view._notes);
		setLink(_dataChapterLink, view._chapterUrl);
		_dataSiteLink._textControl.setVisible(false);
		setLink(_dataSiteLink, view._titleUrl);
		setLink(_dataUpdatedLink, view._updatedUrl);		
	}

}
