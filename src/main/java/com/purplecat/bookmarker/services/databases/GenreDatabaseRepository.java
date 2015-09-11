package com.purplecat.bookmarker.services.databases;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.purplecat.bookmarker.models.Genre;
import com.purplecat.bookmarker.sql.ConnectionManager;
import com.purplecat.bookmarker.sql.DBUtils;
import com.purplecat.bookmarker.sql.NamedResultSet;
import com.purplecat.bookmarker.sql.NamedStatement;
import com.purplecat.commons.logs.ILoggingService;

@Singleton
public class GenreDatabaseRepository implements IGenreRepository {
	static String TAG = "GenreDatabaseRepository";
	static String SELECT_GENRE = "SELECT _id, GenName _name, GenAltName _altName, GenInclude _include "
			+ " FROM Genre ORDER BY _name";
	
	static String SELECT_MEDIA_GENRE = "SELECT _id, GenName _name, GenAltName _altName, GenInclude _include "
			+ " FROM GenreMap INNER JOIN Genre on _id = GenGenre_id "
			+ " WHERE GenMedia_id = @mediaId ORDER BY GenName";
	
	public final ILoggingService _logging;
	public final ConnectionManager _connectionManager;
	
	private Map<Long, Genre> _genreCache;
	 
	@Inject
	public GenreDatabaseRepository(ILoggingService logger, ConnectionManager mgr) {
		_logging = logger;
		_connectionManager = mgr;
		 _genreCache = new HashMap<Long, Genre>();
	}
	
	/**
	 * Assumes the SELECT_GENRE query was used
	 * @param result
	 * @return
	 * @throws SQLException 
	 */
	private Genre loadGenreFromResultSet(NamedResultSet result) throws SQLException {
		long id = result.getLong("_id");
		Genre genre;
		if ( _genreCache.containsKey(id) ) {
			genre = _genreCache.get(id);
		}
		else {
			genre = new Genre();
			genre._id = id;
			_genreCache.put(id, genre);
		}
		genre._name = result.getString("_name");
		genre._altName = result.getString("_altName");
		genre._include = result.getBoolean("_include");
		return genre;
	}
	
	@Override
	public List<Genre> query() throws DatabaseException {
		List<Genre> list = new LinkedList<Genre>();
		try {
			Connection conn = _connectionManager.getConnection();
			Statement stmt = conn.createStatement();
			NamedResultSet result = new NamedResultSet(stmt.executeQuery(SELECT_GENRE));
			while ( result.next() ) {
				list.add(loadGenreFromResultSet(result));
			}
		} catch (SQLException e) {
			throw new DatabaseException("query failed", SELECT_GENRE, e);
		} 
		return list;
	}
	
