package com.purplecat.bookmarker.extensions;

import com.purplecat.bookmarker.models.Media;

public class MediaItemExt {
	
	public static String getPreferredUrl(Media item) {
		if ( item == null ) {
			return "";
		}
		else {
			return item.isUpdated() ? item._updatedUrl : item._chapterUrl;
		}
	}

}
