package com.purplecat.bookmarker.view.swing.ignored;

import java.util.Calendar;

import com.purplecat.commons.swing.EnablableTableCellRenderer;
import com.purplecat.bookmarks.model.EBookmarkType;
import com.purplecat.bookmarks.model.Place;
import com.purplecat.bookmarks.model.ScheduledItem;
import com.purplecat.bookmarks.model.styles.MovieFormatter;
import com.purplecat.bookmarks.model.styles.NextDateFormatter;
import com.purplecat.bookmarks.model.styles.PlaceFormatter;

public class NextItemRenderer extends EnablableTableCellRenderer {
	
	public NextItemRenderer() { 
		super(); 
	}
	
	@Override
	public void setValue(Object value) {
		String s = "";
		if ( value != null && value instanceof Calendar ) {
			s = NextDateFormatter.dateToString((Calendar)value, false);
		}
		else if ( value != null && value instanceof ScheduledItem ) {
			ScheduledItem item = (ScheduledItem)value;
			Calendar c = item.getNextDate();
			if ( c != null && Calendar.getInstance().before(c) ) {
				s = NextDateFormatter.dateToString(c, false);
			}
			else {
				Place nextPlace = item.getNextPlace();
				Place currPlace = item.getPlace();
				if ( nextPlace != null && currPlace != null && nextPlace.compareTo(currPlace) > 0 ) {
					if ( nextPlace.getChapter() == (currPlace.getChapter()+1) ) {
						s = "available";
					}
					else if ( currPlace.isEmpty() ) {
						s = "new";
					}
					else if ( nextPlace.getVolume() == currPlace.getVolume() ) {
						int diff = nextPlace.getChapter() - currPlace.getChapter();
						s = String.format("%d episodes", diff);
					}
					else if ( item.getType() == EBookmarkType.MOVIE ) {
						s = MovieFormatter.placeToString(nextPlace);					
					}
					else {
						s = PlaceFormatter.placeToString(nextPlace);					
					}
				}
			}
		}
		else {
			s = (value != null ? value.toString() : "");
		}
		setText(s);
	}

}
