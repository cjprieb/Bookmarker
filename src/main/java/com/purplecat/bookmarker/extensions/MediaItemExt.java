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
	
	/***
	 * This is a quick fix to prevent media items in certain
	 * folders from being marked as updated. As folders will
	 * eventually be editable, this method is marked as deprecated.
	 * @param folderId
	 * @return
	 */
	@Deprecated
	public static boolean isIgnored(long folderId) { 
		return folderId == 1 || folderId == 2 || folderId == 4;
	}

}
