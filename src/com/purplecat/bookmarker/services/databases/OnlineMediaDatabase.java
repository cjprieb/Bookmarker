package com.purplecat.bookmarker.services.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.purplecat.bookmarker.extensions.OnlineMediaItemExt;
import com.purplecat.bookmarker.extensions.PlaceExt;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.sql.NamedResultSet;
import com.purplecat.bookmarker.sql.NamedStatement;
import com.purplecat.commons.logs.ILoggingService;

public class OnlineMediaDatabase implements IOnlineMediaRepository {
	public static final String TAG = "MangaDatabaseConnector";
	
	private static final String SELECT_ITEMS = "SELECT UpdateBookmark._id, UpbkMedia_id _mediaId, UpbkChapterUrl _chapterUrl, UpbkTitleUrl _titleUrl, UpbkWebsiteName _websiteName,"
			+ " UpbkIsIgnored _isIgnored, UpbkDate _updatedDate, UpbkRating _rating, UpbkPlace _updatedPlace, UpbkNewlyAdded _newlyAdded,"
			+ " MdDisplayTitle _displayTitle, SvdIsSaved _isSaved, SvhstDate _lastReadDate, SvhstPlace _lastReadPlace"
			+ " FROM UpdateBookmark INNER JOIN Media on Media._id = UpbkMedia_id"
			+ " LEFT JOIN SavedHistory on SavedHistory._id = SvdHistory_ID";
	
		
	
	public final ILoggingService _logging;
	public final String _connectionPath;
	public final MediaDatabaseRepository _mediaDatabase;
	 
	@Inject
	public OnlineMediaDatabase(ILoggingService logger, @Named("JDBC URL") String dbPath, MediaDatabaseRepository mediaDb) {
		_logging = logger;
		_connectionPath = dbPath;
		_mediaDatabase = mediaDb;
	}
	
	/**
	 * Assumes the SELECT_MEDIA query was used
	 * @param result
	 * @return
	 * @throws SQLException 
	 */
	private OnlineMediaItem loadOnlineMediaFromResultSet(NamedResultSet result) throws SQLException {
		OnlineMediaItem item = new OnlineMediaItem();
		item._id = result.getLong("_id");
		item._mediaId = result.getLong("_mediaId");
		item._titleUrl = result.getString("_titleUrl");
		item._chapterUrl = result.getString("_chapterUrl");
		item._newlyAdded = result.getBoolean("_newlyAdded");
		item._isIgnored = result.getBoolean("_isIgnored");
		item._isSaved = result.getBoolean("_isSaved");
		item._rating = result.getDouble("_rating");
		item._websiteName = result.getString("_websiteName");
		item._displayTitle = result.getString("_displayTitle");
		item._updatedDate = result.getDateFromString("_updatedDate");
		item._updatedPlace = PlaceExt.parse(result.getString("_updatedPlace"));
		item._lastReadDate = result.getDateFromString("_lastReadDate");
		item._lastReadPlace = PlaceExt.parse(result.getString("_lastReadPlace"));
		return item;
	}

