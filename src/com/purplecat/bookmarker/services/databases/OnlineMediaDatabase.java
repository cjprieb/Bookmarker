package com.purplecat.bookmarker.services.databases;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.purplecat.bookmarker.extensions.OnlineMediaItemExt;
import com.purplecat.bookmarker.extensions.PlaceExt;
import com.purplecat.bookmarker.models.Genre;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.models.OnlineMediaItem;
import com.purplecat.bookmarker.services.ISummaryRepository;
import com.purplecat.bookmarker.sql.ConnectionManager;
import com.purplecat.bookmarker.sql.NamedResultSet;
import com.purplecat.bookmarker.sql.NamedStatement;
import com.purplecat.commons.logs.ILoggingService;
import com.purplecat.commons.utils.StringUtils;

public class OnlineMediaDatabase implements IOnlineMediaRepository {
	public static final String TAG = "MangaDatabaseConnector";
	
	private static final String SELECT_ITEMS = "SELECT UpdateBookmark._id, UpbkMedia_id _mediaId, UpbkChapterUrl _chapterUrl, UpbkTitleUrl _titleUrl, UpbkWebsiteName _websiteName,"
			+ " UpbkIsIgnored _isIgnored, UpbkDate _updatedDate, UpbkRating _rating, UpbkPlace _updatedPlace, UpbkNewlyAdded _newlyAdded,"
			+ " MdDisplayTitle _displayTitle, SvdIsSaved _isSaved, SvhstDate _lastReadDate, SvhstPlace _lastReadPlace, SvdFolder_Id _folderId"
			+ " FROM UpdateBookmark INNER JOIN Media on Media._id = UpbkMedia_id"
			+ " LEFT JOIN SavedHistory on SavedHistory._id = SvdHistory_ID";
	
		
	
	public final ILoggingService _logging;
	public final ConnectionManager _connectionManager;
	public final MediaDatabaseRepository _mediaDatabase;
	public final GenreDatabaseRepository _genreDatabase;
	public final TitleDatabaseRepository _titleDatabase;
	public final ISummaryRepository _summaryRepository;
	 
	@Inject
	public OnlineMediaDatabase(ILoggingService logger, ConnectionManager mgr, MediaDatabaseRepository mediaDb, GenreDatabaseRepository genreDb, 
			TitleDatabaseRepository titleDb, ISummaryRepository summaryRepository) {
		_logging = logger;
		_connectionManager = mgr;
		_mediaDatabase = mediaDb;
		_genreDatabase = genreDb;
		_titleDatabase = titleDb;
		_summaryRepository = summaryRepository;
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
		item._folderId = result.getInt("_folderId");
		return item;
	}

	@Override
	public List<OnlineMediaItem> query() throws DatabaseException {
		List<OnlineMediaItem> list = new LinkedList<OnlineMediaItem>();
		try {
			Connection conn = _connectionManager.getConnection();
			Statement stmt = conn.createStatement();
			NamedResultSet result = new NamedResultSet(stmt.executeQuery(SELECT_ITEMS));
			while ( result.next() ) {
				list.add(loadOnlineMediaFromResultSet(result));
			}
			loadGenres(conn, list);
		} catch (SQLException e) {
			throw new DatabaseException("Query failed", SELECT_ITEMS, e);
		}
		return list;
	}

