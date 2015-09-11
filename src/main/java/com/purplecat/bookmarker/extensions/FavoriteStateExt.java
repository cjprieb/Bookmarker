package com.purplecat.bookmarker.extensions;

import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.models.EFavoriteState;

public class FavoriteStateExt {

	public static EFavoriteState parse(int value) {
		for ( EFavoriteState item : EFavoriteState.values() ) {
			if ( item.getValue() == value ) {
				return(item);
			}
		}
		return(EFavoriteState.UNASSIGNED);
	}
	
	public static int getIconKey(EFavoriteState state) {
		int key = 0;
		if ( state != null ) {
			switch ( state ) {
				case MEH:			key = Resources.image.imgFavMehId;		break;
				case AWESOME:		key = Resources.image.imgFavStarId;		break;
				case GOOD:			key = Resources.image.imgFavGoodId;		break;
				case AVERAGE:		key = Resources.image.imgFavAverageId;	break;
				case UNASSIGNED:	break;
			}
		}
		return(key);
	}

}
