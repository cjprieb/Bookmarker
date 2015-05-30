package com.purplecat.bookmarker.services.databases;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.purplecat.bookmarker.controller.observers.IListLoadedObserver;
import com.purplecat.bookmarker.extensions.FavoriteStateExt;
import com.purplecat.bookmarker.extensions.PlaceExt;
import com.purplecat.bookmarker.extensions.StoryStateExt;
import com.purplecat.bookmarker.extensions.TitleExt;
import com.purplecat.bookmarker.models.Media;
import com.purplecat.bookmarker.services.ServiceException;
import com.purplecat.bookmarker.sql.DBUtils;
import com.purplecat.bookmarker.sql.NamedResultSet;
import com.purplecat.bookmarker.sql.NamedStatement;
import com.purplecat.commons.logs.ILoggingService;

public class MediaDatabaseRepository implements IMediaRepository {
	public static final String TAG = "MediaDatabaseRepository";
	
	private static final String SELECT_MEDIA = "SELECT Media._id _id, MdDisplayTitle _displayTitle, MdIsComplete _isComplete, SvdIsSaved _isSaved, "
			+ " SvdStoryState _storyState, SvdRating _rating, SvdIsFlagged _isFlagged, SvdNotes _notes, "
										+ " hist.svhstDate _lastReadDate, hist.svhstPlace _lastReadPlace"
										+ " FROM MEDIA" + 
										" LEFT JOIN savedhistory hist on hist._id = media.svdhistory_id";
	
	private static final String COUNT_MEDIA = "SELECT COUNT(*) AS total_rows"
										+ " FROM MEDIA" + 
										" LEFT JOIN savedhistory hist on hist._id = media.svdhistory_id";
	
		
	
	public final ILoggingService _logging;
	public final String _connectionPath;
	 
	@Inject
	public MediaDatabaseRepository(ILoggingService logger, @Named("JDBC URL") String dbPath) {
		_logging = logger;
		_connectionPath = dbPath;
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
		return media;
	}

	@Override
	public List<Media> query() {
		List<Media> list = new LinkedList<Media>();
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			Statement stmt = conn.createStatement();
			NamedResultSet result = new NamedResultSet(stmt.executeQuery(SELECT_MEDIA));
			while ( result.next() ) {
				list.add(loadMediaFromResultSet(result));
			}
		} catch (SQLException e) {
			_logging.error(TAG, "Query failed", e);
		} 
		return list;
	}

	@Override
	public List<Media> querySavedMedia(IListLoadedObserver<Media> observer) throws ServiceException {
		List<Media> list = new LinkedList<Media>();
		String whereClause = " WHERE SvdIsSaved = 1";
		String countSql = COUNT_MEDIA + whereClause;
		String sql = SELECT_MEDIA + whereClause;
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
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
		} catch (SQLException e) {
			_logging.error(TAG, "Query for saved failed: " + sql, e);
			throw new ServiceException("Query for saved media failed", ServiceException.SQL_ERROR);
		} 
		return list;
	}

	@Override
	public Media queryById(long id) {
		Media media = null;
		String sql = SELECT_MEDIA + " WHERE Media._id = @id";
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setLong("@id", id);
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				media = loadMediaFromResultSet(result);
			}
		} catch (SQLException e) {
			_logging.error(TAG, "Query for id failed: " + sql, e);
		} 
		return media;
	}

	@Override
	public List<Media> queryByTitle(String title) {
		List<Media> list = null;
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			list = queryByTitle(conn, title);
		} catch (SQLException e) {
			_logging.error(TAG, "Query for id failed", e);
		} 
		return list;
	}

	public List<Media> queryByTitle(Connection conn, String title) throws SQLException {
		List<Media> list = new LinkedList<Media>();
		String strippedTitle = TitleExt.stripTitle(title);
		List<Long> idList = new ArrayList<Long>();

		String sql = "SELECT TtMedia_ID FROM Title WHERE TtStripped LIKE @title";
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
		return list;
	}

	@Override
	public void insert(Media item) throws ServiceException {
		if ( item._isSaved == false ) {
			throw new ServiceException("Media item must be 'saved'.", ServiceException.INVALID_DATA);
		}
		String sql = "INSERT INTO Media (MdDisplayTitle, MdIsComplete, SvdIsSaved, SvdStoryState, SvdRating, SvdNotes)"
				+ " VALUES (@title, @complete, @saved, @state, @rating, @notes)";
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
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
			_logging.error(TAG, "Insert failed: " + sql, e);
			throw new ServiceException("Inserting media item failed", ServiceException.SQL_ERROR);
		}		
	}

	@Override
	public void update(Media item) {
		if ( item._id > 0 ) {
			String sql = "";
			try (Connection conn = DriverManager.getConnection(_connectionPath)) {
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
				_logging.error(TAG, "Update failed: " + sql, e);
			}
		}
		else {
			_logging.error(TAG, "Unable to update: invalid id");
		}
	}

	@Override
	public void delete(long id) {
		String sql = "DELETE FROM Media WHERE _id = @id";
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setLong("@id", id);
			stmt.execute();
		} catch (SQLException e) {
			_logging.error(TAG, "Delete failed: " + sql, e);
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
