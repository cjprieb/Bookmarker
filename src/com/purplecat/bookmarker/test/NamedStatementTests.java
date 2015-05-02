package com.purplecat.bookmarker.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.name.Named;
import com.purplecat.bookmarker.sql.NamedStatement;
import com.purplecat.bookmarker.test.modules.TestDatabaseModule;

public class NamedStatementTests extends DatabaseConnectorTests {
	
	@Inject @Named("JDBC URL") String _connection;
	
	@Before
	public void beforeTest() {
		Injector injector = Guice.createInjector(new TestDatabaseModule());		
		injector.injectMembers(this);
		
		System.out.println("connecting to: " + _connection);
	}

	@Test
	public void testGetFormattedStringSimple() {
		long id = 3;
		try (Connection conn = DriverManager.getConnection(_connection)) {
			NamedStatement stmt = new NamedStatement(conn, "SELECT _id FROM Media WHERE _id = @id");
			stmt.setLong("@id", id);
			String sql = stmt.getFormattedString();
			assertEquals("SELECT _id FROM Media WHERE _id = ?", sql);
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException: " + e.getMessage());
		} 
	}

	@Test
	public void testGetFormattedStringComplex() {
		try (Connection conn = DriverManager.getConnection(_connection)) {
			NamedStatement stmt = new NamedStatement(conn, "SELECT _id FROM Media " +
															"INNER JOIN Author on Author._id = Media.SvdAuthor " +
															"WHERE AuthorFullName like @name AND SvdStoryState = @state");
			String sql = stmt.getFormattedString();
			assertEquals("SELECT _id FROM Media " +
					"INNER JOIN Author on Author._id = Media.SvdAuthor " +
					"WHERE AuthorFullName like ? AND SvdStoryState = ?", sql);
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException: " + e.getMessage());
		} 
	}

	@Test
	public void testExecuteQuery() {
		long id = 4;
		try (Connection conn = DriverManager.getConnection(_connection)) {
			NamedStatement stmt = new NamedStatement(conn, "SELECT _id FROM Media WHERE _id = @id");
			stmt.setLong("@id", id);
			stmt.executeQuery();
			//for now, just validating no exception
		} catch (SQLException e) {
			e.printStackTrace();
			fail("SQLException: " + e.getMessage());
		} 
	}

}
