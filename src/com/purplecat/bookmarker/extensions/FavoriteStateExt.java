package com.purplecat.bookmarker.extensions;

import com.purplecat.bookmarker.models.EFavoriteState;
import com.purplecat.bookmarker.view.swing.BookmarkerImages;

public class FavoriteStateExt {

	public static EFavoriteState parse(int value) {
		for ( EFavoriteState item : EFavoriteState.values() ) {
			if ( item.getValue() == value ) {
				return(item);
			}
		}
		return(EFavoriteState.UNASSIGNED);
	}
	
	public static String getIconKey(EFavoriteState state) {
		String key = null;
		if ( state != null ) {
			switch ( state ) {
				case MEH:			key = BookmarkerImages.imgFavMehId;			break;
				case AWESOME:		key = BookmarkerImages.imgFavStarId;		break;
				case GOOD:			key = BookmarkerImages.imgFavGoodId;		break;
				case AVERAGE:		key = BookmarkerImages.imgFavAverageId;		break;
				case UNASSIGNED:	break;
			}
		}
		return(key);
	}

}
