package com.purplecat.bookmarker.services.databases;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.purplecat.bookmarker.extensions.TitleExt;
import com.purplecat.bookmarker.models.AltTitle;
import com.purplecat.bookmarker.sql.ConnectionManager;
import com.purplecat.bookmarker.sql.DBUtils;
import com.purplecat.bookmarker.sql.NamedResultSet;
import com.purplecat.bookmarker.sql.NamedStatement;
import com.purplecat.commons.logs.ILoggingService;

@Singleton
public class TitleDatabaseRepository {
	static String TAG = "TitleDatabaseRepository";
	static String SELECT_MEDIA_TITLES = "SELECT _id, TtMedia_ID, TtTitle, TtStripped FROM Title";
	
	public final ILoggingService _logging;
	public final ConnectionManager _connectionManager;
	 
	@Inject
	public TitleDatabaseRepository(ILoggingService logger, ConnectionManager mgr) {
		_logging = logger;
		_connectionManager = mgr;
	}
	
	public List<String> queryByMediaId(long id) throws DatabaseException {
		List<String> list = new LinkedList<String>();
		String sql = SELECT_MEDIA_TITLES + " WHERE TtMedia_ID = @mediaId";
		try {
			Connection conn = _connectionManager.getConnection();
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setLong("@mediaId", id);
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				list.add(result.getString("TtTitle"));
			}
		} catch (SQLException e) {
			throw new DatabaseException("queryByMediaId failed", sql, e);
		} 
		return list;
	}
	
	public boolean updateTitleList(Collection<String> list, long mediaId) throws DatabaseException {
		boolean bSuccess = false;
		String sql = "";
		try {
			Connection conn = _connectionManager.getConnection();
			Set<AltTitle> existingTitles = new HashSet<AltTitle>();
			
			sql = SELECT_MEDIA_TITLES + " WHERE TtMedia_ID = @mediaId";
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setLong("@mediaId", mediaId);
			
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				AltTitle altTitle = new AltTitle();
				altTitle._id = result.getLong("_id");
				altTitle._mediaId = result.getLong("TtMedia_ID");
				altTitle._title = result.getString("TtTitle");
				altTitle._stripped = result.getString("TtStripped");				
				existingTitles.add(altTitle);
			}

			sql = "INSERT INTO Title (TtMedia_Id, TtTitle, TtStripped) VALUES (@mediaId, @title, @stripped)";
			stmt = new NamedStatement(conn, sql);
			for ( String title : list ) {
				String stripped = TitleExt.stripTitle(title);
				if ( existingTitles.stream().noneMatch(item -> item._stripped.equals(stripped)) ) {
					stmt.setLong("@mediaId", mediaId);
					stmt.setString("@title", title);
					stmt.setString("@stripped", stripped);
					stmt.addBatch();
				}
			}
			stmt.executeBatchUpdate();
			
			Set<Long> removeIds = new HashSet<Long>();
			for ( AltTitle existing : existingTitles ) {
				if ( list.stream().noneMatch(item -> TitleExt.stripTitle(item).equals(existing._stripped)) ) {
					removeIds.add(existing._id);
				}
			}

			sql = String.format("DELETE FROM Title WHERE _id in (%s)",
					DBUtils.formatIdList(removeIds));
			stmt = new NamedStatement(conn, sql);
			stmt.execute();
			bSuccess = true;
		} catch (SQLException e) {
			_logging.error(TAG, "Error at sql " + sql);
			throw new DatabaseException("updateTitleList failed", sql, e);
		} 
		return bSuccess;
	}
	
	public Map<Long, Set<String>> loadAllMediaTitles() throws DatabaseException {
		Map<Long, Set<String>> map = new HashMap<Long, Set<String>>();
		String sql = "";
		try {
			Connection conn = _connectionManager.getConnection();

			long mediaId = -1;
			Set<String> titleList = null;
			
			sql = SELECT_MEDIA_TITLES;
			NamedStatement stmt = new NamedStatement(conn, sql);
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				mediaId = result.getLong("TtMedia_ID");
				titleList = map.get(mediaId);
				if ( titleList == null ) {
					titleList = new HashSet<String>();
					map.put(mediaId, titleList);
				}
				titleList.add(result.getString("TtTitle"));
			}
		} catch (SQLException e) {
			_logging.error(TAG, "Exception loading all media titles", e);
			throw new DatabaseException("loadAllMediaTitles failed", sql, e);
		} 
		return map;
	}
}
