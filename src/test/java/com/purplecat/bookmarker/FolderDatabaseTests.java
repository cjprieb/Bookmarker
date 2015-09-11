package com.purplecat.bookmarker;

import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.models.Folder;
import com.purplecat.bookmarker.services.databases.DatabaseException;
import com.purplecat.bookmarker.services.databases.FolderDatabaseRepository;
import com.purplecat.bookmarker.sql.ConnectionManager;
import com.purplecat.bookmarker.sql.IConnectionManager;
import com.purplecat.bookmarker.modules.TestDatabaseModule;

public class FolderDatabaseTests extends DatabaseConnectorTestBase {
	private static IConnectionManager _connectionManager;
	private static FolderDatabaseRepository _folderDatabase;
	private static List<Folder> _randomFolders;

	@BeforeClass
	public static void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new TestDatabaseModule());

		_folderDatabase = injector.getInstance(FolderDatabaseRepository.class);
		_connectionManager = injector.getInstance(ConnectionManager.class);
		//_database._log = new ConsoleLog();
	}
	
	@Before
	public void openConnection() {
		try {
			_connectionManager.open();
		} 
		catch (DatabaseException e) {
			e.printStackTrace();
			Assert.fail("Database connection failed");
			_connectionManager.close();
		}		
	}
	
	@After
	public void closeConnection() {
		_connectionManager.close();	
	}

	@Test
	public void queryTest() {
		try {
			_randomFolders = _folderDatabase.query();
			Assert.assertNotNull("List is null", _randomFolders);
			Assert.assertTrue("List has no elements", _randomFolders.size() > 0);
		} 
		catch (DatabaseException e) {
			e.printStackTrace();
			Assert.fail("Database connection failed");
		}
	}
}