	@Override
	public OnlineMediaItem queryById(long id) throws DatabaseException {
		OnlineMediaItem item = null;
		String sql = SELECT_ITEMS + " WHERE UpdateBookmark._id = @id";
		try {
			Connection conn = _connectionManager.getConnection();
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setLong("@id", id);
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				item = loadOnlineMediaFromResultSet(result);
				break;
			}
			if ( item != null ) {
				item._genres.addAll(_genreDatabase.queryByMediaId(item._mediaId));
				item._summary = _summaryRepository.loadSummary(item._mediaId, item._websiteName);
			}
		} catch (SQLException e) {
			throw new DatabaseException("queryForId failed", sql, e);
		} 
		return item;
	}

	@Override
	public OnlineMediaItem findOrCreate(OnlineMediaItem item) throws DatabaseException {
		OnlineMediaItem result = null;
		List<OnlineMediaItem> existingList = null;
		try {
			Connection conn = _connectionManager.getConnection();
			_logging.debug(2, TAG, "Connection retrieved");
			
			List<Media> matches = _mediaDatabase.queryByTitle(item._displayTitle);
			//TODO: don't assume first is best title match
			_logging.debug(3, TAG, "Matches found: " + matches.size());
			if ( matches.size() > 0 ) {
				item._mediaId = matches.get(0)._id;
				item._genres.addAll(matches.get(0)._genres);
				existingList = queryByMediaId(item._mediaId);
//				_logging.debug(3, TAG, "Found existing online items: " + existingList.size());
				
				for ( OnlineMediaItem existing : existingList ) {
					if ( existing._websiteName.equals(item._websiteName) ) {
						result = existing;
						OnlineMediaItemExt.copyNewToExisting(item, result);
					}
				}
			}

			_logging.debug(3, TAG, "Disabling commit");
			conn.setAutoCommit(false);
			
			if (matches.size() == 0) {
				//Insert new Media item
				_logging.debug(4, TAG, "Inserting media");
				item._mediaId = createMedia(conn, item);
				_logging.debug(4, TAG, "id: " + item._mediaId);
			}
			
			if ( result != null ) {
				_logging.debug(4, TAG, "Updating online item");
				update(result);
			}
			else {
				_logging.debug(4, TAG, "Inserting online item");
				item._newlyAdded = true;
				insert(conn, item);
				if ( item._mediaId > 0 ) {
					//matching media was found, but not matching online media, 
					// so reload after insert to load media-specific fields
					result = queryById(item._id);
					_logging.debug(4, TAG, "id (queryById): " + result._id);
				}
				else {
					result = item;
					_logging.debug(4, TAG, "id: " + result._id);
				}
			}
			long maxId = result._id;
			if ( existingList != null ) {
				maxId = OnlineMediaItemExt.getIdWithMaxPlace(existingList, item);
			}
			_logging.debug(4, TAG, "Updating media");
			updateMedia(conn, item._mediaId, maxId > 0 ? maxId : result._id);
			
			conn.commit();
			_logging.debug(3, TAG, "All committed");
		} catch (SQLException e) {
			throw new DatabaseException("findOrCreate failed", e);
		} 
		return result;
	}

	@Override
	public void insert(OnlineMediaItem item) throws DatabaseException {
		try {
			Connection conn = _connectionManager.getConnection();
			if ( item._mediaId <= 0 ) {
				item._mediaId = createMedia(conn, item);
			}
			insert(conn, item);
			List<OnlineMediaItem> existingList = queryByMediaId(item._mediaId);
			updateMedia(conn, item._mediaId, OnlineMediaItemExt.getIdWithMaxPlace(existingList, item));
		} catch (SQLException e) {
			throw new DatabaseException("Insert failed", e);
		} 
	}

	@Override
	public void update(OnlineMediaItem item) throws DatabaseException {
		String sql = "UPDATE UpdateBookmark SET UpbkMedia_ID=@mediaId, UpbkChapterUrl=@chapterUrl, UpbkTitleUrl=@titleUrl, UpbkWebsiteName=@websiteName, "
				+ " UpbkDate=@date, UpbkRating=@rating, UpbkIsIgnored=@isIgnored, "
				+ " UpbkPlace=@place, UpbkNewlyAdded=@newlyAdded, UpbkVolume=@volume, UpbkChapter=@chapter, UpbkSubChapter=@sub, UpbkExtra=@extra"
				+ " WHERE _id = @id";
		try {
			Connection conn = _connectionManager.getConnection();
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

			_genreDatabase.updateGenreList(item._genres, item._mediaId);
			List<OnlineMediaItem> existingList = queryByMediaId(item._mediaId);
			updateMedia(conn, item._mediaId, OnlineMediaItemExt.getIdWithMaxPlace(existingList, item));
			if ( !StringUtils.isNullOrEmpty(item._summary) ) {
				_summaryRepository.saveSummary(item._mediaId, item._websiteName, item._summary);
			}
		} catch (SQLException e) {
			throw new DatabaseException("Update failed", sql, e);
		} 
	}

	@Override
	public void delete(long id) throws DatabaseException {
		String sql = "DELETE FROM UpdateBookmark WHERE _id = @id";
		try {
			Connection conn = _connectionManager.getConnection();
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setLong("@id", id);
			stmt.execute();
		} catch (SQLException e) {
			throw new DatabaseException("Delete failed", sql, e);
		} 
	}

	@Override
	public List<OnlineMediaItem> queryByMediaId(long mediaId) throws DatabaseException {
		String sql = SELECT_ITEMS + " WHERE UpbkMedia_ID = @mediaId";
		List<OnlineMediaItem> existing = new LinkedList<OnlineMediaItem>();
		try {
			Connection conn = _connectionManager.getConnection();
			List<Genre> genres = _genreDatabase.queryByMediaId(mediaId);
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setLong("@mediaId", mediaId);
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				OnlineMediaItem item = loadOnlineMediaFromResultSet(result);
				item._genres.addAll(genres);
				item._summary = _summaryRepository.loadSummary(item._mediaId, item._websiteName);
				existing.add(item);
			}
		} catch (SQLException e) {
			throw new DatabaseException("queryByMediaId failed", sql, e);
		} 
		return existing;
	}

	private long createMedia(Connection conn, OnlineMediaItem item) throws SQLException, DatabaseException {
		String sql = "INSERT INTO Media (MdDisplayTitle, SvdIsSaved) VALUES (@title, @isSaved)";
		NamedStatement stmt = new NamedStatement(conn, sql);
		stmt.setString("@title", item._displayTitle);
		stmt.setBoolean("@isSaved", item._isSaved);
		long mediaId = stmt.executeInsert();
		_titleDatabase.updateTitleList(Collections.singleton(item._displayTitle), mediaId);
		return mediaId;
	}
	
	private void loadGenres(Connection conn, Collection<OnlineMediaItem> list) throws DatabaseException {
		Map<Long, Set<Genre>> map = _genreDatabase.loadAllMediaGenres(); //loading all since we have a list to load.
		for ( OnlineMediaItem item : list ) {
			if ( map.containsKey(item._mediaId)) {
				item._genres.clear();
				item._genres.addAll(map.get(item._mediaId));
			}
		}
	}

	private void insert(Connection conn, OnlineMediaItem item) throws SQLException {
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

	private void updateMedia(Connection conn, long mediaId, long updatedId) throws SQLException {
		String sql = "UPDATE Media SET UpOnline_Id=@upId WHERE _id = @mediaId";
		NamedStatement stmt = new NamedStatement(conn, sql);
		stmt.setLong("@upId", updatedId);
		stmt.setLong("@mediaId", mediaId);
		stmt.executeUpdate();
	}
}
