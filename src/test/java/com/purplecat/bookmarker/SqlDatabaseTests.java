package com.purplecat.bookmarker;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.purplecat.bookmarker.services.databases.DatabaseException;
import com.purplecat.bookmarker.sql.ConnectionManager;
import com.purplecat.bookmarker.modules.TestDatabaseModule;

public class SqlDatabaseTests extends DatabaseConnectorTestBase {
	
	static ConnectionManager _connectionManager;
	static int ID_COUNT = 0;

	@BeforeClass
	public static void setUpBeforeTest() throws Exception {
		Injector injector = Guice.createInjector(new TestDatabaseModule());
		_connectionManager = injector.getInstance(ConnectionManager.class);
	}

	@Test
	public void testRequireOpenConnection() {
		try {
			_connectionManager.getConnection();
			Assert.fail("No exception: should have thrown a DatabaseException exception");
		} catch (DatabaseException e) {
		
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Wrong exception: should have thrown a DatabaseException exception");
		}
	}

	@Test
	public void testOpenConnection() {
		try {
			_connectionManager.open();
			Connection conn = _connectionManager.getConnection();
			Assert.assertNotNull("Connection is null", conn);
			loadGenres(100, conn);
			_connectionManager.close();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred");
		}
		try {
			_connectionManager.getConnection();
			Assert.fail("No exception: should have thrown a DatabaseException exception");
		} catch (DatabaseException e) {
		
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Wrong exception: should have thrown a DatabaseException exception");
		}
	}

	@Test
	public void testThreadSafety() {
		OpenLongConnection conn1 = new OpenLongConnection();
		
		new Thread(conn1).start();
		//new Thread(conn2).start();
		
		int _id = ID_COUNT++;
		System.out.println(_id + " starting");
		try {
			_connectionManager.open();
			Connection conn = _connectionManager.getConnection();
			loadGenres(_id, conn);
			_connectionManager.close();
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(_id + " exception occurred");
		}
		System.out.println(_id + " done");
		synchronized(this) {
			try {
				this.wait(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} //one second
		}
		if ( conn1._failureMessage != null ) {
			Assert.fail(conn1._failureMessage);
		}
	}
	
	public class OpenLongConnection implements Runnable {
		public int _id = ID_COUNT++;
		public String _failureMessage = null;
		
		@Override
		public void run() {
			System.out.println(_id + " starting");
			try {
				_connectionManager.open();
				Connection conn = _connectionManager.getConnection();
				synchronized(this) {
					this.wait(1000); //one second
				}
				loadGenres(_id, conn);
				_connectionManager.close();
			} catch (Exception e) {
				e.printStackTrace();
				_failureMessage = _id + " exception occurred";
			}
			System.out.println(_id + " done");
		}
	}
	
	static void loadGenres(long id, Connection conn) throws SQLException {
		Assert.assertNotNull(id + " connection is null", conn);
		System.out.println(id + " connection aquired");
		Statement stmt = conn.createStatement();
		Assert.assertNotNull("Statement is null", stmt);
		ResultSet result = stmt.executeQuery("SELECT GenName FROM GENRE");
		List<String> genres = new LinkedList<String>();
		while ( result.next() ) {
			genres.add(result.getString(1));
		}
		System.out.println(id + " genres found " + genres.size());
		Assert.assertTrue("no genres found", genres.size() > 0);		
	}

}
