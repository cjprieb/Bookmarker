package com.purplecat.bookmarker.extensions;

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

}
