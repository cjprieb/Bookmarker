package com.purplecat.bookmarker.services.databases;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.extensions.FavoriteStateExt;
import com.purplecat.bookmarker.extensions.PlaceExt;
import com.purplecat.bookmarker.extensions.StoryStateExt;
import com.purplecat.bookmarker.extensions.TitleExt;
import com.purplecat.bookmarker.models.Genre;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.sql.ConnectionManager;
import com.purplecat.bookmarker.sql.DBUtils;
import com.purplecat.bookmarker.sql.NamedResultSet;
import com.purplecat.bookmarker.sql.NamedStatement;
import com.purplecat.commons.logs.ILoggingService;

public class MediaDatabaseRepository implements IMediaRepository {
	public static final String TAG = "MediaDatabaseRepository";
	
	private static final String SELECT_MEDIA = "SELECT Media._id _id, MdDisplayTitle _displayTitle, MdIsComplete _isComplete, SvdIsSaved _isSaved, "
			+ " SvdStoryState _storyState, SvdRating _rating, SvdIsFlagged _isFlagged, SvdNotes _notes, "
										+ " hist.svhstDate _lastReadDate, hist.svhstPlace _lastReadPlace, "
										+ " CASE WHEN svdisSaved THEN upbkchapterUrl ELSE upbktitleUrl END _updatedUrl, upbkdate _updatedDate, upbkplace _updatedPlace"
										+ " FROM MEDIA" + 
										" LEFT JOIN savedhistory hist on hist._id = media.svdhistory_id" +
										" LEFT JOIN updateBookmark upbk on upbk._id = media.uponline_id";
	
	private static final String COUNT_MEDIA = "SELECT COUNT(*) AS total_rows FROM MEDIA ";
	
	public final ILoggingService _logging;
	public final ConnectionManager _connectionManager;
	public final GenreDatabaseRepository _genreDatabase;
	 
	@Inject
	public MediaDatabaseRepository(ILoggingService logger, ConnectionManager mgr, GenreDatabaseRepository genreDatabase) {
		_logging = logger;
		_connectionManager = mgr;
		_genreDatabase = genreDatabase;
	}
	
	/**
	 * Assumes the SELECT_MEDIA query was used
	 * @param result
	 * @return
	 * @throws SQLException 
	 */
	private Media loadMediaFromResultSet(NamedResultSet result) throws SQLException {
		Media media = new Media();
		media._id = result.getLong("_id");
		media._displayTitle = result.getString("_displayTitle");
		media._isComplete = result.getBoolean("_isComplete");
		media._isSaved = result.getBoolean("_isSaved");
		media._lastReadDate = result.getDateFromString("_lastReadDate");
		media._lastReadPlace = PlaceExt.parse(result.getString("_lastReadPlace"));
		media._storyState = StoryStateExt.parse(result.getInt("_storyState"));
		media._rating= FavoriteStateExt.parse(result.getInt("_rating"));
		media._notes = result.getString("_notes");
		media._updatedUrl = result.getString("_updatedUrl");
		media._updatedDate = result.getDateFromString("_updatedDate");
		media._updatedPlace = PlaceExt.parse(result.getString("_updatedPlace"));
		return media;
	}

	@Override
	public List<Media> query() throws DatabaseException {
		List<Media> list = new LinkedList<Media>();
		try {
			Connection conn = _connectionManager.getConnection();
			Statement stmt = conn.createStatement();
			NamedResultSet result = new NamedResultSet(stmt.executeQuery(SELECT_MEDIA));
			while ( result.next() ) {
				list.add(loadMediaFromResultSet(result));
			}
			loadGenres(conn, list, null);
		} catch (SQLException e) {
			throw new DatabaseException("Query failed", SELECT_MEDIA, e);
		} 
		return list;
	}

	@Override
	public List<Media> querySavedMedia(IListLoadedObserver<Media> observer) throws DatabaseException {
		List<Media> list = new LinkedList<Media>();
		String whereClause = " WHERE SvdIsSaved = 1";
		String countSql = COUNT_MEDIA + whereClause;
		String sql = SELECT_MEDIA + whereClause;
		try {
			Connection conn = _connectionManager.getConnection();
			Statement countStmt = conn.createStatement();
			NamedResultSet countResult = new NamedResultSet(countStmt.executeQuery(countSql));
			int total = countResult.next() ? countResult.getInt("total_rows") : 0;
			
			Statement stmt = conn.createStatement();
			NamedResultSet result = new NamedResultSet(stmt.executeQuery(sql));
			int index = 0;
			while ( result.next() ) {
				Media item = loadMediaFromResultSet(result);
				
				list.add(item);
				index++;
				if ( observer != null ) {
					observer.notifyItemLoaded(item, index, total);
				}
			}
			loadGenres(conn, list, observer);
		} catch (SQLException e) {
			_logging.error(TAG, "Exception querying for saved media", e);
			throw new DatabaseException("querySavedMedia failed", sql, e);
		} 
		return list;
	}