	@Override
	public List<OnlineMediaItem> query() {
		List<OnlineMediaItem> list = new LinkedList<OnlineMediaItem>();
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			Statement stmt = conn.createStatement();
			NamedResultSet result = new NamedResultSet(stmt.executeQuery(SELECT_ITEMS));
			while ( result.next() ) {
				list.add(loadOnlineMediaFromResultSet(result));
			}
		} catch (SQLException e) {
			_logging.error(TAG, "Query failed", e);
		}
		return list;
	}

	@Override
	public OnlineMediaItem queryById(long id) {
		OnlineMediaItem item = null;
		String sql = SELECT_ITEMS + " WHERE UpdateBookmark._id = @id";
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setLong("@id", id);
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				item = loadOnlineMediaFromResultSet(result);
				break;
			}
		} catch (SQLException e) {
			_logging.error(TAG, "Query for id failed: " + sql, e);
		} 
		return item;
	}

	@Override
	public OnlineMediaItem findOrCreate(OnlineMediaItem item) {
		OnlineMediaItem result = null;
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			List<Media> matches = _mediaDatabase.queryByTitle(conn, item._displayTitle);
			if ( matches.size() > 0 ) {
				//TODO: don't assume first is best title match
				item._mediaId = matches.get(0)._id;
				result = findExistingOnlineItem(conn, item);
				if ( result != null ) {
					OnlineMediaItemExt.copyNewToExisting(item, result);
				} 
			}
			
			conn.setAutoCommit(false);
			
			if (matches.size() == 0) {
				//Insert new Media item
				item._mediaId = createMedia(conn, item);
			}
			
			if ( result != null ) {
				update(conn, result);
			}
			else {
				insert(conn, item);
				if ( item._mediaId > 0 ) {
					//matching media was found, but not matching online media, 
					// so reload after insert to load media-specific fields
					result = findExistingOnlineItem(conn, item);
				}
				else {
					result = item;
				}
			}
			
			conn.commit();
		} catch (SQLException e) {
			_logging.error(TAG, "findOrCreate failed", e);
		} 
		return result;
	}
	
	private OnlineMediaItem findExistingOnlineItem(Connection conn, OnlineMediaItem item) throws SQLException {
		OnlineMediaItem existing = null;
		NamedStatement stmt = new NamedStatement(conn, SELECT_ITEMS + " WHERE UpbkMedia_ID = @mediaId AND UpbkWebsiteName = @website");
		stmt.setLong("@mediaId", item._mediaId);
		stmt.setString("@website", item._websiteName);
		NamedResultSet result = stmt.executeQuery();
		while ( result.next() ) {
			existing = loadOnlineMediaFromResultSet(result);
			break;
		}
		return existing;
	}
	
	private long createMedia(Connection conn, OnlineMediaItem item) throws SQLException {
		String sql = "INSERT INTO Media (MdDisplayTitle, SvdIsSaved) VALUES (@title, @isSaved)";
		NamedStatement stmt = new NamedStatement(conn, sql);
		stmt.setString("@title", item._displayTitle);
		stmt.setBoolean("@isSaved", item._isSaved);
		return stmt.executeInsert();
	}

	@Override
	public void insert(OnlineMediaItem item) {
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			insert(conn, item);
		} catch (SQLException e) {
			_logging.error(TAG, "Insert failed", e);
		} 
	}

	public void insert(Connection conn, OnlineMediaItem item) throws SQLException {
		String sql = "INSERT INTO UpdateBookmark (UpbkMedia_ID, UpbkChapterUrl, UpbkTitleUrl, UpbkWebsiteName, UpbkDate, UpbkRating, UpbkIsIgnored, "
				+ " UpbkPlace, UpbkNewlyAdded, UpbkVolume, UpbkChapter, UpbkSubChapter, UpbkExtra) "
				+ " VALUES (@mediaId, @chapterUrl, @titleUrl, @websiteName, @date, @rating, @isIgnored, "
				+ " @place, @newlyAdded, @volume, @chapter, @sub, @extra)";
		NamedStatement stmt = new NamedStatement(conn, sql);
		stmt.setLong("@mediaId", item._mediaId);
		stmt.setString("@chapterUrl", item._chapterUrl);
		stmt.setString("@titleUrl", item._titleUrl);
		stmt.setString("@websiteName", item._websiteName);
		stmt.setDate("@date", item._updatedDate);
		stmt.setDouble("@rating", item._rating);
		stmt.setBoolean("@isIgnored", item._isIgnored);
		stmt.setString("@place", PlaceExt.format(item._updatedPlace));
		stmt.setBoolean("@newlyAdded", item._newlyAdded);
		stmt.setInt("@volume", item._updatedPlace._volume);
		stmt.setInt("@chapter", item._updatedPlace._chapter);
		stmt.setInt("@sub", item._updatedPlace._subChapter);
		stmt.setBoolean("@extra", item._updatedPlace._extra);
		item._id = stmt.executeInsert();
	}

	@Override
	public void update(OnlineMediaItem item) {
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			update(conn, item);
		} catch (SQLException e) {
			_logging.error(TAG, "Update failed", e);
		} 
	}

	public void update(Connection conn, OnlineMediaItem item) throws SQLException {
		String sql = "UPDATE UpdateBookmark SET UpbkMedia_ID=@mediaId, UpbkChapterUrl=@chapterUrl, UpbkTitleUrl=@titleUrl, UpbkWebsiteName=@websiteName, "
				+ " UpbkDate=@date, UpbkRating=@rating, UpbkIsIgnored=@isIgnored, "
				+ " UpbkPlace=@place, UpbkNewlyAdded=@newlyAdded, UpbkVolume=@volume, UpbkChapter=@chapter, UpbkSubChapter=@sub, UpbkExtra=@extra"
				+ " WHERE _id = @id";
		NamedStatement stmt = new NamedStatement(conn, sql);
		stmt.setLong("@id", item._id);
		stmt.setLong("@mediaId", item._mediaId);
		stmt.setString("@chapterUrl", item._chapterUrl);
		stmt.setString("@titleUrl", item._titleUrl);
		stmt.setString("@websiteName", item._websiteName);
		stmt.setDate("@date", item._updatedDate);
		stmt.setDouble("@rating", item._rating);
		stmt.setBoolean("@isIgnored", item._isIgnored);
		stmt.setString("@place", PlaceExt.format(item._updatedPlace));
		stmt.setBoolean("@newlyAdded", item._newlyAdded);
		stmt.setInt("@volume", item._updatedPlace._volume);
		stmt.setInt("@chapter", item._updatedPlace._chapter);
		stmt.setInt("@sub", item._updatedPlace._subChapter);
		stmt.setBoolean("@extra", item._updatedPlace._extra);
		stmt.executeUpdate();
	}

	@Override
	public void delete(long id) {
		String sql = "DELETE FROM UpdateBookmark WHERE _id = @id";
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setLong("@id", id);
			stmt.execute();
		} catch (SQLException e) {
			_logging.error(TAG, "Delete failed: " + sql, e);
		} 
	}
}
