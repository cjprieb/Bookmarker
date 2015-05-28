package com.purplecat.bookmarker.view.swing.renderers;

import org.joda.time.DateTime;

import com.purplecat.bookmarker.Resources;
import com.purplecat.bookmarker.models.EFavoriteState;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.Place;
import com.purplecat.bookmarker.view.swing.BookmarkerImages;
import com.purplecat.commons.TTableColumn;

public class DataFields {
//	public static final TTableColumn TITLE_COL 		= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblTitle, 	String.class,			"mmmmmmmmmmmmmmmmmmm");
//	public static final TTableColumn DATE_COL 		= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblDate, 	Calendar.class,			Calendar.getInstance());
//	public static final TTableColumn TIME_COL 		= new TTableColumn(BookmarkOptions.APP_NAME, com.purplecat.commons.Resources.string.lblTime, 	Calendar.class,			Calendar.getInstance());
//	public static final TTableColumn NEXT_TIME_COL 	= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblNextInstallment, 	ScheduledItem.class,			Calendar.getInstance());
//	public static final TTableColumn TYPE_COL 		= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblType, 	EBookmarkType.class,	EBookmarkType.MOVIE);	
//	public static final TTableColumn FAVORITE_COL	= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblFavorite, 				EFavoriteState.class,	EFavoriteState.AVERAGE);	
//	public static final TTableColumn SITE_COL 		= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblSite, 	String.class,			"mmmmmmmmmmm");
//	public static final TTableColumn FOLDER_COL 	= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblFolder, 	String.class,			"mmmmmmmmmmm");
//	public static final TTableColumn CATEGORY_COL 	= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblCategory, String.class,			"mmmmmmmmmmm");
//	public static final TTableColumn PLACE_COL 		= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblPlace, 	Place.class,			new Place(10, 100, 10, 0));
//	public static final TTableColumn SHORT_PLACE_COL = new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblPlace, 	Place.class,			new Place(9, 99));
//	public static final TTableColumn GENRES_COL		= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblGenre, 	GenreList.class,		"");	
//	public static final TTableColumn URL_COL		= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblChapterUrl, 	String.class,			"mmmmmmmmmmmmmmmmmmmmm");
//	public static final TTableColumn FLAG_COL		= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblFlagged, 				Boolean.class,	true);	
//	public static final TTableColumn IGNORE_COL		= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblIgnore, 	Boolean.class,	true);	
//	public static final TTableColumn RATING_COL		= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblRating, 	Double.class,	(Double)5.00);
//	public static final TTableColumn MEDIA_STATE_COL	= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblStatus,	Media.class, Images.get(AppImages.imgOpenBookId));
//	public static final TTableColumn ONLINE_STATE_COL	= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblStatus,	UpdateOnlineBookmark.class, Images.get(AppImages.imgOpenBookId));
//	public static final TTableColumn SCHEDULED_STATE_COL= new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblStatus,	ScheduledItem.class, Images.get(AppImages.imgOpenBookId));
//	public static final TTableColumn NEXT_EPISODE_COL = new TTableColumn(BookmarkOptions.APP_NAME, Resources.string.lblNextInstallment,	ScheduledItem.class, "");
//	public static final TTableColumn WATCHED_COL	= new TTableColumn(BookmarkOptions.APP_NAME, "Watched", 	Boolean.class, true);	

	public static final TTableColumn DATE_COL 		= new TTableColumn(Resources.string.lblDate, 		DateTime.class,			DateTime.now());
	public static final TTableColumn FAVORITE_COL	= new TTableColumn(Resources.string.lblFavorite, 	EFavoriteState.class,	BookmarkerImages.imgFavAverageId, true); //Is an image column
	public static final TTableColumn FLAG_COL		= new TTableColumn(Resources.string.lblFlagged, 	Boolean.class,			BookmarkerImages.imgBlankFlagId, true);	 //Is an image column
	public static final TTableColumn MEDIA_STATE_COL= new TTableColumn(Resources.string.lblStatus, 		Media.class, 			BookmarkerImages.imgOpenBookId, true); //Is an image column
	public static final TTableColumn PLACE_COL 		= new TTableColumn(Resources.string.lblPlace,		Place.class,			new Place(10, 100, 10, 0, false));
	public static final TTableColumn TIME_COL 		= new TTableColumn(Resources.string.lblTime, 		DateTime.class,			DateTime.now());
	public static final TTableColumn TITLE_COL 		= new TTableColumn(Resources.string.lblTitle,		String.class,			"mmmmmmmmmmmmmmmmmmm");
	public static final TTableColumn RATING_COL		= new TTableColumn(Resources.string.lblRating, 		Double.class,			(Double)5.00);
	public static final TTableColumn ONLINE_STATE_COL= new TTableColumn(Resources.string.lblStatus, 	Media.class, 			BookmarkerImages.imgOpenBookId, true); //Is an image column

}
