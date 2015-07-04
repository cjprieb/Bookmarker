package com.purplecat.bookmarker.view.swing.panels;

import com.google.inject.Singleton;
import com.purplecat.bookmarker.models.OnlineMediaItem;

@Singleton
public class OnlineMediaSummaryPanel extends MediaSummaryPanel {
	public void update(OnlineMediaItem view) {
		_dataTitle.setText(view._displayTitle);		
//		setAltTitles(view._altTitles);
		setUpdateColor(view._isSaved && view.isUpdated(), view.isRead());
//		setAuthor(view._author);
		setPlaceAndDateTime(view._lastReadPlace, view._lastReadDate, view._updatedPlace, view._updatedDate);
		setGenres(view._genres);
//		setCategories(view._categories);
		_dataRating.setVisible(true);
		setRating(view._rating);
//		setType(view._type);
		setSummary(view._summary);
		setLink(_dataChapterLink, "");
		setLink(_dataSiteLink, view._titleUrl);
		setLink(_dataUpdatedLink, view._chapterUrl);
	}

}
