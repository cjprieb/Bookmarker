package com.purplecat.bookmarker.services.databases;

public class MediaBookmarkContract {
	public static final int STARTING_VERSION 			= 1;
	public static final int ADD_INVALID_TABLE_VERSION 	= 2;	
	public static final int ADD_STRINGS_TABLE_VERSION 	= 3;	
	public static final int ADD_AUTHORS_TABLE_VERSION 	= 4;	
	public static final int ADD_PLACE_COLUMNS_VERSION 	= 5;	
	public static final int ADD_UPDATE_VIEWS 			= 6;
	public static final int UPDATE_STORY_STATE			= 7;
	public static final int ADD_SCAN_SITE_TABLE			= 8;
	public static final int ADD_HISTORY_ROW				= 9;
//	public static final int ADD_UPDATE_ROW				= 10;
	public static final int ADD_URL_PATTERNS_TABLE		= 11;
	
	public static final int DATABASE_VERSION = ADD_URL_PATTERNS_TABLE;
	
	public static final String DATABASE_NAME = "bookmarker";
	public static final String FULL_DATABASE_NAME = DATABASE_NAME + ".db";
	
	public static final String TEXT_TYPE = " TEXT";
	public static final String INTEGER_TYPE = " INT";
	public static final String DEFAULT = " DEFAULT ";
	public static final String NOT_NULL = " NOT NULL";
	public static final String REFERENCES = " REFERENCES ";
	public static final String COMMA_SEP = ",\n\t";
	public static final String INT_PRIMARY_KEY = " INTEGER PRIMARY KEY ASC";
	public static final String FOREIGN_KEY = "FOREIGN KEY ";
	public static final String PRIMARY_KEY = "PRIMARY KEY ";
	
	public static final String SQL_SELECT_SAVED_MEDIA = "SELECT Media._id,MdDisplayTitle," + 
			"MdIsComplete,MdType,MdMainUrl,SvdIsSaved,SvdRating,SvdIsFlagged,SvdNotes," +
			"SvdHistory_ID,SvhstDate,SvhstUrl,SvhstVolume,SvhstChapter,SvhstSubChapter,SvhstPage,SvhstExtra," +
			"SvdFolder_ID, FldrName, FldrType, FldrIgnore, FldrImage," + 
			"SvdAuthor, AuthFullName," + 
			"UpOnline_ID, UpbkWebsiteName,UpbkDate,UpbkChapterUrl,UpbkTitleUrl,UpbkVolume,UpbkChapter,UpbkSubChapter,UpbkExtra,UpbkIsIgnored" + 
			" FROM Media" + 
			" LEFT JOIN SavedHistory sh on sh._id = Media.SvdHistory_ID" +  
			" LEFT JOIN Folder f on f._id = Media.SvdFolder_ID" + 
			" LEFT JOIN Author a on a._id = Media.SvdAuthor" +
			" LEFT JOIN UpdateBookmark ub on ub._id = Media.UpOnline_ID";
	
	public static final String SQL_SELECT_NON_SAVED_MEDIA = "SELECT Media._id,MdDisplayTitle," + 
			"MdIsComplete,MdType,MdMainUrl,SvdIsSaved,SvdRating,SvdIsFlagged,SvdNotes," +
			"SvdAuthor, AuthFullName," + 
			"UpOnline_ID, UpbkWebsiteName,UpbkDate,UpbkChapterUrl,UpbkTitleUrl,UpbkVolume,UpbkChapter,UpbkSubChapter,UpbkExtra" + 
			" FROM Media" + 
			" LEFT JOIN Author a on a._id = Media.SvdAuthor" +
			" LEFT JOIN UpdateBookmark ub on ub._id = Media.UpOnline_ID";
	
