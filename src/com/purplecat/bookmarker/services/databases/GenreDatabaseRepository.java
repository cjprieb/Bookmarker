package com.purplecat.bookmarker.services.databases;

import java.sql.Connection;
import java.sql.DriverManager;
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
import com.google.inject.name.Named;
import com.purplecat.bookmarker.models.Genre;
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
	public final String _connectionPath;
	
	private Map<Long, Genre> _genreCache;
	 
	@Inject
	public GenreDatabaseRepository(ILoggingService logger, @Named("JDBC URL") String dbPath) {
		_logging = logger;
		_connectionPath = dbPath;
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
	public List<Genre> query() {
		List<Genre> list = new LinkedList<Genre>();
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			Statement stmt = conn.createStatement();
			NamedResultSet result = new NamedResultSet(stmt.executeQuery(SELECT_GENRE));
			while ( result.next() ) {
				list.add(loadGenreFromResultSet(result));
			}
		} catch (SQLException e) {
			_logging.error(TAG, "Query failed", e);
		} 
		return list;
	}
	
	@Override
	public List<Genre> queryByMediaId(long id) {
		List<Genre> list = new LinkedList<Genre>();
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			list = queryByMediaId(conn, id);
		} catch (SQLException e) {
			_logging.error(TAG, "Query by id failed", e);
		} 
		return list;
	}
	
	@Override
	public boolean updateGenreList(List<Genre> list, long mediaId) {
		boolean bSuccess = false;
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			bSuccess = updateGenreList(conn, list, mediaId);
		} catch (SQLException e) {
			_logging.error(TAG, "Updating genre list failed", e);
		} 
		return bSuccess;
	}
	
	@Override
	public Genre find(String text) {
		if ( _genreCache.size() == 0 ) {
			//assume has not been loaded yet if empty
			query();
		}
		for ( Genre existing : _genreCache.values() ) {
			if ( existing._name.equalsIgnoreCase(text) || existing._altName.equalsIgnoreCase(text) ) {
				return existing;
			}
		}
		Genre genre = new Genre();
		genre._name = text;
		return genre;
	}
	
	public List<Genre> queryByMediaId(Connection conn, long id) throws SQLException {
		List<Genre> list = new LinkedList<Genre>();
		NamedStatement stmt = new NamedStatement(conn, SELECT_MEDIA_GENRE);
		stmt.setLong("@mediaId", id);
		NamedResultSet result = stmt.executeQuery();
		while ( result.next() ) {
			list.add(loadGenreFromResultSet(result));
		}
		return list;		
	}
	
	public Map<Long, Set<Genre>> loadAllMediaGenres() {
		Map<Long, Set<Genre>> list = new HashMap<Long, Set<Genre>>();
		try (Connection conn = DriverManager.getConnection(_connectionPath)) {
			list = loadAllMediaGenres(conn);
		} catch (SQLException e) {
			_logging.error(TAG, "Query by id failed", e);
		} 
		return list;
	}
	
	public Map<Long, Set<Genre>> loadAllMediaGenres(Connection conn) throws SQLException {
		//first make sure all genres are loaded:
		NamedStatement stmt = new NamedStatement(conn, SELECT_GENRE);
		NamedResultSet result = stmt.executeQuery();
		while ( result.next() ) {
			loadGenreFromResultSet(result);
		}

		Map<Long, Set<Genre>> map = new HashMap<Long, Set<Genre>>();
		long mediaId = -1;
		Set<Genre> genreList = null;
		
		stmt = new NamedStatement(conn, "SELECT GenMedia_ID, GenGenre_ID FROM GenreMap");
		result = stmt.executeQuery();
		while ( result.next() ) {
			mediaId = result.getLong("GenMedia_ID");
			genreList = map.get(mediaId);
			if ( genreList == null ) {
				genreList = new HashSet<Genre>();
				map.put(mediaId, genreList);
			}
			genreList.add(_genreCache.get(result.getLong("GenGenre_ID")));
		}
		return map;		
	}
	
	public boolean updateGenreList(Connection conn, Collection<Genre> list, long mediaId) throws SQLException {
		Set<Long> existingIds = new HashSet<Long>();
		NamedStatement stmt = new NamedStatement(conn, "SELECT GenGenre_ID FROM GenreMap WHERE GenMedia_ID = " + mediaId);
		NamedResultSet result = stmt.executeQuery();
		while ( result.next() ) {
			existingIds.add(result.getLong("GenGenre_ID"));
		}

		Set<Long> includeIds = new HashSet<Long>();
		Set<Genre> insertItems = new HashSet<Genre>();
		for ( Genre genre : list ) {
			if ( genre._id == 0 ) {
				insertItems.add(genre);
			}
			else if ( !existingIds.contains(genre._id) ) {
				includeIds.add(genre._id);
			}
		}
		
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

		stmt = new NamedStatement(conn, "INSERT INTO Genre (GenName, GenAltName, GenInclude) VALUES (@name, @alt, @include)");
		for ( Genre genre : insertItems ) {
			stmt.setString("@name", genre._name);
			stmt.setString("@alt", genre._altName != null ? genre._altName : "");
			stmt.setBoolean("@include", true);
			genre._id = stmt.executeInsert();
			_genreCache.put(genre._id, genre);
			includeIds.add(genre._id);
		}

		stmt = new NamedStatement(conn, "INSERT INTO GenreMap (GenGenre_ID, GenMedia_ID) VALUES (@genreId, @mediaId)");
		for ( Long id : includeIds ) {
			stmt.setLong("@genreId", id);
			stmt.setLong("@mediaId", mediaId);
			stmt.addBatch();
		}
		stmt.executeBatchUpdate();

		String deleteStr = String.format("DELETE FROM GenreMap WHERE GenMedia_ID = %d AND GenGenre_ID in (%s)", 
				mediaId,
				DBUtils.formatIdList(removeIds));
		stmt = new NamedStatement(conn, deleteStr);
		stmt.execute();
		
		return true;		
	}

}