	@Override
	public Media queryById(long id) throws DatabaseException {
		Media media = null;
		String sql = SELECT_MEDIA + " WHERE Media._id = @id";
		try {
			Connection conn = _connectionManager.getConnection();
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setLong("@id", id);
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				media = loadMediaFromResultSet(result);
			}
			if ( media != null ) {
				loadGenres(conn, Collections.singleton(media), null);
			}
		} catch (SQLException e) {
			_logging.error(TAG, "Exception querying for id " + id, e);
			throw new DatabaseException("queryById failed", sql, e);
		} 
		return media;
	}

	@Override
	public List<Media> queryByTitle(String title) throws DatabaseException {
		List<Media> list = new LinkedList<Media>();
		String sql = "SELECT TtMedia_ID FROM Title WHERE TtStripped LIKE @title";
		try {
			Connection conn = _connectionManager.getConnection();
			String strippedTitle = TitleExt.stripTitle(title);
			List<Long> idList = new ArrayList<Long>();

			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setString("@title", strippedTitle);
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				idList.add(result.getLong("TtMedia_ID"));
			}
			
			if ( idList.size() > 0 ) {
				sql = String.format(SELECT_MEDIA + " WHERE Media._id IN (%s)", DBUtils.formatIdList(idList));
				stmt = new NamedStatement(conn, sql);
				stmt.setString("@title", title);
				result = stmt.executeQuery();
				while ( result.next() ) {
					list.add(loadMediaFromResultSet(result));
				}
			}
			loadGenres(conn, list, null);
		} catch (SQLException e) {
			_logging.error(TAG, "Exception querying for title " + title, e);
			throw new DatabaseException("queryByTitle failed", sql, e);
		} 
		return list;
	}

	@Override
	public void insert(Media item) throws DatabaseException, ServiceException {
		if ( item._isSaved == false ) {
			throw new ServiceException("Media item must be 'saved'.", ServiceException.INVALID_DATA);
		}
		String sql = "INSERT INTO Media (MdDisplayTitle, MdIsComplete, SvdIsSaved, SvdStoryState, SvdRating, SvdNotes)"
				+ " VALUES (@title, @complete, @saved, @state, @rating, @notes)";
		try {
			Connection conn = _connectionManager.getConnection();
			conn.setAutoCommit(false);
			
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setString("@title", item._displayTitle);
			stmt.setString("@title", item._displayTitle);
			stmt.setBoolean("@complete", item._isComplete);
			stmt.setBoolean("@saved", item._isSaved);
			stmt.setInt("@state", item._storyState.getValue());
			stmt.setInt("@rating", item._rating.getValue());
			stmt.setString("@notes", item._notes);
			item._id = stmt.executeInsert();
			
			updateHistory(conn, item);
			
			conn.commit();
		} catch (SQLException e) {
			throw new DatabaseException("insert failed", sql, e);
		}		
	}

	@Override
	public void update(Media item) throws DatabaseException {
		if ( item._id > 0 ) {
			String sql = "";
			try {
				Connection conn = _connectionManager.getConnection();
				conn.setAutoCommit(false);
				
				sql = "UPDATE Media SET MdDisplayTitle = @title, MdIsComplete = @complete, SvdIsSaved = @saved, SvdStoryState = @state,"
						+ " SvdRating = @rating, SvdNotes = @notes WHERE _id = @id";
				NamedStatement stmt = new NamedStatement(conn, sql);
				stmt.setLong("@id", item._id);
				stmt.setString("@title", item._displayTitle);
				stmt.setBoolean("@complete", item._isComplete);
				stmt.setBoolean("@saved", item._isSaved);
				stmt.setInt("@state", item._storyState.getValue());
				stmt.setInt("@rating", item._rating.getValue());
				stmt.setString("@notes", item._notes);
				stmt.executeUpdate();
				
				updateHistory(conn, item);			
								
				conn.commit();
			} catch (SQLException e) {
				throw new DatabaseException("update failed", sql, e);
			}
		}
		else {
			_logging.error(TAG, "Unable to update: invalid id");
		}
	}

	@Override
	public void delete(long id) throws DatabaseException {
		String sql = "DELETE FROM Media WHERE _id = @id";
		try {
			Connection conn = _connectionManager.getConnection();
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setLong("@id", id);
			stmt.execute();
			
			stmt = new NamedStatement(conn, "DELETE FROM GenreMap WHERE GenMedia_ID = @mediaId");
			stmt.setLong("@mediaId", id);
			stmt.execute();			
		} catch (SQLException e) {
			throw new DatabaseException("delete failed", sql, e);
		}	
	}
	
	private void loadGenres(Connection conn, Collection<Media> list, IListLoadedObserver<Media> observer) throws DatabaseException {
		Map<Long, Set<Genre>> map = _genreDatabase.loadAllMediaGenres();
		int index = 0;
		int total = list.size();
		for ( Media media : list ) {
			if ( map.containsKey(media._id)) {
				media._genres.clear();
				media._genres.addAll(map.get(media._id));
			}
			index++;
			if ( observer != null ) {
				observer.notifyItemLoaded(media, index, total);
			}
		}
	}
	
	private void updateHistory(Connection conn, Media item) throws SQLException {
		String sql = "INSERT INTO SavedHistory (SvhstMedia_ID, SvhstDate, SvhstPlace, SvhstUrl, SvhstVolume, SvhstChapter, SvhstSubChapter, SvhstPage, SvhstExtra) " +
				"VALUES (@mediaId, @date, @place, @url, @v, @ch, @sub, @pg, @extra)";
		NamedStatement stmt = new NamedStatement(conn, sql);
		stmt.setLong("@mediaId", item._id);
		stmt.setDate("@date", item._lastReadDate);
		stmt.setString("@place", PlaceExt.format(item._lastReadPlace));
		stmt.setString("@url", item._chapterURL);
		stmt.setInt("@v", item._lastReadPlace._volume);
		stmt.setInt("@ch", item._lastReadPlace._chapter);
		stmt.setInt("@sub", item._lastReadPlace._subChapter);
		stmt.setInt("@pg", item._lastReadPlace._page);
		stmt.setBoolean("@extra", item._lastReadPlace._extra);
		long newHistId = stmt.executeInsert();
		
		sql = "UPDATE Media SET SvdHistory_ID=@histId WHERE _id = @id";
		stmt = new NamedStatement(conn, sql);
		stmt.setLong("@id", item._id);
		stmt.setLong("@histId", newHistId);
		stmt.executeUpdate();	
	}
}