	public static final String SQL_SELECT_ONLINE_MEDIA = "SELECT UpdateBookmark._id," +
			"UpbkMedia_ID,UpbkRating,UpbkIsIgnored,UpbkNewlyAdded," + 
			"UpbkWebsiteName,UpbkDate,UpbkChapterUrl,UpbkTitleUrl,UpbkVolume,UpbkChapter,UpbkSubChapter,UpbkExtra," + 
			"MdDisplayTitle,MdIsComplete,MdType,MdMainUrl,SvdIsSaved,UpOnline_ID," +
			"SvdHistory_ID,SvhstDate,SvhstUrl,SvhstVolume,SvhstChapter,SvhstSubChapter,SvhstPage,SvhstExtra," +
			"SvdFolder_ID, FldrName, FldrType, FldrIgnore, FldrImage," + 
			"SvdAuthor,AuthFullName" + 
			" FROM UpdateBookmark" +
			" INNER JOIN Media m on m._id = UpbkMedia_ID" + 
			" LEFT JOIN SavedHistory sh on sh._id = m.SvdHistory_ID" +  
			" LEFT JOIN Folder f on f._id = m.SvdFolder_ID" + 
			" LEFT JOIN Author a on a._id = m.SvdAuthor";
	
	public static final String SQL_SELECT_MEDIA_BY_TITLE = "SELECT " + 
			"m._ID, m." + MediaTable.COLUMN_NAME_IS_SAVED + 
			" FROM " + TitleTable.TABLE_NAME + " INNER JOIN " +  MediaTable.TABLE_NAME + " m " + 
			" ON m._ID = " + TitleTable.COLUMN_NAME_MEDIA_ID + 
			" WHERE " + TitleTable.COLUMN_NAME_STRIPPED + " = ?";
	
	public interface BaseColumns {
		public static final String _COUNT = "_count";//Type: Integer
		public static final String _ID = "_id";//Type: Integer(long)
	}
			
	public static abstract class MediaTable implements BaseColumns {
		public static final String TABLE_NAME = "Media";
		public static final String LONG_ID = TABLE_NAME + "." + _ID;
		
		public static final String COLUMN_NAME_DISPLAY_TITLE = "MdDisplayTitle";
		public static final String COLUMN_NAME_IS_COMPLETE 	= "MdIsComplete";
		public static final String COLUMN_NAME_TYPE 		= "MdType";
//		public static final String COLUMN_NAME_SUMMARY 		= "MdSummary"; 
		public static final String COLUMN_NAME_MAIN_URL 	= "MdMainUrl";

		public static final String COLUMN_NAME_IS_SAVED 	= "SvdIsSaved";
//		public static final String COLUMN_NAME_STORY_STATE 	= "SvdStoryState";
		public static final String COLUMN_NAME_RATING 		= "SvdRating";
		public static final String COLUMN_NAME_IS_FLAGGED 	= "SvdIsFlagged";
		public static final String COLUMN_NAME_NOTES 		= "SvdNotes";
		public static final String COLUMN_NAME_FOLDER_ID 	= "SvdFolder_ID";
		public static final String COLUMN_NAME_AUTHOR	 	= "SvdAuthor";
		public static final String COLUMN_NAME_HISTORY_ID 	= "SvdHistory_ID";
		public static final String COLUMN_NAME_ONLINE_ID 	= "UpOnline_ID";
		
		public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + TABLE_NAME + " (" + 
			_ID + INT_PRIMARY_KEY + COMMA_SEP +
			COLUMN_NAME_DISPLAY_TITLE 	+ TEXT_TYPE + NOT_NULL + COMMA_SEP + 
			COLUMN_NAME_IS_COMPLETE 	+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP + 
			COLUMN_NAME_TYPE 			+ TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_MAIN_URL 		+ TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_IS_SAVED 		+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
//TODO:			COLUMN_NAME_RATING 			+ INTEGER_TYPE + DEFAULT + EFavoriteState.UNASSIGNED.getValue() + COMMA_SEP +		
			COLUMN_NAME_IS_FLAGGED 		+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			COLUMN_NAME_NOTES 			+ TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_FOLDER_ID 		+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			COLUMN_NAME_AUTHOR			+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP + 
			COLUMN_NAME_HISTORY_ID 		+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			COLUMN_NAME_ONLINE_ID 		+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			FOREIGN_KEY + " (" + COLUMN_NAME_FOLDER_ID + ")" + REFERENCES + FolderTable.TABLE_NAME + " (" + FolderTable._ID + ")" +
			FOREIGN_KEY + " (" + COLUMN_NAME_HISTORY_ID + ")" + REFERENCES + SavedHistoryTable.TABLE_NAME + " (" + SavedHistoryTable._ID + ")" +
			FOREIGN_KEY + " (" + COLUMN_NAME_ONLINE_ID + ")" + REFERENCES + UpdateBookmarkTable.TABLE_NAME + " (" + UpdateBookmarkTable._ID + ")" +
			" )";
		