	@Override
	public List<Genre> queryByMediaId(long id) throws DatabaseException {
		List<Genre> list = new LinkedList<Genre>();
		try {
			Connection conn = _connectionManager.getConnection();
			NamedStatement stmt = new NamedStatement(conn, SELECT_MEDIA_GENRE);
			stmt.setLong("@mediaId", id);
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				list.add(loadGenreFromResultSet(result));
			}
		} catch (SQLException e) {
			throw new DatabaseException("queryByMediaId failed", SELECT_MEDIA_GENRE, e);
		} 
		return list;
	}
	
	@Override
	public boolean updateGenreList(Collection<Genre> list, long mediaId) throws DatabaseException {
		boolean bSuccess = false;
		String sql = "";
		try {
			Connection conn = _connectionManager.getConnection();
			Set<Long> existingIds = new HashSet<Long>();
			sql = "SELECT GenGenre_ID FROM GenreMap WHERE GenMedia_ID = " + mediaId;
			NamedStatement stmt = new NamedStatement(conn, sql);
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				existingIds.add(result.getLong("GenGenre_ID"));
			}
//			System.out.println("Existing ids:" + DBUtils.formatIdList(existingIds));

			Set<Long> includeIds = new HashSet<Long>();
			Set<Genre> insertItems = new HashSet<Genre>();
//			System.out.print("Updating map:");
			for ( Genre genre : list ) {
//				System.out.print(genre._id + ",");
				if ( genre._id == 0 ) {
					insertItems.add(genre);
				}
				else if ( !existingIds.contains(genre._id) ) {
					includeIds.add(genre._id);
				}
			}
			System.out.println("");
			
			Set<Long> removeIds = new HashSet<Long>();
			for ( Long id : existingIds ) {
				boolean bFound = false;
				for ( Genre genre : list ) {
					if ( genre._id == id ) {
						bFound = true;
					}
				}
				if ( !bFound ) {
					removeIds.add(id);
				}
			}

//			System.out.println("Adding new genres");
			sql = "INSERT INTO Genre (GenName, GenAltName, GenInclude) VALUES (@name, @alt, @include)";
			stmt = new NamedStatement(conn, sql);
			for ( Genre genre : insertItems ) {
				stmt.setString("@name", genre._name);
				stmt.setString("@alt", genre._altName != null ? genre._altName : "");
				stmt.setBoolean("@include", true);
				genre._id = stmt.executeInsert();
				_genreCache.put(genre._id, genre);
				includeIds.add(genre._id);
				System.out.println("    " + genre._id + " " + genre._name);
			}

//			System.out.println("Updating map:" + DBUtils.formatIdList(includeIds));
			sql = "INSERT INTO GenreMap (GenGenre_ID, GenMedia_ID) VALUES (@genreId, @mediaId)";
			stmt = new NamedStatement(conn, sql);
			for ( Long id : includeIds ) {
				stmt.setLong("@genreId", id);
				stmt.setLong("@mediaId", mediaId);
				stmt.addBatch();
			}
			stmt.executeBatchUpdate();

//			System.out.println("Deleting map:" + DBUtils.formatIdList(removeIds));
			sql = String.format("DELETE FROM GenreMap WHERE GenMedia_ID = %d AND GenGenre_ID in (%s)", 
					mediaId,
					DBUtils.formatIdList(removeIds));
			stmt = new NamedStatement(conn, sql);
			stmt.execute();
			bSuccess = true;
		} catch (SQLException e) {
			throw new DatabaseException("updateGenreList failed", sql, e);
		} 
		return bSuccess;
	}
	
	@Override
	public Genre find(String text) {
//TODO: pass Genres to Website Scrapers
//		if ( _genreCache.size() == 0 ) {
//			//assume has not been loaded yet if empty
//			query();
//		}
		for ( Genre existing : _genreCache.values() ) {
			if ( existing._name.equalsIgnoreCase(text) || existing._altName.equalsIgnoreCase(text) ) {
				return existing;
			}
		}
		Genre genre = new Genre();
		genre._name = text;
		return genre;
	}
	
	public Map<Long, Set<Genre>> loadAllMediaGenres() throws DatabaseException {
		Map<Long, Set<Genre>> map = new HashMap<Long, Set<Genre>>();
		String sql = "";
		try {
			Connection conn = _connectionManager.getConnection();
			//first make sure all genres are loaded:
			loadGenreCache(conn);

			long mediaId = -1;
			Set<Genre> genreList = null;
			
			sql = "SELECT GenMedia_ID, GenGenre_ID FROM GenreMap";
			NamedStatement stmt = new NamedStatement(conn, sql);
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				mediaId = result.getLong("GenMedia_ID");
				genreList = map.get(mediaId);
				if ( genreList == null ) {
					genreList = new HashSet<Genre>();
					map.put(mediaId, genreList);
				}
				genreList.add(_genreCache.get(result.getLong("GenGenre_ID")));
			}
		} catch (SQLException e) {
			_logging.error(TAG, "Exception loading all media genres", e);
			throw new DatabaseException("loadAllMediaGenres failed", sql, e);
		} 
		return map;
	}
	
	public Set<Genre> loadGenresForMedia(long id) throws DatabaseException {
		Set<Genre> genreList = new HashSet<Genre>();
		String sql = "";
		try {
			Connection conn = _connectionManager.getConnection();
			//first make sure all genres are loaded:
			loadGenreCache(conn);
			
			sql = "SELECT GenMedia_ID, GenGenre_ID FROM GenreMap WHERE GenMedia_ID = @mediaId";			
			NamedStatement stmt = new NamedStatement(conn, sql);
			stmt.setLong("@mediaId", id);
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				genreList.add(_genreCache.get(result.getLong("GenGenre_ID")));
			}
		} catch (SQLException e) {
			_logging.error(TAG, "Exception loading all media genres", e);
			throw new DatabaseException("loadGenresForMedia failed", sql, e);
		} 
		return genreList;
	}
	
	protected void loadGenreCache(Connection conn) throws SQLException {
		if ( _genreCache.size() == 0 ) {
			NamedStatement stmt = new NamedStatement(conn, SELECT_GENRE);
			NamedResultSet result = stmt.executeQuery();
			while ( result.next() ) {
				loadGenreFromResultSet(result);
			}
		}
	}
}
