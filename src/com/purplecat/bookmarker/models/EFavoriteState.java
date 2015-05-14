package com.purplecat.bookmarker.models;


public enum EFavoriteState {	
	/*
	 * Lower numbers == better rating
	 * Unassigned is just below Average
	 */
	UNASSIGNED	(6),//Icons.BlankUtil, 	//0		
	AVERAGE		(5),//Icons.gAverage,	//1	
	GOOD		(3),//Icons.gGood,		//2
	AWESOME		(1),//Icons.gStar,		//3
	MEH			(7);//Icons.gMeh,		//4

	public int mRelativeValue;
	
	EFavoriteState(int v) {
		mRelativeValue = v;
	}
	
	public int getValue() { return(mRelativeValue); }
	
	/*public static class FavoriteComparor implements Comparator<EFavoriteState> {
		@Override
		public int compare(EFavoriteState s1, EFavoriteState s2) {
			return(Numbers.compareInt(s1.mRelativeValue, s2.mRelativeValue));
		}
	}

	public static String getFavoriteIconKey(EFavoriteState state, String key) {
		if ( state != null ) {
			switch ( state ) {
				case MEH:		key = AppImages.imgFavMehId;		break;
				case AWESOME:	key = AppImages.imgFavStarId;		break;
				case GOOD:		key = AppImages.imgFavGoodId;		break;
				case AVERAGE:	key = AppImages.imgFavAverageId;	break;
				case UNASSIGNED:	break;
			}
		}
		return(key);
	}	*/
	
	public static EFavoriteState[] MEDIA_STATE_ORDER = {
			EFavoriteState.UNASSIGNED,
			EFavoriteState.AVERAGE,	
			EFavoriteState.GOOD,
			EFavoriteState.AWESOME,	
//			EFavoriteState.MEH,				
	};
}