		public static final String SQL_CREATE_TYPE_INDEX = 
			"CREATE INDEX Media_Type_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_TYPE +")";
		
		public static final String SQL_CREATE_FOLDER_INDEX = 
			"CREATE INDEX Svd_Folder_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_FOLDER_ID +")";
		
		public static final String SQL_ADD_HISTORY_ROW = 
				"ALTER TABLE " + TABLE_NAME + " ADD " + COLUMN_NAME_HISTORY_ID + INTEGER_TYPE + DEFAULT + "0";
		
		public static final String SQL_ADD_HISTORY_KEY = 
				"ALTER TABLE " + TABLE_NAME + " ADD " + 
				FOREIGN_KEY + " (" + COLUMN_NAME_HISTORY_ID + ")" + REFERENCES + SavedHistoryTable.TABLE_NAME + " (" + SavedHistoryTable._ID + ")";
		
		public static final String SQL_ADD_ONLINE_ROW = 
				"ALTER TABLE " + TABLE_NAME + " ADD " + COLUMN_NAME_ONLINE_ID + INTEGER_TYPE + DEFAULT + "0";
		
		public static final String SQL_ADD_ONLINE_KEY = 
				"ALTER TABLE " + TABLE_NAME + " ADD " + 
				FOREIGN_KEY + " (" + COLUMN_NAME_ONLINE_ID + ")" + REFERENCES + UpdateBookmarkTable.TABLE_NAME + " (" + UpdateBookmarkTable._ID + ")";
		
		private MediaTable() {}
	}	

	public static abstract class UpdateBookmarkTable implements BaseColumns {
		public static final String TABLE_NAME = "UpdateBookmark";
		public static final String LONG_ID = TABLE_NAME + "." + _ID;
		public static final String COLUMN_NAME_MEDIA_ID = "UpbkMedia_ID";	
		public static final String COLUMN_NAME_CHAPTER_URL = "UpbkChapterUrl";
		public static final String COLUMN_NAME_TITLE_URL = "UpbkTitleUrl";
		public static final String COLUMN_NAME_WEBSITE_NAME = "UpbkWebsiteName";
		public static final String COLUMN_NAME_DATE = "UpbkDate";
		public static final String COLUMN_NAME_RATING = "UpbkRating";
		public static final String COLUMN_NAME_IS_IGNORED = "UpbkIsIgnored";
		public static final String COLUMN_NAME_PLACE = "UpbkPlace"; 
		public static final String COLUMN_NAME_NEWLY_ADDED = "UpbkNewlyAdded";
		public static final String COLUMN_NAME_VOLUME = "UpbkVolume"; 
		public static final String COLUMN_NAME_CHAPTER = "UpbkChapter";
		public static final String COLUMN_NAME_SUB_CHAPTER = "UpbkSubChapter";
		public static final String COLUMN_NAME_EXTRA = "UpbkExtra";
		
		public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + TABLE_NAME + " (" + 
			_ID + INT_PRIMARY_KEY 	+ COMMA_SEP +
			COLUMN_NAME_MEDIA_ID 	+ INTEGER_TYPE + NOT_NULL + COMMA_SEP + 
			COLUMN_NAME_CHAPTER_URL + TEXT_TYPE + COMMA_SEP + 
			COLUMN_NAME_TITLE_URL 	+ TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_WEBSITE_NAME + TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_DATE 		+ TEXT_TYPE + NOT_NULL + COMMA_SEP +
			COLUMN_NAME_RATING 		+ INTEGER_TYPE + DEFAULT + "0.0" + COMMA_SEP +			
			COLUMN_NAME_IS_IGNORED 	+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			COLUMN_NAME_PLACE 		+ TEXT_TYPE + NOT_NULL + COMMA_SEP +		
			COLUMN_NAME_NEWLY_ADDED	+ INTEGER_TYPE + DEFAULT + "1" + COMMA_SEP +
			COLUMN_NAME_VOLUME	+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			COLUMN_NAME_CHAPTER	+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			COLUMN_NAME_SUB_CHAPTER	+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			COLUMN_NAME_EXTRA	+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			FOREIGN_KEY + " (" + COLUMN_NAME_MEDIA_ID + ")" + REFERENCES + MediaTable.TABLE_NAME + " (" + MediaTable._ID + ")" +
			" )";
		
		public static final String SQL_CREATE_MEDIA_INDEX = 
			"CREATE INDEX Up_Media_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_MEDIA_ID +")";
		
		public static final String SQL_CREATE_MEDIA_SITE_INDEX = 
			"CREATE INDEX Up_Media_Site_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_MEDIA_ID + "," + COLUMN_NAME_WEBSITE_NAME + ")";
		
		public static final String SQL_CREATE_SITE_INDEX = 
			"CREATE INDEX Up_Site_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_WEBSITE_NAME + ")";
		
		private UpdateBookmarkTable() {}
	}

	public static abstract class CategoryTable implements BaseColumns {
		public static final String TABLE_NAME = "Category";	
		public static final String COLUMN_NAME_NAME = "CatName";
		public static final String COLUMN_NAME_SEARCH = "CatSearch";
		
		public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + TABLE_NAME + " (" + 
			_ID + INT_PRIMARY_KEY 	+ COMMA_SEP +
			COLUMN_NAME_NAME 	+ TEXT_TYPE + NOT_NULL + COMMA_SEP + 
			COLUMN_NAME_SEARCH 	+ TEXT_TYPE + 
			" )";
		
		public static final String SQL_CREATE_NAME_INDEX = 
			"CREATE INDEX Cat_Name_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_NAME + ")";
		
		private CategoryTable() {}
	}

	public static abstract class GenreTable implements BaseColumns {
		public static final String TABLE_NAME = "Genre";	
		public static final String LONG_ID = TABLE_NAME + "." + _ID;
		public static final String COLUMN_NAME_NAME = "GenName";
		public static final String COLUMN_NAME_ALT_NAME = "GenAltName";
		public static final String COLUMN_NAME_INCLUDE = "GenInclude";
		public static final String COLUMN_NAME_IMAGE = "GenImage";
		
		public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + TABLE_NAME + " (" + 
			_ID + INT_PRIMARY_KEY 	+ COMMA_SEP +
			COLUMN_NAME_NAME 		+ TEXT_TYPE + NOT_NULL + COMMA_SEP + 
			COLUMN_NAME_ALT_NAME	+ TEXT_TYPE + COMMA_SEP + 
			COLUMN_NAME_INCLUDE 	+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			COLUMN_NAME_IMAGE 		+ TEXT_TYPE + 
			" )";
		
		private GenreTable() {}
		
		public static final String SQL_CREATE_NAME_INDEX = 
			"CREATE INDEX Gen_Name_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_NAME + ")";
	}

	public static abstract class TitleTable implements BaseColumns {
		public static final String TABLE_NAME = "Title";	
		public static final String COLUMN_NAME_MEDIA_ID = "TtMedia_ID";
		public static final String COLUMN_NAME_TITLE = "TtTitle";
		public static final String COLUMN_NAME_STRIPPED = "TtStripped";
		
		public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + TABLE_NAME + " (" + 
			_ID + INT_PRIMARY_KEY 	+ COMMA_SEP +
			COLUMN_NAME_MEDIA_ID 	+ INTEGER_TYPE + NOT_NULL + COMMA_SEP + 
			COLUMN_NAME_TITLE 		+ TEXT_TYPE + NOT_NULL + COMMA_SEP +
			COLUMN_NAME_STRIPPED 	+ TEXT_TYPE + NOT_NULL + COMMA_SEP +	
			FOREIGN_KEY + " (" + COLUMN_NAME_MEDIA_ID + ")" + REFERENCES + MediaTable.TABLE_NAME + " (" + MediaTable._ID + ")" + 
			" )";
		
		public static final String SQL_CREATE_TITLE_STRIPPED_INDEX = 
			"CREATE INDEX Title_Stripped_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_STRIPPED +")";
		
		public static final String SQL_CREATE_TITLE_MEDIA_INDEX = 
			"CREATE INDEX Title_Media_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_MEDIA_ID +")";
		
		public static final String SQL_CREATE_TITLE_NAME_INDEX = 
			"CREATE INDEX Title_Name_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_TITLE +")";
				
		private TitleTable() {}
	}

	public static abstract class CategoryMapTable {
		public static final String TABLE_NAME = "CategoryMap";	
		public static final String COLUMN_NAME_MEDIA_ID = "CatMedia_ID";
		public static final String COLUMN_NAME_CAT_ID = "CatCategory_ID";
		
		public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + TABLE_NAME + " (" +
			COLUMN_NAME_MEDIA_ID 	+ INTEGER_TYPE + NOT_NULL + COMMA_SEP + 
			COLUMN_NAME_CAT_ID 		+ INTEGER_TYPE + NOT_NULL + COMMA_SEP +
			PRIMARY_KEY + "(" + COLUMN_NAME_MEDIA_ID + ", " + COLUMN_NAME_CAT_ID + ")" + COMMA_SEP + 
			FOREIGN_KEY + " (" + COLUMN_NAME_MEDIA_ID + ")" + REFERENCES + MediaTable.TABLE_NAME + " (" + MediaTable._ID + ")" + COMMA_SEP +
			FOREIGN_KEY + " (" + COLUMN_NAME_CAT_ID + ")" + REFERENCES + CategoryTable.TABLE_NAME + " (" + CategoryTable._ID + ")" + 
			" )";
		
		public static final String SQL_CREATE_MEDIA_INDEX = 
			"CREATE INDEX CatMap_Media_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_MEDIA_ID + ")";
		
		public static final String SQL_CREATE_CAT_INDEX = 
			"CREATE INDEX CatMap_ID_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_CAT_ID + ")";
		
		public static final String SQL_SELECT_BY_MEDIA_ID =
			"SELECT " + COLUMN_NAME_CAT_ID + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_MEDIA_ID + " = ?";				
				
		private CategoryMapTable() {}
	}

	public static abstract class GenreMapTable {
		public static final String TABLE_NAME = "GenreMap";	
		public static final String COLUMN_NAME_MEDIA_ID = "GenMedia_ID";
		public static final String COLUMN_NAME_GENRE_ID = "GenGenre_ID";		
		
		public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + TABLE_NAME + " (" +
			COLUMN_NAME_MEDIA_ID 	+ INTEGER_TYPE + NOT_NULL + COMMA_SEP + 
			COLUMN_NAME_GENRE_ID 		+ INTEGER_TYPE + NOT_NULL + COMMA_SEP +
			PRIMARY_KEY + "(" + COLUMN_NAME_MEDIA_ID + ", " + COLUMN_NAME_GENRE_ID + ")" + COMMA_SEP + 
			FOREIGN_KEY + " (" + COLUMN_NAME_MEDIA_ID + ")" + REFERENCES + MediaTable.TABLE_NAME + " (" + MediaTable._ID + ")" + COMMA_SEP +
			FOREIGN_KEY + " (" + COLUMN_NAME_GENRE_ID + ")" + REFERENCES + GenreTable.TABLE_NAME + " (" + GenreTable._ID + ")" + 
			" )";
		
		public static final String SQL_CREATE_MEDIA_INDEX = 
			"CREATE INDEX GenMap_Media_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_MEDIA_ID +")";
		
		public static final String SQL_SELECT_BY_MEDIA_ID =
			"SELECT " + COLUMN_NAME_GENRE_ID + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NAME_MEDIA_ID + " = ?";
				
		private GenreMapTable() {}
	}

	public static abstract class SavedHistoryTable implements BaseColumns {
		public static final String TABLE_NAME = "SavedHistory";	
		public static final String LONG_ID = TABLE_NAME + "." + _ID;	
		public static final String COLUMN_NAME_MEDIA_ID = "SvhstMedia_ID";	
		public static final String COLUMN_NAME_DATE = "SvhstDate";		
		public static final String COLUMN_NAME_PLACE = "SvhstPlace";		
		public static final String COLUMN_NAME_URL= "SvhstUrl";
		public static final String COLUMN_NAME_VOLUME = "SvhstVolume"; 
		public static final String COLUMN_NAME_CHAPTER = "SvhstChapter";
		public static final String COLUMN_NAME_SUB_CHAPTER = "SvhstSubChapter";
		public static final String COLUMN_NAME_PAGE = "SvhstPage";
		public static final String COLUMN_NAME_EXTRA = "SvhstExtra";
		
		public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + TABLE_NAME + " (" +
			_ID + INT_PRIMARY_KEY 	+ COMMA_SEP +
			COLUMN_NAME_MEDIA_ID 	+ INTEGER_TYPE + NOT_NULL + COMMA_SEP + 
			COLUMN_NAME_DATE 		+ TEXT_TYPE + NOT_NULL + COMMA_SEP +
			COLUMN_NAME_PLACE 		+ TEXT_TYPE + NOT_NULL + COMMA_SEP +
			COLUMN_NAME_URL 		+ TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_VOLUME	+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			COLUMN_NAME_CHAPTER	+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			COLUMN_NAME_SUB_CHAPTER	+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			COLUMN_NAME_PAGE + INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			COLUMN_NAME_EXTRA	+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			FOREIGN_KEY + " (" + COLUMN_NAME_MEDIA_ID + ")" + REFERENCES + MediaTable.TABLE_NAME + " (" + MediaTable._ID + ")" +
			" )";
		
		public static final String SQL_CREATE_HISTORY_INDEX = 
			"CREATE INDEX His_Date_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_MEDIA_ID + ", " + COLUMN_NAME_DATE + " DESC)";
		
		public static final String SQL_CREATE_MEDIA_INDEX = 
			"CREATE INDEX His_Media_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_MEDIA_ID + ")";
			
		public static final String SQL_SELECT_MOST_RECENT = "SELECT hst1.* FROM " + TABLE_NAME + " as hst1" + 
				" INNER JOIN (SELECT MAX(" + COLUMN_NAME_DATE + ") as maxdate, " + COLUMN_NAME_MEDIA_ID + " FROM " + TABLE_NAME +
				" GROUP BY " + COLUMN_NAME_MEDIA_ID + ") as hst2 ON hst2." + COLUMN_NAME_MEDIA_ID + " = hst1." + COLUMN_NAME_MEDIA_ID +
				" AND hst1." + COLUMN_NAME_DATE + " = maxdate";
				
		private SavedHistoryTable() {}
	}

	public static abstract class FolderTable implements BaseColumns {		
		public static final String TABLE_NAME = "Folder";		
		public static final String LONG_ID = TABLE_NAME + "." + _ID;
		public static final String COLUMN_NAME_NAME = "FldrName";
		public static final String COLUMN_NAME_TYPE = "FldrType";
		public static final String COLUMN_NAME_IMAGE = "FldrImage";
		public static final String COLUMN_NAME_IGNORE= "FldrIgnore";
		
		public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + TABLE_NAME + " (" +
			_ID + INT_PRIMARY_KEY 	+ COMMA_SEP +
			COLUMN_NAME_NAME 	+ TEXT_TYPE + NOT_NULL + COMMA_SEP +
			COLUMN_NAME_TYPE 	+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			COLUMN_NAME_IGNORE 	+ INTEGER_TYPE + DEFAULT + "0" + COMMA_SEP +
			COLUMN_NAME_IMAGE 	+ TEXT_TYPE +
			" )";
		
		public static final String SQL_CREATE_NAME_INDEX = 
			"CREATE INDEX Folder_Name_Index ON " + TABLE_NAME + "(" + COLUMN_NAME_NAME + ")";
		
		public static final String SQL_ADD_STORY_COLUMN = 
			"ALTER TABLE " + TABLE_NAME + " ADD " + COLUMN_NAME_TYPE + INTEGER_TYPE + DEFAULT + "0";		
		
		public static final String SQL_ADD_IGNORE_COLUMN = 
			"ALTER TABLE " + TABLE_NAME + " ADD " + COLUMN_NAME_IGNORE + INTEGER_TYPE + DEFAULT + "0";	
		
		public static final String SQL_ADD_IMAGE_COLUMN = 
			"ALTER TABLE " + TABLE_NAME + " ADD " + COLUMN_NAME_IMAGE + TEXT_TYPE;		
		
				
		private FolderTable() {}
	}
	
	public static abstract class InvalidItemsTable implements BaseColumns {
		public static final String TABLE_NAME 		= "InvalidItems";		
		public static final String COLUMN_NAME_NAME = "InvalidName";	
		public static final String COLUMN_NAME_DATE = "InvalidDateAdded";
		
		public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + TABLE_NAME + " (" +
			_ID + INT_PRIMARY_KEY 	+ COMMA_SEP +
			COLUMN_NAME_NAME 	+ TEXT_TYPE + NOT_NULL + COMMA_SEP +
			COLUMN_NAME_DATE 		+ TEXT_TYPE + NOT_NULL + 
			" )";
				
		private InvalidItemsTable() {}
	}
	
	public static abstract class StringsTable implements BaseColumns {
		public static final String TABLE_NAME 			= "Strings";		
		public static final String COLUMN_NAME_TYPE 	= "StrType";	
		public static final String COLUMN_NAME_ID 		= "StrId";	
		public static final String COLUMN_NAME_VALUE 	= "StrValue";
		
		public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + TABLE_NAME + " (" +
			_ID + INT_PRIMARY_KEY 	+ COMMA_SEP +
			COLUMN_NAME_TYPE 	+ TEXT_TYPE + NOT_NULL + COMMA_SEP +
			COLUMN_NAME_ID 	+ INTEGER_TYPE + NOT_NULL + COMMA_SEP +
			COLUMN_NAME_VALUE 		+ TEXT_TYPE + NOT_NULL + 
			" )";
				
		private StringsTable() {}
	}

	public static abstract class AuthorTable implements BaseColumns {
		public static final String TABLE_NAME = "Author";	
		public static final String LONG_ID = TABLE_NAME + "." + _ID;
		public static final String COLUMN_NAME_NAME = "AuthFullName";
		
		public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + TABLE_NAME + " (" + 
			_ID + INT_PRIMARY_KEY 	+ COMMA_SEP +
			COLUMN_NAME_NAME 	+ TEXT_TYPE + NOT_NULL +
			" )";
		
		private AuthorTable() {}
	}

	public static abstract class ScanSiteTable implements BaseColumns {
		public static final String TABLE_NAME = "ScanSite";	
		public static final String LONG_ID = TABLE_NAME + "." + _ID;
		public static final String COLUMN_NAME_NAME = "ScanName";
		public static final String COLUMN_NAME_URL= "ScanUrl";
		
		public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + TABLE_NAME + " (" + 
			_ID + INT_PRIMARY_KEY 	+ COMMA_SEP +
			COLUMN_NAME_NAME 	+ TEXT_TYPE + COMMA_SEP +
			COLUMN_NAME_URL 	+ TEXT_TYPE + " )";
		
		private ScanSiteTable() {}
	}

	public static abstract class UrlPatternsTable implements BaseColumns {
		public static final String TABLE_NAME = "UrlPattern";	
		public static final String LONG_ID = TABLE_NAME + "." + _ID;
		public static final String COLUMN_NAME_PATTERN = "PatPattern";
		public static final String COLUMN_NAME_GROUPS = "PatGroups";
		public static final String COLUMN_NAME_TYPE	= "PatType";
		
		public static final String SQL_CREATE_ENTRIES = 
			"CREATE TABLE " + TABLE_NAME + " (" + 
			_ID + INT_PRIMARY_KEY 	+ COMMA_SEP +
			COLUMN_NAME_PATTERN 	+ TEXT_TYPE + NOT_NULL + COMMA_SEP + 
			COLUMN_NAME_GROUPS 		+ TEXT_TYPE + COMMA_SEP + 
			COLUMN_NAME_TYPE 		+ TEXT_TYPE + 
			" )";
		
		private UrlPatternsTable() {}
	}
}
