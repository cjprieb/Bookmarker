package com.purplecat.bookmarker.services.databases;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.purplecat.bookmarker.extensions.StoryStateExt;
import com.purplecat.bookmarker.models.Folder;
import com.purplecat.bookmarker.services.IFolderRepository;
import com.purplecat.bookmarker.sql.ConnectionManager;
import com.purplecat.bookmarker.sql.NamedResultSet;
import com.purplecat.commons.logs.ILoggingService;

@Singleton
public class FolderDatabaseRepository implements IFolderRepository {
	
	static String TAG = "FolderDatabaseRepository";
	
	static String SELECT_FOLDER = "SELECT _id, FldrName _name, FldrType _storyState"
			+ " FROM Folder ORDER BY _name";
	
	public final ILoggingService _logging;
	public final ConnectionManager _connectionManager;
	 
	@Inject
	public FolderDatabaseRepository(ILoggingService logger, ConnectionManager mgr) {
		_logging = logger;
		_connectionManager = mgr;
	}
	
	/**
	 * Assumes the SELECT_FOLDER query was used
	 * @param result
	 * @return
	 * @throws SQLException 
	 */
	private Folder loadFolderFromResultSet(NamedResultSet result) throws SQLException {
		Folder folder = new Folder();
		folder._id = result.getLong("_id");
		folder._name = result.getString("_name");
		folder._storyState = StoryStateExt.parse(result.getInt("_storyState"));
		return folder;
	}
	
	@Override
	public List<Folder> query() throws DatabaseException {
		List<Folder> list = new LinkedList<Folder>();
		try {
			_connectionManager.open();
			Connection conn = _connectionManager.getConnection();
			Statement stmt = conn.createStatement();
			NamedResultSet result = new NamedResultSet(stmt.executeQuery(SELECT_FOLDER));
			while ( result.next() ) {
				list.add(loadFolderFromResultSet(result));
			}
		} catch (SQLException e) {
			throw new DatabaseException("query failed", SELECT_FOLDER, e);
		} 
		finally {
			_connectionManager.close();
		}
		return list;
	}
}
