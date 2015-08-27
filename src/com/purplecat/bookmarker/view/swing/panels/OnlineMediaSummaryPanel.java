package com.purplecat.bookmarker.view.swing.panels;

import com.google.inject.Singleton;
import com.purplecat.bookmarker.models.OnlineMediaItem;

@Singleton
public class OnlineMediaSummaryPanel extends MediaSummaryPanel {
	public void update(OnlineMediaItem view) {
		if ( view == null ) {
			throw new NullPointerException("OnlineMediaItem cannot be null");
		}
		_dataTitle.setText(view._displayTitle);	
		_dataAltTitles.setVisible(false);
		setUpdateColor(view._isSaved && view.isUpdated(), view.isRead());
//		setAuthor(view._author);
		setPlaceAndDateTime(view._lastReadPlace, view._lastReadDate, view._updatedPlace, view._updatedDate, view._isSaved);
		setGenres(view._genres);
		_dataCategories.setVisible(false);
		_dataDescription.setVisible(false);
		_dataRating.setVisible(true);
		setRating(view._rating);
//		setType(view._type);
		setSummary(view._summary);
		setLink(_dataChapterLink, view._lastReadUrl);
		setLink(_dataSiteLink, view._titleUrl);
		setLink(_dataUpdatedLink, view._chapterUrl);
	}

}
